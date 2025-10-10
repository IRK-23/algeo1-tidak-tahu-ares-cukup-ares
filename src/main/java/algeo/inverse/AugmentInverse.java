package algeo.inverse;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.determinant.RowReductionDeterminant;
import algeo.io.MatrixIO;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** Inverse dengan matriks augmented [A | I] kemudian RREF → [I | A^-1]. */
public final class AugmentInverse {
  private AugmentInverse() {}

  public static Matrix inverse(Matrix A) {
    return inverse(A, true, MatrixOps.EPS);
  }

  public static Matrix inverse(Matrix A, boolean pivoting, double eps) {
    if (A == null) throw new IllegalArgumentException("Matrix A tidak boleh kosong");
    if (!A.isSquare()) throw new IllegalArgumentException("Inverse hanya untuk matriks persegi");

        PrintStream originalOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

System.setOut(new PrintStream(bos));

    double det = RowReductionDeterminant.of(A);

         System.setOut(originalOut);

    if (Math.abs(det) <= eps)
      throw new IllegalArgumentException("Matriks singular, tidak memiliki inverse");

    int n = A.rows();
    Matrix I = Matrix.identity(n);
    Matrix aug = A.copy().augment(I);
    System.out.println(aug);

   
    System.setOut(new PrintStream(bos));

    Matrix mAug = MatrixOps.rref(aug);

    System.setOut(originalOut);

    Matrix inv = mAug.submatrix(0, n - 1, n, 2 * n - 1);
    return inv;
  }

  /** CLI helper: input matrix dulu lalu tampilkan inverse (augment method). */
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
      System.out.println("Inverse (augment) =");
      System.out.println(inv);
    } catch (IllegalArgumentException ex) {
      System.out.println("Tidak dapat menghitung inverse: " + ex.getMessage());
    }
  }
}

