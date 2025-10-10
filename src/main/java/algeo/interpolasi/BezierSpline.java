package algeo.interpolasi;

import algeo.core.Matrix;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;
import algeo.io.UiPrompts;

import java.io.IOException;
import java.util.Scanner;

public class BezierSpline {

    private static final Scanner sc = new Scanner(System.in);

    /** Baca sampel (x y) sebagai Matrix via manual / file. */
    private static Matrix readSamples() {
        UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
        Matrix smp = null;

        while (smp == null) {
            if (choice == UiPrompts.InputChoice.MANUAL) {
                smp = Polinomial.inputTitikSampelManual();
            } else {
                String path = UiPrompts.askPath(sc, "Masukkan path file interpolasi (.txt): ");
                try {
                    smp = MatrixIO.readInterpolationFromFile(path); // 2 kolom: x y
                    System.out.println("File berhasil dibaca: " + smp.rows() + " titik sampel");
                } catch (IOException | IllegalArgumentException ex) {
                    System.out.println("Gagal membaca file: " + ex.getMessage());
                    boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
                    if (!retry) {
                        boolean sw = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
                        if (sw) choice = UiPrompts.InputChoice.MANUAL; else { System.out.println("Operasi dibatalkan."); return null; }
                    }
                }
            }
        }
        return smp;
    }

    /** Solver OBE sederhana untuk sistem tridiagonal 4-1-1 yang sudah dibentuk dalam augmented matrix. */
    public static double[] solveTridiagonal411(double[][] mat) {
        int n = mat.length;
        Matrix a = new Matrix(mat); // augmented matrix
        System.out.println("\n=== Mulai OBE Tridiagonal 4-1-1 ===");

        // Forward elimination
        for (int k = 0; k < n - 1; k++) {
            System.out.println("\nLangkah " + (k + 1) + ", memproses Kolom " + (k + 1));
            System.out.println("Sebelum eliminasi:");
            System.out.println(a);
            double factor = a.get(k + 1, k) / a.get(k, k);
            for (int j = k; j < n + 1; j++) {
                a.set(k + 1, j, a.get(k + 1, j) - factor * a.get(k, j));
            }
            System.out.println("Setelah eliminasi baris " + (k + 2) + ":");
            System.out.println(a);
        }

        System.out.println("\n=== Matriks setelah OBE (forward elimination selesai) ===");
        System.out.println(a);

        // Back-substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) sum += a.get(i, j) * x[j];
            x[i] = (a.get(i, n) - sum) / a.get(i, i);
        }
        System.out.println("\n=== OBE Selesai ===\n");
        return x;
    }

    public static void cubicBezierSpline() {
        System.out.println("\n=== Interpolasi Bézier Spline Kubik ===");

        Matrix smp = readSamples();
        if (smp == null) return;

        int n = smp.rows();
        if (n < 2) {
            System.out.println("Minimal 2 titik sampel diperlukan.");
            return;
        }

        // Ubah Matrix → array data[x,y]
        double[][] data = new double[n][2];
        for (int i = 0; i < n; i++) {
            data[i][0] = smp.get(i, 0);
            data[i][1] = smp.get(i, 1);
        }

        if (n == 2) {
            double h = data[1][0] - data[0][0];
            double deltaY = data[1][1] - data[0][1];
            double C11x = data[0][0] + h / 3.0;
            double C11y = data[0][1] + deltaY / 3.0;
            double C12x = data[1][0] - h / 3.0;
            double C12y = data[1][1] - deltaY / 3.0;
            System.out.println("\n--- Hasil Interpolasi Bézier Linear ---");
            System.out.println("Segmen 1 (dari P1 ke P2):");
            System.out.println("  P1: (" + NumberFmt.format3(data[0][0]) + ", " + NumberFmt.format3(data[0][1]) + ")");
            System.out.println("  C1,1: (" + NumberFmt.format3(C11x) + ", " + NumberFmt.format3(C11y) + ")");
            System.out.println("  C1,2: (" + NumberFmt.format3(C12x) + ", " + NumberFmt.format3(C12y) + ")");
            System.out.println("  P2: (" + NumberFmt.format3(data[1][0]) + ", " + NumberFmt.format3(data[1][1]) + ")");
            return;
        }

        int m = n - 1;

        // Sistem tridiagonal untuk kontrol titik C_{i,1} (komponen y)
        double[][] matY = new double[m][m + 1];
        for (int i = 0; i < m; i++) {
            if (i > 0) matY[i][i - 1] = 1.0;
            matY[i][i] = 4.0;
            if (i < m - 1) matY[i][i + 1] = 1.0;

            if (i == 0) matY[i][m] = 6 * data[1][1] - data[0][1];
            else if (i == m - 1) matY[i][m] = 6 * data[n - 1][1] - data[n - 2][1];
            else matY[i][m] = 6 * data[i + 1][1];
        }
        double[] B_y = solveTridiagonal411(matY);

        // Sistem tridiagonal untuk kontrol titik C_{i,1} (komponen x)
        double[][] matX = new double[m][m + 1];
        for (int i = 0; i < m; i++) {
            if (i > 0) matX[i][i - 1] = 1.0;
            matX[i][i] = 4.0;
            if (i < m - 1) matX[i][i + 1] = 1.0;

            if (i == 0) matX[i][m] = 6 * data[1][0] - data[0][0];
            else if (i == m - 1) matX[i][m] = 6 * data[n - 1][0] - data[n - 2][0];
            else matX[i][m] = 6 * data[i + 1][0];
        }
        double[] B_x = solveTridiagonal411(matX);

        // Hitung C_{i,2}
        double[] C2x = new double[m];
        double[] C2y = new double[m];
        for (int i = 0; i < m - 1; i++) {
            C2x[i] = 2 * data[i + 1][0] - B_x[i + 1];
            C2y[i] = 2 * data[i + 1][1] - B_y[i + 1];
        }
        C2x[m - 1] = (B_x[m - 1] + data[n - 1][0]) / 2.0;
        C2y[m - 1] = (B_y[m - 1] + data[n - 1][1]) / 2.0;

        // Tampilkan titik kontrol Bézier kubik per segmen
        System.out.println("\n--- Hasil Interpolasi Bézier Spline Kubik ---");
        for (int i = 0; i < m; i++) {
            System.out.println("\nSegmen " + (i + 1) + " (dari P" + (i + 1) + " ke P" + (i + 2) + "):");
            System.out.println("  P" + (i + 1) + ": (" + NumberFmt.format3(data[i][0]) + ", " + NumberFmt.format3(data[i][1]) + ")");
            System.out.println("  C" + (i + 1) + ",1: (" + NumberFmt.format3(B_x[i]) + ", " + NumberFmt.format3(B_y[i]) + ")");
            System.out.println("  C" + (i + 1) + ",2: (" + NumberFmt.format3(C2x[i]) + ", " + NumberFmt.format3(C2y[i]) + ")");
            System.out.println("  P" + (i + 2) + ": (" + NumberFmt.format3(data[i + 1][0]) + ", " + NumberFmt.format3(data[i + 1][1]) + ")");
        }
    }
}
