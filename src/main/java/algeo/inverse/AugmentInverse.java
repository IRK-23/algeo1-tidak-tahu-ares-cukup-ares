package algeo.inverse;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;

/**
 * Inverse dengan matriks augmented [A | I] kemudian RREF → [I | A^-1].
 */
public final class AugmentInverse {
    private AugmentInverse() {}

    public static Matrix inverse(Matrix A) {
        return inverse(A, true, MatrixOps.EPS);
    }

    public static Matrix inverse(Matrix A, boolean pivoting, double eps) {
        if (A == null) throw new IllegalArgumentException("Matrix A tidak boleh kosong");
        if (!A.isSquare()) throw new IllegalArgumentException("Inverse hanya untuk matriks persegi");

        double det = MatrixOps.determinantOBE(A, pivoting, eps);
        if (Math.abs(det) <= eps) throw new IllegalArgumentException("Matriks singular, tidak memiliki inverse");

        int n = A.rows();
        Matrix I = Matrix.identity(n);
        Matrix aug = A.copy().augment(I);
        MatrixOps.rref(aug, pivoting, eps);
        Matrix inv = aug.submatrix(0, n-1, n, 2*n-1);
        return inv;
    }

    /** CLI helper: input matrix dulu lalu tampilkan inverse (augment method). */
    public static void run() {
        Matrix A = MatrixIO.inputMatrix();
        if (A == null) {
            System.out.println("Input dibatalkan.");
            return;
        }
        if (!A.isSquare()) {
            System.out.println("Matriks harus persegi untuk inverse.");
            return;
        }
        try {
            Matrix inv = inverse(A);
            System.out.println("Inverse (augment) =");
            for (int i = 0; i < inv.rows(); i++) {
                for (int j = 0; j < inv.cols(); j++) {
                    System.out.print(NumberFmt.format3(inv.get(i, j)) + (j + 1 < inv.cols() ? " " : ""));
                }
                System.out.println();
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Tidak dapat menghitung inverse: " + ex.getMessage());
        }
    }
}
