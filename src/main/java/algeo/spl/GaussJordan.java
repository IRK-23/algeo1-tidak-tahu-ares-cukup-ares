package algeo.spl;

import algeo.core.*;
import algeo.io.*;

public class GaussJordan {
    public static void gaussjordan() {
    Matrix M = MatrixIO.inputAugmentedMatrix();

    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);
    Matrix mRef = MatrixOps.ref(M);
    switch (solutionType) {
        case 0 -> System.out.println("Tidak ada solusi.");
        case 1 -> {
            System.out.println("Solusi tunggal:");
            Gauss.finishSPL(mRef);
        }
        default -> {
            System.out.println("Solusi banyak:");
            Gauss.finishParametricSPL(mRef);
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
