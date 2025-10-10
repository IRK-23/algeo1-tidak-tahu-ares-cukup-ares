package algeo.regression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import algeo.io.*;
import algeo.core.Matrix;
import algeo.core.NumberFmt;

public final class MultivariatePolynomialRegression {
    
    public static final class Term {
        public final int[] exponents;

        public Term(int[] exponents) {
            this.exponents = exponents;
        }

        public String name() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < exponents.length; i++) {
                int e = exponents[i];
                if (e == 0) continue;
                if (!first) sb.append("*");
                first = false;
                sb.append("x_").append(i + 1);
                if (e != 1) sb.append("^").append(e);
            }
            return first ? "1" : sb.toString();
        }
    }

    public static final class Model {
        private final double[] beta;
        private final List<Term> terms;
        private final int degree;
        private final int k;
        private final double eps;

        private Model(double[] beta, List<Term> terms, int degree, int k, double eps) {
            this.beta = beta;
            this.terms = terms;
            this.degree = degree;
            this.k = k;
            this.eps = eps;
        }
        
        public int variables() { return k; }
        public int degree() { return degree; }
        public double[] coefficients() { return beta.clone(); }
        public List<Term> terms() { return terms; }

        public double predict(double[] x) {
            if (x == null || x.length != k) {
                throw new IllegalArgumentException("x harus berdimensi " + k);
            }
            double y = 0.0;
            for (int i = 0; i < beta.length; i++) {
                y += beta[i] * evalMonomial(x, terms.get(i).exponents);
            }
            return (Math.abs(y) <= eps) ? 0.0 : y;
        }

        public String toEquationString() {
            StringBuilder sb = new StringBuilder();
            sb.append("y = ");
            boolean first = true;
            for (int i = 0; i < beta.length; i++) {
                double c = (Math.abs(beta[i]) <= eps ? 0.0 : beta[i]);
                String mono = terms.get(i).name();
                String cstrAbs = NumberFmt.format3(Math.abs(c));
                if (first){
                    String cstr = NumberFmt.format3(c);
                    if ("1".equals(mono)) sb.append(cstr);
                    else sb.append(cstr).append("·").append(mono);
                    first = false;
                } else {
                    sb.append(c >= 0 ? " + " : " - ");
                    if ("1".equals(mono)) sb.append(cstrAbs);
                    else sb.append(cstrAbs).append("·").append(mono);
                }
            }
            if (first) sb.append(NumberFmt.format3(0.0));
            return sb.toString();
        }
    }

    private MultivariatePolynomialRegression() {}

    // FUNGSI TAMBAHAN: Hitung jumlah suku (terms) untuk k variabel dan derajat d
    public static int calculateNumberOfTerms(int k, int d) {
        return binomialCoeff(d + k, k);
    }

    // Kombinasi C(n, k)
    private static int binomialCoeff(int n, int k) {
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;
        
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }
        return (int) result;
    }

    public static Model fit(double[][] Xraw, double[] y, int degree, double eps) {
        if (Xraw == null || Xraw.length == 0) throw new IllegalArgumentException("X kosong");
        final int n = Xraw.length;
        final int k = Xraw[0].length;
        if (y == null || y.length != n) throw new IllegalArgumentException("y tidak selaras dengan X");
        if (degree < 0) throw new IllegalArgumentException("degree harus >= 0");
        if (k <= 0) throw new IllegalArgumentException("jumlah peubah harus > 0");

        // VALIDASI JUMLAH SAMPEL
        int p = calculateNumberOfTerms(k, degree);
        if (n < p) {
            throw new IllegalArgumentException(
                String.format("Jumlah sampel tidak cukup! Butuh minimal %d sampel untuk k=%d variabel dan derajat d=%d. Diberikan: %d sampel.",
                    p, k, degree, n)
            );
        }

        // Basis & desain X
        List<Term> basis = generateBasis(k, degree);
        Matrix X = buildDesignMatrix(Xraw, basis);

        // Normal equations
        Matrix XtX = gram(X);
        Matrix Xty = crossXtY(X, columnVector(y));

        // Solve [XtX | Xty] → [I | beta]
        double[] beta = solveByGaussJordan(XtX, Xty, eps);

        // Snap koefisien kecil ke nol
        for (int i = 0; i < beta.length; i++) if (Math.abs(beta[i]) <= eps) beta[i] = 0.0;

        return new Model(beta, basis, degree, k, eps);
    }   

    public static Model fit(Matrix samples, int degree, double eps) {
        if (samples == null) throw new IllegalArgumentException("samples null");
        final int n = samples.rows();
        final int kc = samples.cols();
        if (kc < 2) throw new IllegalArgumentException("Minimal 1 fitur + 1 kolom y.");
        final int k = kc - 1;

        // Susun Xraw & y dari sample
        double[][] Xraw = new double[n][k];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) Xraw[i][j] = samples.get(i, j);
            y[i] = samples.get(i, k);
        }
        return fit(Xraw, y, degree, eps);
    }
    
    private static List<Term> generateBasis(int k, int degree) {
        List<Term> terms = new ArrayList<>();
        int[] exponents = new int[k];
        generateBasisRecursive(terms, exponents, 0, degree);
        return terms;
    }

    private static void generateBasisRecursive(List<Term> out, int[] cur, int idx, int rem) {
        if (idx == cur.length) {
            out.add(new Term(Arrays.copyOf(cur, cur.length)));
            return;
        }
        for (int e = 0; e <= rem; e++) {
            cur[idx] = e;
            generateBasisRecursive(out, cur, idx + 1, rem - e);
        }
    }

    private static double evalMonomial(double[] x, int[] exp) {
        double v = 1.0;
        for (int j = 0; j < exp.length; j++) {
            int e = exp[j];
            if (e == 0) continue;
            v *= Math.pow(x[j], e);
        }
        return v;
    }

    private static Matrix buildDesignMatrix(double[][] Xraw, List<Term> basis) {
        int n = Xraw.length, p = basis.size();
        Matrix X = new Matrix(n, p);
        for (int i = 0; i < n; i++) {
            double[] row = Xraw[i];
            for (int j = 0; j < p; j++) {
                X.set(i, j, evalMonomial(row, basis.get(j).exponents));
            }
        }
        return X;
    }

    private static Matrix gram(Matrix X) {
        int n = X.rows(), p = X.cols();
        Matrix XtX = new Matrix(p, p);
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0.0;
                for (int r = 0; r < n; r++) {
                    sum += X.get(r, i) * X.get(r, j);
                }
                XtX.set(i, j, sum);
                if (i != j) XtX.set(j, i, sum);
            }
        }
        return XtX;
    }

    private static Matrix crossXtY(Matrix X, Matrix y) {
        int n = X.rows(), p = X.cols();
        if (y.rows() != n || y.cols() != 1) {
            throw new IllegalArgumentException("y harus vektor kolom dengan jumlah baris sama dengan X");
        }
        Matrix Xty = new Matrix(p, 1);
        for (int i = 0; i < p; i++) {
            double sum = 0.0;
            for (int r = 0; r < n; r++) {
                sum += X.get(r, i) * y.get(r, 0);
            }
            Xty.set(i, 0, sum);
        }
        return Xty;
    }

    private static Matrix columnVector(double[] y) {
        int n = y.length;
        Matrix Y = new Matrix(n, 1);
        for (int i = 0; i < n; i++) Y.set(i, 0, y[i]);
        return Y;
    }

    private static double[] solveByGaussJordan(Matrix A, Matrix b, double eps) {
        if (A == null || b == null) throw new IllegalArgumentException("A/b null");
        if (A.rows() != A.cols()) throw new IllegalArgumentException("A harus persegi");
        if (b.rows() != A.rows() || b.cols() != 1) throw new IllegalArgumentException("b harus vektor kolom nx1");

        final int n = A.rows();

        if (algeo.core.MatrixOps.cekRank(A) < n) {
            throw new ArithmeticException("Matriks singular atau tak berbalik (rank(A) < n). Mungkin terjadi multikolinearitas pada data.");
        }

        // Bentuk augmented [A | b], RREF-kan
        Matrix aug = A.augment(b);
        Matrix rrefAug = algeo.core.MatrixOps.rref(aug);

        // Opsional: cek inkonsistensi baris nol di kiri tapi y non-nol
        for (int i = 0; i < n; i++) {
            boolean leftAllZero = true;
            for (int j = 0; j < n; j++) {
                if (Math.abs(rrefAug.get(i, j)) > eps) { leftAllZero = false; break; }
            }
            if (leftAllZero && Math.abs(rrefAug.get(i, n)) > eps) {
                throw new ArithmeticException("Sistem tidak konsisten (0 = c ≠ 0).");
            }
        }

        // Ambil solusi dari kolom terakhir
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = rrefAug.get(i, n);
        return x;
    }

    public static void run(Scanner sc) {
        System.out.println("\n== Regresi Polinomial Berganda ==");

        // baca degree dan eps
        int degree = UiPrompts.askInt(sc, "Masukkan derajat polinom: ", 0, 4);
        double eps = 1e-21;

        // pilih sumber input
        Matrix samples;
        int n=-1, k=-1;
        UiPrompts.InputChoice ic = UiPrompts.askInputChoice(sc);
        
        while(true){
            if (ic == UiPrompts.InputChoice.FILE) {
                String path = UiPrompts.askPath(sc, "Masukkan path file samples (.txt): ");
                try {
                    Object[] data = MatrixIO.readRegressionFromFile(path);
                    samples = (Matrix) data[0];
                    int fileDegree = (Integer) data[1];
                    
                    n = samples.rows();
                    k = samples.cols() - 1;
                    
                    // Override degree dari file jika valid
                    degree = fileDegree;
                    System.out.printf("File berhasil dibaca: %d sampel, %d variabel, derajat %d%n", n, k, degree);
                    break;
                } catch (IOException | IllegalArgumentException ex) {
                    System.out.println("Gagal membaca file: " + ex.getMessage());
                }
                
                boolean retryFile = UiPrompts.askYesNo(sc, "Ingin memilih file lain? (y/n): ");
                if (retryFile) {
                    continue;
                }
                boolean switchToManual = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
                if (switchToManual) {
                    ic = UiPrompts.InputChoice.MANUAL;
                } else {
                    System.out.println("Operasi dibatalkan.");
                    return;
                }
            } else { // MANUAL
                k = UiPrompts.askInt(sc, "Masukkan jumlah peubah k (1..5): ", 1, 5);
                
                // Hitung minimal sampel yang dibutuhkan
                int minSampel = calculateNumberOfTerms(k, degree);
                System.out.printf("Untuk k=%d variabel dan derajat d=%d, dibutuhkan minimal %d sampel.%n", k, degree, minSampel);
                
                n = UiPrompts.askInt(sc, "Masukkan jumlah sampel n (minimal " + minSampel + "): ", minSampel, MatrixIO.MAX_MANUAL);
                
                samples = readSamplesManual(sc, n, k);
                break;
            }
        }
        
        if (samples == null || n <= 0 || k <= 0) {
            System.out.println("Gagal memperoleh sampel yang valid. Operasi dibatalkan.");
            return;
        }
        
        // fit
        try {
            MultivariatePolynomialRegression.Model model =
                    MultivariatePolynomialRegression.fit(samples, degree, eps);

            // tampilkan & simpan
            String eq = model.toEquationString();
            System.out.println("\nPersamaan regresi:");
            System.out.println(eq);

            while (true) {
                System.out.println("\nEvaluasi y untuk satu titik x_t.");
                System.out.println("Masukkan " + model.variables() + " nilai x_t dipisahkan spasi (atau ketik -9999 untuk selesai):");
                String line = sc.nextLine().trim();
                if (line.equals("-9999")) break;
                if (line.isEmpty()) {
                    System.out.println("Baris kosong. Coba lagi.");
                    continue;
                }

                String[] toks = line.split("\\s+");
                if (toks.length != model.variables()) {
                    System.out.println("Butuh tepat " + model.variables() + " nilai. Diberikan: " + toks.length);
                    continue;
                }

                try {
                    double[] xt = new double[model.variables()];
                    for (int i = 0; i < xt.length; i++) xt[i] = NumberFmt.parseNumber(toks[i]);
                    double yt = model.predict(xt);
                    
                    System.out.print("x_t = (");
                    for (int i = 0; i < xt.length; i++) {
                        if (i > 0) System.out.print(", ");
                        System.out.print(NumberFmt.format3(xt[i]));
                    }
                    System.out.println(")  ⇒  y_t = " + NumberFmt.format3(yt));
                } catch (RuntimeException ex) {
                    System.out.println("Input tidak valid. Gunakan angka desimal/pecahan yang sah (contoh: 2.5, 3/4).");
                }
            }

            String nl = System.lineSeparator();
            StringBuilder body = new StringBuilder();
            body.append("Regresi Polinomial Berganda").append(nl);
            body.append("n = ").append(n).append(", k = ").append(k).append(", m = ").append(model.degree()).append(nl).append(nl);

            body.append("Sampel (setiap baris: x1 ... xk y):").append(nl);
            body.append(samples).append(nl);

            body.append("Persamaan: ").append(eq).append(nl).append(nl);

            ResultSaver.maybeSaveText(sc, 
                "regression", 
                "Hasil Regresi Polinomial Berganda", 
                body.toString());
        } catch (IllegalArgumentException | ArithmeticException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            System.out.println("Operasi gagal. Silakan coba lagi dengan data yang berbeda.");
        }
    }
    
    private static Matrix readSamplesManual(Scanner sc, int n, int k) {
        System.out.println("Masukkan " + n + " baris. Setiap baris berisi " + k + " nilai x diikuti 1 nilai y.");
        System.out.println("Format: x1 x2 ... x" + k + " y");
        double[][] data = new double[n][k + 1];

        for (int i = 0; i < n; i++) {
            while (true) {
                System.out.print("Baris " + (i+1) + "/" + n + ": ");
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Baris tidak boleh kosong. Coba lagi.");
                    continue;
                }
                String[] toks = line.split("\\s+");
                if (toks.length != k + 1) {
                    System.out.println("Jumlah nilai pada baris ini harus " + (k + 1) + ". Diberikan: " + toks.length);
                    continue;
                }
                try {
                    for (int j = 0; j < k + 1; j++) data[i][j] = NumberFmt.parseNumber(toks[j]);
                    break;
                } catch (RuntimeException ex) {
                    System.out.println("Ada token tidak valid. Contoh valid: 2,5 | 2.5 | 3/4 | -7/3. Ulangi baris.");
                }
            }
        }
        return new Matrix(data);
    }
}