package algeo.spl;

import algeo.core.*;
import algeo.io.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class GaussJordan {
  public static void gaussjordan() {
    Matrix M = MatrixIO.inputAugmentedMatrix();

    PrintStream originalOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    System.setOut(new PrintStream(bos));

    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);

    System.setOut(originalOut);

      switch (solutionType) {
          case 0 -> System.out.println("Determinan = 0, Tidak ada solusi.\n");
          case 1 ->               {
                  System.out.println("Solusi tunggal:");
                  Matrix mRref = MatrixOps.rref(M);
                  Gauss.finishSPL(mRref);
              }
          default ->               {
                  // solutionType == 2
                  System.out.println("Solusi banyak:");
                  Matrix mRref = MatrixOps.rref(M);
                  Gauss.finishParametricSPL(mRref);
              }
      }
  }

  public static void makeReductedEchelon(Matrix M) {
    int rows = M.rows();
    int cols = M.cols();

    MatrixOps.ref(M);

    System.out.println("\n--- Tahap Eliminasi Mundur (Gauss-Jordan) ---");

    for (int i = rows - 1; i >= 0; i--) {
      int cP = -1;

      for (int j = 0; j < cols; j++) {
        if (M.get(i, j) == 1.0) {
          cP = j;
          break;
        }
      }

      if (cP == -1) {
        continue;
      }

      // Gunakan leading one untuk mengeliminasi elemen di atasnya
      for (int k = i - 1; k >= 0; k--) {
        double factor = M.get(k, cP);
        if (factor != 0) {
          M.addRowMultiple(k, i, -factor);
          System.out.println(
              "Eliminasi baris "
                  + (k + 1)
                  + " (R"
                  + (k + 1)
                  + " - "
                  + factor
                  + "*R"
                  + (i + 1)
                  + "):");
          System.out.println(M);
        }
      }
    }
  }
}
