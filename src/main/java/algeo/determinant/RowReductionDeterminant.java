package algeo.determinant;

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

  public static void run() {
    Matrix M = MatrixIO.inputMatrix();
    if (M == null) {
      System.out.println("Input dibatalkan.");
      return;
    }
    if (!M.isSquare()) {
      System.out.println("Matriks harus persegi untuk menghitung determinan.");
      return;
    }
    double det = of(M);
    System.out.println("Determinan (OBE) = " + NumberFmt.format3(det));
  }
}

