package algeo.inverse;

import algeo.core.Matrix;
import algeo.core.MatrixOps;
import algeo.core.NumberFmt;
import algeo.determinant.CofactorDeterminant;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

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

  private static final Scanner sc = new Scanner(System.in);

  public static void run() {
    System.out.println("\n=== Matriks Balikan (Adjoint/Adjugate) ===");

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
    out.append("Metode pencarian matriks balikan: Adjoint/Adjugate").append(nl).append(nl);
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

    ResultSaver.maybeSaveText(sc, "inv_adjoint", "Hasil Invers – Adjoint", out.toString());
  }
}

