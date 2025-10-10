package algeo.determinant;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;

/** Determinan menggunakan ekspansi kofaktor */
public final class CofactorDeterminant {
    private CofactorDeterminant() {}

    public static double of(Matrix A) {
        return of(A, MatrixOps.EPS);
    }

    public static double of(Matrix A, double eps) {
        if (A == null) throw new IllegalArgumentException("Matriks tidak boleh kosong");
        if (!A.isSquare())
            throw new IllegalArgumentException("Matriks harus merupakan matriks persegi");

        System.out.println("\n--- Perhitungan Determinan (Kofaktor) ---");
        System.out.println("Matriks awal:");
        System.out.println(A);

        StringBuilder equation = new StringBuilder("det(A) = ");
        double result = detRecursive(A, eps, equation);
        
        System.out.println("\n" + equation.toString() + " = " + NumberFmt.format3(result));

        if (Math.abs(result) < eps) {
            result = 0.0;
            System.out.println("Determinan = 0\n");
        } else {
            System.out.println("Determinan tidak nol, tetapi sangat kecil\n");
        }

        System.out.println("Hasil akhir: det(A) = " + NumberFmt.format3(result));
        return result;
    }

    private static double detRecursive(Matrix A, double eps, StringBuilder equation) {
        int n = A.rows();

        if (n == 1) {
            equation.append(NumberFmt.format3(A.get(0, 0)));
            return A.get(0, 0);
        }
        if (n == 2) {
            double det = A.get(0, 0) * A.get(1, 1) - A.get(0, 1) * A.get(1, 0);
            equation.append("(")
                    .append(NumberFmt.format3(A.get(0, 0))).append(" * ")
                    .append(NumberFmt.format3(A.get(1, 1))).append(" - ")
                    .append(NumberFmt.format3(A.get(0, 1))).append(" * ")
                    .append(NumberFmt.format3(A.get(1, 0))).append(")");
            if (Math.abs(det) < eps) det = 0.0;
            return det;
        }

        double det = 0.0;
        equation.append("[");
        for (int col = 0; col < n; col++) {
            double termSign = ((col % 2) == 0 ? 1 : -1);
            
            // Mencetak matriks minor untuk setiap tingkat rekursi
            System.out.println("Mengekspansi baris 1, kolom " + (col + 1));
            System.out.println("Matriks minor:");

            double[][] minor = new double[n - 1][n - 1];
            int minorRow = 0;
            for (int i = 1; i < n; i++) {
                int minorCol = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    minor[minorRow][minorCol++] = A.get(i, j);
                }
                minorRow++;
            }
            Matrix m = new Matrix(minor);
            System.out.println(m);

            if (col > 0) {
                equation.append((termSign > 0) ? " + " : " - ");
            } else if (termSign < 0) {
                equation.append("-");
            }
            
            equation.append(NumberFmt.format3(Math.abs(A.get(0, col)))).append(" * ");
            double subDet = detRecursive(m, eps, equation);
            det += termSign * A.get(0, col) * subDet;
        }
        equation.append("]");
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
        of(M);
    }
}
