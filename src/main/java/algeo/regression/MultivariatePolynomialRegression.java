package algeo.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        private final int k; //jumlah fitur
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

    public static Model fit(double[][] Xraw, double[] y, int degree, double eps) {
        if (Xraw == null || Xraw.length == 0) throw new IllegalArgumentException("X kosong");
        final int n = Xraw.length;
        final int k = Xraw[0].length;
        if (y == null || y.length != n) throw new IllegalArgumentException("y tidak selaras dengan X");
        if (degree < 0) throw new IllegalArgumentException("degree harus >= 0");
        if (k <= 0) throw new IllegalArgumentException("jumlah peubah harus > 0");

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
            out.add(new Term(Arrays.copyOf(cur, cur.length))); // satu term
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
        Matrix aug = A.augment(b);
        final int n = A.rows();

        int r = 0;
        for (int c = 0; c < n && r < n; c++) {
            // pilih pivot: partial pivoting (maks |elemen|)
            int pivot = r;
            double best = Math.abs(aug.get(pivot, c));
            for (int i = r + 1; i < n; i++) {
                double v = Math.abs(aug.get(i, c));
                if (v > best) { best = v; pivot = i; }
            }
            if (best <= eps) throw new ArithmeticException("Matriks singular/hampir singular (pivot ~ 0).");
            if (pivot != r) aug.swapRows(pivot, r);

            // normalisasi pivot → 1
            double pv = aug.get(r, c);
            if (Math.abs(pv - 1.0) > eps) aug.scaleRow(r, 1.0 / pv);
            aug.set(r, c, 1.0); // rapikan

            // eliminasi kolom c pada baris lain
            for (int i = 0; i < n; i++) {
                if (i == r) continue;
                double factor = aug.get(i, c);
                if (Math.abs(factor) <= eps) continue;
                aug.addRowMultiple(i, r, -factor);
                aug.set(i, c, 0.0);
            }
            r++;
        }

        // Ambil kolom solusi (kanan) sebagai submatrix (0..n-1, n..n)
        Matrix solCol = aug.submatrix(0, n - 1, n, n);
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = solCol.get(i, 0);
        return x;
    }
}


