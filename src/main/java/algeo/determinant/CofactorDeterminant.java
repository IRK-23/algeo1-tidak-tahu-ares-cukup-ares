package algeo.determinant;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;

/**
 * Determinan menggunakan ekspansi kofaktor
 */
public final class CofactorDeterminant {
    private CofactorDeterminant() {}

    public static double of(Matrix A) {
        return of(A, MatrixOps.EPS);
    }

    public static double of(Matrix A, double eps) {
        if (A == null) throw new IllegalArgumentException("Matriks tidak boleh kosong");
        if (!A.isSquare()) throw new IllegalArgumentException("Matriks harus merupakan matriks persegi");
        return detRecursive(A, eps);
    }

    private static double detRecursive(Matrix A, double eps) {
        int n = A.rows();
        if (n == 1) return A.get(0, 0);
        if (n == 2) return A.get(0,0) * A.get(1,1) - A.get(0,1) * A.get(1,0);

        double det = 0.0;
        for (int col = 0; col < n; col++) {
            double[][] minor = new double[n-1][n-1];
            for (int i = 1; i < n; i++) {
                int mj = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    minor[i-1][mj++] = A.get(i, j);
                }
            }
            Matrix m = new Matrix(minor);
            double cofactor = ((col % 2) == 0 ? 1 : -1) * A.get(0, col) * detRecursive(m, eps);
            det += cofactor;
        }
        if (Math.abs(det) < eps) det = 0.0;
        return det;
    }

    /** CLI helper: input matrix dulu lalu tampilkan determinan (kofaktor). */
    public static void run() {
        Matrix M = MatrixIO.inputMatrix();
        if (M == null) {
            System.out.println("Input dibatalkan.");
            return;
        }
        if (!M.isSquare()) {
            System.out.println("Matriks harus persegi untuk menghitung determinan.");
            return;
        }
        double det = of(M);
        System.out.println("Determinan (kofaktor) = " + NumberFmt.format3(det));
    }
}
