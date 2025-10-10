package algeo.inverse;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.determinant.CofactorDeterminant;
import algeo.io.MatrixIO;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** Inverse menggunakan adjoin : A^-1 = adj(A) / det(A). */
public final class AdjointInverse {
  private AdjointInverse() {}

  public static Matrix inverse(Matrix A) {
    return inverse(A, MatrixOps.EPS);
  }

  public static Matrix inverse(Matrix A, double eps) {
    if (A == null) throw new IllegalArgumentException("Matriks tidak boleh kosong");
    if (!A.isSquare())
      throw new IllegalArgumentException("Matriks harus merupakan matriks persegi");

    int n = A.rows();

    PrintStream originalOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    System.setOut(new PrintStream(bos));

    double det = CofactorDeterminant.of(A);

    System.setOut(originalOut);

    if (Math.abs(det) <= eps)
      throw new IllegalArgumentException("Matriks singular, tidak memiliki inverse");

    Matrix adj = new Matrix(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double[][] minor = new double[n - 1][n - 1];
        int mi = 0;
        for (int r = 0; r < n; r++) {
          if (r == i) continue;
          int mj = 0;
          for (int c = 0; c < n; c++) {
            if (c == j) continue;
            minor[mi][mj++] = A.get(r, c);
          }
          mi++;
        }
        Matrix m = new Matrix(minor);
        double cofactor = CofactorDeterminant.of(m, eps);
        if (((i + j) & 1) != 0) cofactor = -cofactor;

        System.out.println("Kofaktor C[" + (i + 1) + "][" + (j + 1) + "] = " + m);
        System.out.println("=" + cofactor);

        adj.set(j, i, cofactor);
      }
    }

    Matrix inv = new Matrix(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        inv.set(i, j, adj.get(i, j) / det);
      }
    }

    System.out.println(inv);

    return inv;
  }

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
      System.out.println("Inverse (adjoint) =");
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

