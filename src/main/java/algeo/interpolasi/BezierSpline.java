package algeo.interpolasi;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;

public class BezierSpline {
    
    public static double[][] inputTitikSampel() {
        try {
            return Polinomial.inputTitikSampel();
        } catch (Exception e) {
            System.out.println("Gagal membaca input titik sampel: " + e.getMessage());
            throw new RuntimeException("Gagal input data.");
        }
    }

    public static Matrix solveSLE(Matrix augmentedMatrix) {
        return MatrixOps.ref(augmentedMatrix);
    }

    public static void cubicBezierSpline() {
        double[][] data = inputTitikSampel();
        int n = data.length;

        if (n < 2) {
            System.out.println("Minimal 2 titik sampel diperlukan.");
            return;
        }
        
        // Hitung interval antara titik-titik x
        double[] h = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            h[i] = data[i + 1][0] - data[i][0];
        }

        // Kasus hanya 2 titik
        if (n == 2) {
            double M1x = 0.0, M1y = 0.0;
            double M2x = 0.0, M2y = 0.0;
            double hSegmen = h[0];

            double C11x = data[0][0] + (1.0 / 3.0) * M1x * hSegmen;
            double C11y = data[0][1] + (1.0 / 3.0) * M1y * hSegmen;
            double C12x = data[1][0] - (1.0 / 3.0) * M2x * hSegmen;
            double C12y = data[1][1] - (1.0 / 3.0) * M2y * hSegmen;
            
            System.out.println("\n--- Hasil Interpolasi Bézier Spline Kubik (1 Segmen) ---");
            System.out.println("Segmen 1 (dari P1 ke P2):");
            System.out.println("  Titik Kontrol P1: (" + NumberFmt.format3(data[0][0]) + ", " + NumberFmt.format3(data[0][1]) + ")");
            System.out.println("  Titik Kontrol C1,1: (" + NumberFmt.format3(C11x) + ", " + NumberFmt.format3(C11y) + ")");
            System.out.println("  Titik Kontrol C1,2: (" + NumberFmt.format3(C12x) + ", " + NumberFmt.format3(C12y) + ")");
            System.out.println("  Titik Kontrol P2: (" + NumberFmt.format3(data[1][0]) + ", " + NumberFmt.format3(data[1][1]) + ")");
            return;
        }

        // Untuk n >= 3, selesaikan sistem persamaan tridiagonal
        int numSlopes = n - 2;
        
        Matrix A = new Matrix(numSlopes, numSlopes);
        Matrix Rx = new Matrix(numSlopes, 1);
        Matrix Ry = new Matrix(numSlopes, 1);

        for (int i = 0; i < numSlopes; i++) {
            int row = i;
            int pointIndex = i + 1; // Titik P_{i+2} dalam data
            
            // Set elemen matriks tridiagonal
            if (i > 0) {
                A.set(row, i - 1, h[pointIndex - 1]);
            }
            A.set(row, i, 2.0 * (h[pointIndex - 1] + h[pointIndex]));
            if (i < numSlopes - 1) {
                A.set(row, i + 1, h[pointIndex]);
            }
            
            // Hitung RHS untuk x dan y
            double rhsX = 6.0 * (((data[pointIndex + 1][0] - data[pointIndex][0]) / h[pointIndex]) - 
                               ((data[pointIndex][0] - data[pointIndex - 1][0]) / h[pointIndex - 1]));
            double rhsY = 6.0 * (((data[pointIndex + 1][1] - data[pointIndex][1]) / h[pointIndex]) - 
                               ((data[pointIndex][1] - data[pointIndex - 1][1]) / h[pointIndex - 1]));
            
            Rx.set(row, 0, rhsX);
            Ry.set(row, 0, rhsY);
        }
        
        // Selesaikan SPL untuk Mx dan My (turunan kedua)
        Matrix AugmentedMx = A.augment(Rx);
        Matrix SolvedMx = solveSLE(AugmentedMx);
        Matrix AugmentedMy = A.augment(Ry);
        Matrix SolvedMy = solveSLE(AugmentedMy);
        
        // Ekstrak solusi M (turunan kedua)
        double[] Mx = new double[n];
        double[] My = new double[n];
        Mx[0] = 0.0; My[0] = 0.0; // Natural boundary
        Mx[n-1] = 0.0; My[n-1] = 0.0; // Natural boundary
        
        for (int i = 0; i < numSlopes; i++) {
            Mx[i + 1] = SolvedMx.get(i, SolvedMx.cols() - 1);
            My[i + 1] = SolvedMy.get(i, SolvedMy.cols() - 1);
        }
        
        // Hitung turunan pertama D dari M
        double[] Dx = new double[n];
        double[] Dy = new double[n];
        
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                Dx[i] = (data[1][0] - data[0][0]) / h[0] - h[0] * (2 * Mx[0] + Mx[1]) / 6.0;
                Dy[i] = (data[1][1] - data[0][1]) / h[0] - h[0] * (2 * My[0] + My[1]) / 6.0;
            } else if (i == n - 1) {
                Dx[i] = (data[n-1][0] - data[n-2][0]) / h[n-2] + h[n-2] * (Mx[n-2] + 2 * Mx[n-1]) / 6.0;
                Dy[i] = (data[n-1][1] - data[n-2][1]) / h[n-2] + h[n-2] * (My[n-2] + 2 * My[n-1]) / 6.0;
            } else {
                Dx[i] = (data[i+1][0] - data[i][0]) / h[i] - h[i] * (2 * Mx[i] + Mx[i+1]) / 6.0;
                Dy[i] = (data[i+1][1] - data[i][1]) / h[i] - h[i] * (2 * My[i] + My[i+1]) / 6.0;
            }
        }
        
        // Hitung dan tampilkan titik kontrol Bézier
        System.out.println("\n--- Hasil Interpolasi Bézier Spline Kubik ---");
        
        for (int i = 0; i < n - 1; i++) {
            double x_i = data[i][0];
            double y_i = data[i][1];
            double x_i_plus_1 = data[i+1][0];
            double y_i_plus_1 = data[i+1][1];
            double h_i = h[i];
            
            // Titik kontrol Bézier: B_i = P_i + (h_i * D_i)/3, C_i = P_{i+1} - (h_i * D_{i+1})/3
            double Bx = x_i + (h_i * Dx[i]) / 3.0;
            double By = y_i + (h_i * Dy[i]) / 3.0;
            double Cx = x_i_plus_1 - (h_i * Dx[i+1]) / 3.0;
            double Cy = y_i_plus_1 - (h_i * Dy[i+1]) / 3.0;
            
            System.out.println("\nSegmen " + (i + 1) + " (dari P" + (i + 1) + " ke P" + (i + 2) + "):");
            System.out.println("  Titik Kontrol P" + (i + 1) + ": (" + NumberFmt.format3(x_i) + ", " + NumberFmt.format3(y_i) + ")");
            System.out.println("  Titik Kontrol C" + (i + 1) + ",1: (" + NumberFmt.format3(Bx) + ", " + NumberFmt.format3(By) + ")");
            System.out.println("  Titik Kontrol C" + (i + 1) + ",2: (" + NumberFmt.format3(Cx) + ", " + NumberFmt.format3(Cy) + ")");
            System.out.println("  Titik Kontrol P" + (i + 2) + ": (" + NumberFmt.format3(x_i_plus_1) + ", " + NumberFmt.format3(y_i_plus_1) + ")");
        }
    }
}