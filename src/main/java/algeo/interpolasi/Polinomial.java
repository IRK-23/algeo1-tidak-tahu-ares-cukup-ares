package algeo.interpolasi;

import java.io.IOException;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;
import algeo.io.UiPrompts.InputChoice;

import java.util.Locale;
import java.util.Scanner;

public class Polinomial {
  public static void polinomial() {
        try (Scanner sc = new Scanner(System.in)) {
            run(sc);
        }
      }

  public static void run(Scanner sc) {
        System.out.println("\n== Interpolasi Polinomial ==");
        System.out.println("Masukkan n titik (x, y). Akan dibangun polinom derajat n-1 (Vandermonde).");

        InputChoice ic = UiPrompts.askInputChoice(sc);

        double[][] samples;
        if (ic == InputChoice.FILE) {
            String path = UiPrompts.askPath(sc, "Masukkan path file .txt (2 kolom: x y): ");
            try {
                Matrix m = MatrixIO.readMatrixFromFile(path);
                samples = toXY(m);
            } catch (IOException e) {
                throw new RuntimeException("Error reading file: " + e.getMessage());
            }
        } else {
            samples = inputSamplesManual(sc);
        }

        // Validasi minimal titik
        final int n = samples.length;
        if (n < 2) {
            throw new IllegalArgumentException("Minimal butuh 2 titik sampel untuk interpolasi polinomial.");
        }

        // Bangun Vandermonde A (n×n) dan vektor Y (n×1)
        Matrix A = new Matrix(n, n);
        Matrix Y = new Matrix(n, 1);
        for (int i = 0; i < n; i++) {
            double x = samples[i][0];
            for (int j = 0; j < n; j++) {
                A.set(i, j, Math.pow(x, j)); // basis 1, x, x^2, ...
            }
            Y.set(i, 0, samples[i][1]);
        }

        // Cek rank untuk singularitas (duplikasi x dapat membuat Vandermonde singular)
        if (MatrixOps.cekRank(A) < n) {
            throw new ArithmeticException("Vandermonde singular (kemungkinan ada x yang sama).");
        }

        // Pecahkan A c = Y dengan RREF pada augmented
        Matrix aug = A.augment(Y);
        Matrix rref = MatrixOps.rref(aug);

        // Ambil koefisien c0..c_{n-1} dari kolom terakhir
        double[] coeff = new double[n];
        for (int i = 0; i < n; i++) coeff[i] = rref.get(i, n);

        // Bentuk persamaan y(x)
        String equation = formatPolynomial(coeff);

        // Tampilkan
        String nl = System.lineSeparator();
        StringBuilder out = new StringBuilder();
        out.append("Jumlah titik: ").append(n).append(nl);
        out.append("Titik sampel:").append(nl);
        for (int i = 0; i < n; i++) {
            out.append("  (")
               .append(NumberFmt.format3(samples[i][0]))
               .append(", ")
               .append(NumberFmt.format3(samples[i][1]))
               .append(")")
               .append(nl);
        }
        out.append(nl);
        out.append("Persamaan Polinomial (derajat ").append(n - 1).append("):").append(nl);
        out.append(equation).append(nl);

        System.out.println();
        System.out.println(out);

        // Tanya simpan?
        ResultSaver.maybeSaveText(sc, "interpolation", "Interpolasi Polinomial (Vandermonde)", out.toString());

        // Evaluasi interaktif
        evalLoop(sc, coeff);
    }

    private static double[][] inputSamplesManual(Scanner sc) {
        final int maxN = MatrixIO.MAX_MANUAL;
        int n = UiPrompts.askInt(sc, "Masukkan jumlah titik (2-" + maxN + "): ", 2, maxN);
        double[][] data = new double[n][2];

        System.out.println("Masukkan tiap baris sebagai: x y");
        for (int i = 0; i < n; i++) {
            while (true) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Baris kosong. Ulangi.");
                    continue;
                }
                String[] toks = line.split("\\s+");
                if (toks.length != 2) {
                    System.out.println("Format salah. Harus dua angka: x y");
                    continue;
                }
                try {
                    data[i][0] = NumberFmt.parseNumber(toks[0]); // dukung 2,5 | 2.5 | 3/4
                    data[i][1] = NumberFmt.parseNumber(toks[1]);
                    break;
                } catch (RuntimeException ex) {
                    System.out.println("Token tidak valid. Contoh: 2,5  |  2.5  |  3/4  |  -7/3");
                }
            }
        }
        return data;
    }

    private static double[][] toXY(Matrix m) {
        if (m.cols() != 2) {
            throw new IllegalArgumentException("File harus memiliki tepat 2 kolom (x dan y).");
        }
        double[][] data = new double[m.rows()][2];
        for (int i = 0; i < m.rows(); i++) {
            data[i][0] = m.get(i, 0);
            data[i][1] = m.get(i, 1);
        }
        return data;
    }

    private static String formatPolynomial(double[] c) {
        StringBuilder sb = new StringBuilder();
        sb.append("y(x) = ");
        boolean first = true;
        Locale us = Locale.US; // jaga titik desimal

        for (int i = 0; i < c.length; i++) {
            double coeff = c[i];
            if (Math.abs(coeff) <= MatrixOps.EPS) continue; // skip koefisien ~0

            String coeffStr = NumberFmt.format3(Math.abs(coeff));

            if (first) {
                // suku pertama: tampilkan tanda lewat nilai
                switch (i) {
                    case 0 -> sb.append(String.format(us, "%s", NumberFmt.format3(coeff)));
                    case 1 -> sb.append(String.format(us, "%s·x", NumberFmt.format3(coeff)));
                    default -> sb.append(String.format(us, "%s·x^%d", NumberFmt.format3(coeff), i));
                }
                first = false;
            } else {
                sb.append(coeff >= 0 ? " + " : " - ");
                switch (i) {
                    case 0 -> sb.append(coeffStr);
                    case 1 -> sb.append(coeffStr).append("·x");
                    default -> sb.append(coeffStr).append("·x^").append(i);
                }
            }
        }

        if (first) sb.append(NumberFmt.format3(0.0)); // semua 0
        return sb.toString();
    }

    private static void evalLoop(Scanner sc, double[] coeff) {
        System.out.println("\nEvaluasi nilai polinom.");
        System.out.println("Ketik nilai x, lalu ENTER. Kosongkan baris / ketik 'q' untuk selesai.");
        while (true) {
            System.out.print("x = ");
            String line = sc.nextLine().trim();
            if (line.isEmpty() || line.equalsIgnoreCase("q")) break;
            try {
                double x = NumberFmt.parseNumber(line);
                double y = 0.0;
                double xpow = 1.0; // Horner sederhana
                for (int i = 0; i < coeff.length; i++) {
                    y += coeff[i] * xpow;
                    xpow *= x;
                }
                System.out.println("y(" + NumberFmt.format3(x) + ") = " + NumberFmt.format3(y));
            } catch (RuntimeException ex) {
                System.out.println("Input x tidak valid. Contoh: 2,5  |  2.5  |  3/4  |  -7/3");
            }
        }
    }
}
