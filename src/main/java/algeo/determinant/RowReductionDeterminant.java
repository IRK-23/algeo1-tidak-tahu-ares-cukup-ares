package algeo.determinant;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;

/**
 * Determinan menggunakan OBE
 */
public final class RowReductionDeterminant {
    private RowReductionDeterminant() {}

    public static double of(Matrix A) {
        return MatrixOps.determinantOBE(A);
    }

    public static double of(Matrix A, boolean pivoting, double eps) {
        return MatrixOps.determinantOBE(A, pivoting, eps);
    }


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
        System.out.println("Determinan (OBE) = " + NumberFmt.format3(det));
    }
}
