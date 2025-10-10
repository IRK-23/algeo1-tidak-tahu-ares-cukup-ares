package algeo.spl;

import algeo.core.*;
import algeo.inverse.AugmentInverse;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;
import java.io.ByteArrayOutputStream; // <-- TAMBAHKAN INI
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class InverseMethod {
  private static final Scanner sc = new Scanner(System.in);

  public static void inverseMethod() {
    System.out.println("\n=== SPL dengan Metode Matriks Balikan (A^-1 b) ===");

    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix M = null;

    while (M == null) {
      if (choice == UiPrompts.InputChoice.MANUAL) {
        M = MatrixIO.inputAugmentedMatrix(sc);
      } else {
        String path = UiPrompts.askPath(sc, "Masukkan path file SPL (.txt): ");
        try {
          M = MatrixIO.readSPLFromFile(path);
          System.out.println("File berhasil dibaca: " + M.rows() + "x" + M.cols());
        } catch (IOException | IllegalArgumentException ex) {
          System.out.println("Gagal membaca file: " + ex.getMessage());
          boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
          if (!retry) {
            boolean sw = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
            if (sw) choice = UiPrompts.InputChoice.MANUAL;
            else {
              System.out.println("Operasi dibatalkan.");
              return;
            }
          }
        }
      }
    }

    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    out.append("Metode penyelesaian SPL: Matriks Balikan (A^-1 b)").append(nl).append(nl);
    out.append("Input yang digunakan (A|b):").append(nl).append(M).append(nl);

    int sol = JumlahSolusi.cekJumlahSolusiM(M);
    if (sol != 1) {
      String msg =
          (sol == 0)
              ? "Tidak ada solusi. det(A)=0 atau sistem inkonsisten."
              : "Sistem memiliki banyak solusi. Metode matriks balikan tidak dapat digunakan.";
      System.out.println("\n" + msg);
      out.append("Hasil SPL: ").append(msg).append(nl);
    } else {
      int n = M.rows();
      Matrix A = M.submatrix(0, n - 1, 0, n - 1);
      Matrix b = M.colAsMatrix(n);
      try {
        Matrix inv = AugmentInverse.inverse(A);
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
          double s = 0.0;
          for (int j = 0; j < n; j++) s += inv.get(i, j) * b.get(j, 0);
          x[i] = s;
        }
        StringBuilder solStr = new StringBuilder();
        for (int i = 0; i < n; i++)
          solStr.append("x").append(i + 1).append(" = ").append(NumberFmt.format3(x[i])).append(nl);
        System.out.println("\nSolusi tunggal:");
        System.out.println(solStr);
        out.append("Hasil SPL (solusi tunggal):").append(nl).append(solStr);
      } catch (IllegalArgumentException ex) {
        String msg = "Matriks A tidak invertible. Gunakan Gauss/Gauss–Jordan.";
        System.out.println("\n" + msg);
        out.append("Hasil SPL: ").append(msg).append(nl);
      }
    }

    ResultSaver.maybeSaveText(
        sc, "spl_inverse", "Hasil SPL – Metode Matriks Balikan", out.toString());
  }

  public static void solveInverse(Matrix M) {
    int rows = M.rows();
    int cols = M.cols();

    if (cols != rows + 1) {
      System.out.println("Input harus berupa matriks augmented (n x (n+1)).");
      return;
    }

    Matrix A = M.submatrix(0, rows - 1, 0, cols - 2);
    Matrix B = M.colAsMatrix(cols - 1);

    if (!A.isSquare()) {
      System.out.println("Matriks koefisien harus persegi.");
      return;
    }

    int n = A.rows();

    try {
      PrintStream originalOut = System.out;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      System.setOut(new PrintStream(bos));

      Matrix inv = AugmentInverse.inverse(A);

      System.setOut(originalOut);

      System.out.println("Kalikan matriks invers A dengan B");
      System.out.println(inv);

      System.out.println(B);

      for (int i = 0; i < n; i++) {
        double xi = 0.0;
        for (int j = 0; j < n; j++) {
          xi += inv.get(i, j) * B.get(j, 0);
        }
        System.out.println("Solusi: x" + (i + 1) + " = " + NumberFmt.format3(xi));
      }
    } catch (IllegalArgumentException ex) {
      System.out.println("Matriks tidak memiliki inverse.");
    }
  }
}
