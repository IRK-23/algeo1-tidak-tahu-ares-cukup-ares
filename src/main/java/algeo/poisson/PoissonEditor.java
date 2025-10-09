package algeo.poisson;

import java.awt.Color;
import java.awt.image.BufferedImage;

import algeo.core.Matrix;

/**
 * PoissonEditor:
 * - Menangani proses blending utama antara source dan target menggunakan solver Jacobi.
 * - Menggunakan mask untuk menentukan area yang di-blend.
 */
public final class PoissonEditor {
    private PoissonEditor() {}

    /**
     * Melakukan blending Poisson antara source dan target berdasarkan mask.
     * @param src  Gambar sumber
     * @param tgt  Gambar target
     * @param mask Matriks mask (1 = area blending, 0 = area tidak diubah)
     * @param offsetX posisi peletakan src di dalam target
     * @param offsetY posisi peletakan src di dalam target
     * @return hasil blending sebagai BufferedImage
     */
    public static BufferedImage blend(BufferedImage src, BufferedImage tgt, Matrix mask, int offsetX, int offsetY) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage result = deepCopy(tgt);

        // Channel RGB diselesaikan terpisah
        Matrix[] srcChannels = extractRGB(src);
        Matrix[] tgtChannels = extractRGB(tgt);

        for (int c = 0; c < 3; c++) {
            Matrix b = new Matrix(h, w);
            Matrix init = new Matrix(h, w);

            // Bangun RHS b berdasarkan gradien sumber
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (mask.get(y, x) > 0.5) {
                        double center = srcChannels[c].get(y, x);
                        double sumNeigh = 0.0;
                        int count = 0;

                        if (x > 0) { sumNeigh += srcChannels[c].get(y, x - 1); count++; }
                        if (x < w - 1) { sumNeigh += srcChannels[c].get(y, x + 1); count++; }
                        if (y > 0) { sumNeigh += srcChannels[c].get(y - 1, x); count++; }
                        if (y < h - 1) { sumNeigh += srcChannels[c].get(y + 1, x); count++; }

                        // Laplacian sumber
                        double lap = count * center - sumNeigh;

                        // Jika neighbor di luar mask → ambil dari target
                        double boundarySum = 0.0;
                        if (x > 0 && mask.get(y, x - 1) < 0.5)
                            boundarySum += tgtChannels[c].get(offsetY + y, offsetX + x - 1);
                        if (x < w - 1 && mask.get(y, x + 1) < 0.5)
                            boundarySum += tgtChannels[c].get(offsetY + y, offsetX + x + 1);
                        if (y > 0 && mask.get(y - 1, x) < 0.5)
                            boundarySum += tgtChannels[c].get(offsetY + y - 1, offsetX + x);
                        if (y < h - 1 && mask.get(y + 1, x) < 0.5)
                            boundarySum += tgtChannels[c].get(offsetY + y + 1, offsetX + x);

                        b.set(y, x, lap + boundarySum);
                        init.set(y, x, tgtChannels[c].get(offsetY + y, offsetX + x));
                    }
                }
            }
            double tol = 1e-5;
            // Solve Poisson menggunakan Jacobi
            Matrix solved = JacobiSolver.solvePoisson(b, mask,init, tol, 5000);

            // Tempelkan hasilnya ke hasil akhir
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (mask.get(y, x) > 0.5) {
                        double val = solved.get(y, x);
                        val = Math.max(0, Math.min(255, val)); // clamp
                        int rgb = result.getRGB(offsetX + x, offsetY + y);
                        Color color = new Color(rgb);

                        int r = color.getRed();
                        int g = color.getGreen();
                        int bcol = color.getBlue();
                        
                        if (c == 0) r = (int) val;
                        else if (c == 1) g = (int) val;
                        else bcol = (int) val;

                        result.setRGB(offsetX + x, offsetY + y, new Color(r, g, bcol).getRGB());
                    }
                }
            }
        }

        return result;
    }

    // ─────────────────────────────
    // Helper methods
    // ─────────────────────────────

    private static Matrix[] extractRGB(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        Matrix[] channels = new Matrix[3];
        for (int i = 0; i < 3; i++) channels[i] = new Matrix(h, w);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                channels[0].set(y, x, c.getRed());
                channels[1].set(y, x, c.getGreen());
                channels[2].set(y, x, c.getBlue());
            }
        }
        return channels;
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                copy.setRGB(x, y, bi.getRGB(x, y));
            }
        }
        return copy;
    }
}
