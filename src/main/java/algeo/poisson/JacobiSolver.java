package algeo.poisson;

import algeo.core.Matrix;

public final class JacobiSolver {
    private JacobiSolver() {}

    /**
     * Jacobi solver untuk sistem Poisson dengan masking.
     * @param b - matrix sumber (misalnya perbedaan luminansi)
     * @param mask - matrix boolean (1.0 jika termasuk area blending, 0.0 jika boundary)
     * @param initial - tebakan awal
     * @param tol - toleransi konvergensi
     * @param maxIter - iterasi maksimum
     */
    public static Matrix solvePoisson(Matrix b, Matrix mask, Matrix initial, double tol, int maxIter) {
        if (b == null || initial == null || mask == null)
            throw new IllegalArgumentException("b, mask, atau initial tidak boleh null");

        int h = b.rows();
        int w = b.cols();

        if (mask.rows() != h || mask.cols() != w || initial.rows() != h || initial.cols() != w)
            throw new IllegalArgumentException("Ukuran b, mask, dan initial harus sama");

        Matrix x = initial.copy();
        Matrix xnew = new Matrix(h, w);

        for (int iter = 0; iter < maxIter; iter++) {
            double maxDiff = 0.0;

            for (int y = 0; y < h; y++) {
                for (int x0 = 0; x0 < w; x0++) {
                    if (mask.get(y, x0) == 0.0) {
                        xnew.set(y, x0, initial.get(y, x0));
                        continue;
                    }

                    double sumNeigh = 0.0;
                    int count = 0;

                    if (x0 > 0) { sumNeigh += x.get(y, x0 - 1); count++; }
                    if (x0 < w - 1) { sumNeigh += x.get(y, x0 + 1); count++; }
                    if (y > 0) { sumNeigh += x.get(y - 1, x0); count++; }
                    if (y < h - 1) { sumNeigh += x.get(y + 1, x0); count++; }

                    double val = (b.get(y, x0) + sumNeigh) / count;
                    xnew.set(y, x0, val);

                    double diff = Math.abs(val - x.get(y, x0));
                    if (diff > maxDiff) maxDiff = diff;
                }
            }

            Matrix tmp = x;
            x = xnew;
            xnew = tmp;

            if (maxDiff <= tol) break;
        }

        return x;
    }
}
