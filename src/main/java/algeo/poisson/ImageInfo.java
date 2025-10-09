package algeo.poisson;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public final class ImageInfo {
    private ImageInfo() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java algeo.poisson.ImageInfo <pathA> <pathB>");
            System.out.println("Example paths:");
            System.out.println("C:\\Users\\Miguel\\OneDrive\\Documents\\ALGEO\\algeo1-tidak-tahu-ares-cukup-ares\\src\\test\\java\\image\\Mengenal-Simian-Line-yang-Hanya-Dimiliki-Sedikit-Orang.png");
            System.out.println("C:\\Users\\Miguel\\OneDrive\\Documents\\ALGEO\\algeo1-tidak-tahu-ares-cukup-ares\\src\\test\\java\\image\\anatomi-mulut-kenali-bagian-bagian-dan-fungsinya.png");
            return;
        }
        File fA = new File(args[0]);
        File fB = new File(args[1]);
        if (!fA.exists() || !fB.exists()) {
            System.out.println("One or both files do not exist.");
            return;
        }
        BufferedImage A = ImageIO.read(fA);
        BufferedImage B = ImageIO.read(fB);
        if (A == null || B == null) {
            System.out.println("Failed to read one or both images.");
            return;
        }
        int Aw = A.getWidth(), Ah = A.getHeight();
        int Bw = B.getWidth(), Bh = B.getHeight();
        System.out.printf("Image A: %s → %dx%d%n", fA.getName(), Aw, Ah);
        System.out.printf("Image B: %s → %dx%d%n", fB.getName(), Bw, Bh);
        System.out.println();

        int maxOffX = Aw - Bw;
        int maxOffY = Ah - Bh;
        System.out.println("Valid offsets must satisfy: offX >= 0, offY >= 0, offX <= Aw - Bw, offY <= Ah - Bh");
        System.out.printf("Maximum allowed offsets (offX, offY): (%d, %d)%n", maxOffX, maxOffY);
        System.out.println();
        System.out.println("Suggested offsets:");
        System.out.printf("  Top-left: (0, 0)%n");
        int centerX = (Aw - Bw) / 2;
        int centerY = (Ah - Bh) / 2;
        System.out.printf("  Center: (%d, %d)%n", Math.max(0, centerX), Math.max(0, centerY));
        System.out.printf("  Bottom-right: (%d, %d)%n", Math.max(0, maxOffX), Math.max(0, maxOffY));

        if (maxOffX < 0 || maxOffY < 0) {
            System.out.println();
            System.out.println("Note: B is larger than available space at any non-negative offset.");
            System.out.println("The program will auto-resize B to fit; you can use offsets >= 0 (e.g. 0,0 or center after resize).");
        }
    }
}