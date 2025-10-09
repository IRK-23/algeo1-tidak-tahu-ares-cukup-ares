package algeo.poisson;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;

public final class PoissonBlend {
    private PoissonBlend() {}

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        Scanner in = new Scanner(System.in);

        System.out.print("Masukkan path gambar sumber: ");
        String srcPath = in.nextLine().trim();
        BufferedImage src = ImageIO.read(new File(srcPath));

        System.out.print("Masukkan path gambar target: ");
        String tgtPath = in.nextLine().trim();
        BufferedImage tgt = ImageIO.read(new File(tgtPath));

        System.out.print("Gunakan mask Bézier otomatis? (y/n): ");
        boolean useBezier = in.nextLine().trim().equalsIgnoreCase("y");

        BufferedImage mask;
        if (useBezier) {
            System.out.print("Masukkan file titik kontrol (format: x y per baris): ");
            String controlFile = in.nextLine().trim();
            mask = PoissonMask.makeMaskInteractive(controlFile, src.getWidth(), src.getHeight());
            ImageIO.write(mask, "png", new File("mask_generated.png"));
            System.out.println("✅ Mask Bézier disimpan ke mask_generated.png");
        } else {
            System.out.print("Masukkan path mask (hitam-putih): ");
            String maskPath = in.nextLine().trim();
            mask = ImageIO.read(new File(maskPath));
        }

        System.out.print("Masukkan offset X dan Y (contoh: 50 80): ");
        int offsetX = in.nextInt();
        int offsetY = in.nextInt();

        System.out.println("🔄 Melakukan blending...");
        BufferedImage blended = poissonBlend(src, tgt, mask, offsetX, offsetY);
        ImageIO.write(blended, "png", new File("blended_result.png"));
        System.out.println("✅ Selesai! Hasil disimpan di blended_result.png");
    }

    /**
     * Fungsi utama untuk melakukan Poisson blending.
     * Gunakan perbedaan gradien antara source dan target untuk menjaga tekstur halus.
     */
    public static BufferedImage poissonBlend(BufferedImage src, BufferedImage tgt, BufferedImage mask, int offsetX, int offsetY) {
        int width = tgt.getWidth();
        int height = tgt.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Copy target dulu ke hasil
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setRGB(x, y, tgt.getRGB(x, y));
            }
        }

        // Loop untuk area masked
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int maskVal = new Color(mask.getRGB(x, y)).getRed();
                if (maskVal < 128) continue; // bukan area yang ingin di-blend

                int tx = x + offsetX;
                int ty = y + offsetY;
                if (tx < 1 || ty < 1 || tx >= width - 1 || ty >= height - 1) continue;

                Color srcColor = new Color(src.getRGB(x, y));
                Color tgtColor = new Color(tgt.getRGB(tx, ty));

                // Ambil tetangga (gradien)
                int sx1 = Math.max(x - 1, 0), sx2 = Math.min(x + 1, src.getWidth() - 1);
                int sy1 = Math.max(y - 1, 0), sy2 = Math.min(y + 1, src.getHeight() - 1);

                Color srcLeft = new Color(src.getRGB(sx1, y));
                Color srcRight = new Color(src.getRGB(sx2, y));
                Color srcUp = new Color(src.getRGB(x, sy1));
                Color srcDown = new Color(src.getRGB(x, sy2));

                // Hitung gradien Laplacian (approximation)
                int lapR = 4 * srcColor.getRed() - srcLeft.getRed() - srcRight.getRed() - srcUp.getRed() - srcDown.getRed();
                int lapG = 4 * srcColor.getGreen() - srcLeft.getGreen() - srcRight.getGreen() - srcUp.getGreen() - srcDown.getGreen();
                int lapB = 4 * srcColor.getBlue() - srcLeft.getBlue() - srcRight.getBlue() - srcUp.getBlue() - srcDown.getBlue();

                // Kombinasi dengan warna target untuk menjaga integrasi halus
                int blendedR = clamp(tgtColor.getRed() + lapR / 8);
                int blendedG = clamp(tgtColor.getGreen() + lapG / 8);
                int blendedB = clamp(tgtColor.getBlue() + lapB / 8);

                Color blendedColor = new Color(blendedR, blendedG, blendedB);
                result.setRGB(tx, ty, blendedColor.getRGB());
            }
        }

        return result;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
