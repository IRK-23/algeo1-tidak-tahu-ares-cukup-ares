package algeo.determinant;

import java.io.IOException;
import java.util.Scanner;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;

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
    private static final Scanner sc = new Scanner(System.in);

    public static void run() {
        System.out.println("\n=== Determinan (Metode Kofaktor) ===");

        UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
        Matrix A = null;
        while (A == null) {
            if (choice == UiPrompts.InputChoice.MANUAL) {
                A = MatrixIO.inputMatrix(sc);
            } else {
                String path = UiPrompts.askPath(sc, "Masukkan path file determinan (.txt): ");
                try {
                    A = MatrixIO.readDeterminantFromFile(path);
                    System.out.println("File berhasil dibaca: " + A.rows() + "x" + A.cols());
                } catch (IOException | IllegalArgumentException ex) {
                    System.out.println("Gagal membaca path: " + ex.getMessage());
                    boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
                    if (!retry) {
                        boolean sw = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
                        if (sw) choice = UiPrompts.InputChoice.MANUAL; else { System.out.println("Operasi dibatalkan."); return; }
                    }
                }
            }
        }

        double det = CofactorDeterminant.of(A);
        String nl = System.lineSeparator();
        StringBuilder out = new StringBuilder();
        out.append("Metode pencarian determinan: Kofaktor").append(nl).append(nl);
        out.append("Input yang digunakan:").append(nl).append(A).append(nl);
        out.append("Hasil determinan: ").append(NumberFmt.format3(det)).append(nl);

        System.out.println("\nDet(A) = " + NumberFmt.format3(det));
        ResultSaver.maybeSaveText(sc, "det_kofaktor", "Hasil Determinan – Metode Kofaktor", out.toString());
    }
}
