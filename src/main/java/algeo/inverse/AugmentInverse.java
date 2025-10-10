package algeo.inverse;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.determinant.RowReductionDeterminant;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

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

    Matrix mAug = MatrixOps.rref(aug);

    Matrix inv = mAug.submatrix(0, n - 1, n, 2 * n - 1);
    return inv;
  }

  /** CLI helper: input matrix dulu lalu tampilkan inverse (augment method). */
  private static final Scanner sc = new Scanner(System.in);

  public static void run() {
    System.out.println("\n=== Matriks Balikan (Augment + RREF) ===");

    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix A = null;

    while (A == null) {
      if (choice == UiPrompts.InputChoice.MANUAL) {
        A = MatrixIO.inputMatrix(sc);
      } else {
        String path = UiPrompts.askPath(sc, "Masukkan path file inverse (.txt): ");
        try {
          A = MatrixIO.readInverseFromFile(path);
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

    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    out.append("Metode pencarian matriks balikan: Augment + RREF").append(nl).append(nl);
    out.append("Input yang digunakan:").append(nl).append(A).append(nl);

    try {
      Matrix inv = inverse(A); // panggil implementasi kamu
      System.out.println("\nA^-1 =");
      System.out.println(inv);
      out.append("Matriks balikan hasil:").append(nl).append(inv).append(nl);
    } catch (IllegalArgumentException ex) {
      String msg = "Matriks tidak memiliki balikan (singular).";
      System.out.println("\n" + msg);
      out.append("Matriks balikan hasil: ").append(msg).append(nl);
    }

    ResultSaver.maybeSaveText(sc, "inv_augment", "Hasil Invers – Augment + RREF", out.toString());
  }
}
