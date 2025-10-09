package algeo.poisson;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class PoissonMask {
    private PoissonMask() {}

    /**
     * Membuat mask berbentuk tertutup dari titik kontrol dengan kurva Bézier kubik.
     */
    public static BufferedImage makeMaskFromBezier(int width, int height, List<double[]> controlPoints) {
        BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = mask.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));

        // Gambar kurva untuk setiap 4 titik
        for (int i = 0; i + 3 < controlPoints.size(); i += 3) {
            double[] P0 = controlPoints.get(i);
            double[] P1 = controlPoints.get(i + 1);
            double[] P2 = controlPoints.get(i + 2);
            double[] P3 = controlPoints.get(i + 3);

            int lastX = (int) P0[0];
            int lastY = (int) P0[1];

            for (double t = 0; t <= 1.0; t += 0.01) {
                double x = Math.pow(1 - t, 3) * P0[0] +
                           3 * Math.pow(1 - t, 2) * t * P1[0] +
                           3 * (1 - t) * t * t * P2[0] +
                           Math.pow(t, 3) * P3[0];

                double y = Math.pow(1 - t, 3) * P0[1] +
                           3 * Math.pow(1 - t, 2) * t * P1[1] +
                           3 * (1 - t) * t * t * P2[1] +
                           Math.pow(t, 3) * P3[1];

                g.drawLine(lastX, lastY, (int) x, (int) y);
                lastX = (int) x;
                lastY = (int) y;
            }
        }

        g.dispose();

        // isi area dalamnya (flood fill)
        fillInterior(mask, width / 2, height / 2);

        return mask;
    }

    /** Flood-fill area tertutup */
    private static void fillInterior(BufferedImage img, int sx, int sy) {
        int w = img.getWidth();
        int h = img.getHeight();
        int target = img.getRGB(sx, sy);
        int replacement = Color.WHITE.getRGB();
        if (target == replacement) return;

        java.util.Stack<int[]> stack = new java.util.Stack<>();
        stack.push(new int[]{sx, sy});

        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int x = p[0], y = p[1];
            if (x < 0 || y < 0 || x >= w || y >= h) continue;
            if (img.getRGB(x, y) != target) continue;

            img.setRGB(x, y, replacement);
            stack.push(new int[]{x + 1, y});
            stack.push(new int[]{x - 1, y});
            stack.push(new int[]{x, y + 1});
            stack.push(new int[]{x, y - 1});
        }
    }

    public static BufferedImage makeMaskInteractive(String controlFile, int width, int height) throws Exception {
        List<double[]> controlPoints = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(controlFile))) {
            while (sc.hasNextDouble()) {
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                controlPoints.add(new double[]{x, y});
            }
        }
        return makeMaskFromBezier(width, height, controlPoints);
    }
}
