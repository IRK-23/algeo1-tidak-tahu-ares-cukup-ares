package algeo.determinant;

import java.io.IOException;
import java.util.Scanner;

import algeo.core.*;
import algeo.io.*;

public final class RowReductionDeterminant {
  private RowReductionDeterminant() {}

  public static double of(Matrix A) {
    if (!A.isSquare()) {
      throw new IllegalArgumentException("Determinan hanya untuk matriks persegi.");
    }
    Matrix m = A.copy();
    final int n = m.rows();

    System.out.println("\n--- Perhitungan Determinan (OBE) ---");
    System.out.println("Matriks awal:");
    System.out.println(m);

    double sign = 1.0; // flip -1 ketika swap baris
    int r = 0;

    for (int c = 0; c < n && r < n; c++) {
      System.out.println("\nLangkah " + (r + 1) + ", memproses Kolom " + (c + 1));

      // Cari pivot: baris yang memiliki nilai non-nol
      int pivotRow = r;
      while (pivotRow < n && Math.abs(m.get(pivotRow, c)) <= MatrixOps.EPS) {
        pivotRow++;
      }

      if (pivotRow == n) {
        System.out.println("Kolom ini hanya berisi nol. Determinan = 0.");
        return 0.0;
      }

      if (pivotRow != r) {
        m.swapRows(pivotRow, r);
        sign *= -1.0;
        System.out.println("Tukar baris " + (r + 1) + " dan " + (pivotRow + 1) + ".");
        System.out.println("Tanda determinan berubah menjadi " + (sign > 0 ? "+" : "-"));
        System.out.println(m);
      }

      // Eliminasi ke bawah
      double pv = m.get(r, c);
      if (Math.abs(pv) > MatrixOps.EPS) {
        for (int rr = r + 1; rr < n; rr++) {
          double factor = m.get(rr, c) / pv;
          if (Math.abs(factor) > MatrixOps.EPS) {
            m.addRowMultiple(rr, r, -factor);
            System.out.println(
                "Eliminasi baris "
                    + (rr + 1)
                    + " (R"
                    + (rr + 1)
                    + " - "
                    + NumberFmt.format3(factor)
                    + "*R"
                    + (r + 1)
                    + "):");
            System.out.println(m);
          }
        }
      }
      r++;
    }

    // Produk diagonal
    double det = sign;
    System.out.println("\n--- Matriks Reduksi ---");
    System.out.println(m);

    System.out.println("Menghitung hasil perkalian diagonal:");
    System.out.print("det(A) = ");
    for (int i = 0; i < n; i++) {
      double diagVal = m.get(i, i);
      det *= diagVal;

      System.out.print(NumberFmt.format3(diagVal));
      if (i != (n - 1)) System.out.print(" * ");
    }
    
    System.out.println("\n");

    if (Math.abs(det) < MatrixOps.EPS) {
      det = 0.0;
      System.out.println("Determinan = 0\n");
    } else {
      System.out.println("Determinan tidak nol, tetapi sangat kecil\n");
    }

    System.out.println("Hasil akhir: det(A) = " + NumberFmt.format3(det));
    return det;
  }

  private static final Scanner sc = new Scanner(System.in);

  public static void run() {
    System.out.println("\n=== Determinan (Metode OBE/Reduksi Baris) ===");

    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix M = null;

    // Loop input sampai dapat matriks valid atau user batal
    while (M == null) {
      if (choice == UiPrompts.InputChoice.MANUAL) {
        M = MatrixIO.inputMatrix(sc);
      } else {
        String path = UiPrompts.askPath(sc, "Masukkan path file determinan (.txt): ");
        try {
          M = MatrixIO.readDeterminantFromFile(path);
          System.out.println("File berhasil dibaca: " + M.rows() + "x" + M.cols());
        } catch (IOException | IllegalArgumentException ex) {
          System.out.println("Gagal menyimpan: " + ex.getMessage());
          boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
          if (!retry) {
            boolean sw = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
            if (sw) choice = UiPrompts.InputChoice.MANUAL; else { System.out.println("Operasi dibatalkan."); return; }
          }
        }
      }
    }

    if (!M.isSquare()) {
      System.out.println("Matriks harus persegi untuk menghitung determinan.");
      return;
    }

    double det = of(M);
    System.out.println("Determinan (OBE) = " + NumberFmt.format3(det));

    // Susun teks simpan sesuai spesifikasi
    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    out.append("Metode pencarian determinan: OBE (Reduksi Baris)").append(nl).append(nl);
    out.append("Input yang digunakan:").append(nl).append(M).append(nl);
    out.append("Hasil determinan: ").append(NumberFmt.format3(det)).append(nl);

    ResultSaver.maybeSaveText(sc, "det_obe", "Hasil Determinan – Metode OBE", out.toString());
  }
}

