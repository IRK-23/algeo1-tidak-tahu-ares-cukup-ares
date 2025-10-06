package algeo.spl;

import algeo.core.*;
import algeo.io.*;

public class Gauss {
  public static void gauss() {
    Matrix M = MatrixIO.inputMatrix();
    makeEchelon(M);
    finishSPL(M);
  }

  public static void finishSPL(Matrix M) {
    int n = M.cols() - 1;
    double[] solution = new double[n];

    for (int i = n - 1; i >= 0; i--) {
      double rhs = M.get(i, n);

      StringBuilder equation = new StringBuilder();
      for (int j = 0; j < n; j++) {
        double coef = M.get(i, j);
        if (coef != 0) {
          if (equation.length() > 0 && coef > 0) {
            equation.append(" + ");
          } else if (coef < 0) {
            equation.append(" - ");
          }
          if (Math.abs(coef) != 1 || j == n - 1) {
            equation.append(Math.abs(coef));
          }
          equation.append("x").append(j + 1);
        }
      }
      equation.append(" = ").append(rhs);
      System.out.println("Persamaan: " + equation.toString());

      double sum = 0;
      for (int j = i + 1; j < n; j++) {
        sum += M.get(i, j) * solution[j];
      }
      solution[i] = rhs - sum;

      System.out.println("Solusi: x" + (i + 1) + " = " + NumberFmt.format3(solution[i]) + "\n");
    }
  }

  public static void makeEchelon(Matrix M) {
    int rP = 0;
    int cP = 0;
    int rows = M.rows();
    int cols = M.cols();

    while (rP < rows && cP < cols) {
      System.out.println("\nLangkah " + (rP + 1) + ", memproses Kolom " + (cP + 1));

      if (M.get(rP, cP) == 1.0) {
        for (int i = rP + 1; i < rows; i++) {
          double factor = M.get(i, cP);
          if (factor != 0) {
            M.addRowMultiple(i, rP, -factor);
            System.out.println(
                "Eliminasi baris "
                    + (i + 1)
                    + " (R"
                    + (i + 1)
                    + " - "
                    + factor
                    + "*B"
                    + (rP + 1)
                    + "):");

            System.out.println(M);
          }
        }
        rP++;
        cP++;
        continue;
      }

      int i_max = rP;
      for (int i = rP; i < rows; i++) {
        if (Math.abs(M.get(i, cP)) > Math.abs(M.get(i_max, cP))) {
          i_max = i;
        }
      }

      if (M.get(i_max, cP) == 0) {
        System.out.println("Kolom ini hanya berisi nol. Pindah ke kolom berikutnya.");
        cP++;
      } else {
        if (rP != i_max) {
          M.swapRows(rP, i_max);
          System.out.println("Tukar baris " + (rP + 1) + " dan " + (i_max + 1) + ":");
          System.out.println(M);
        }

        double pivotValue = M.get(rP, cP);
        M.scaleRow(rP, 1.0 / pivotValue);
        System.out.println(
            "Bentuk leading one pada baris " + (rP + 1) + "R" + (rP + 1) + "/" + pivotValue);
        System.out.println(M);

        for (int i = rP + 1; i < rows; i++) {
          double factor = M.get(i, cP);
          if (factor != 0) {
            M.addRowMultiple(i, rP, -factor);
            System.out.println(
                "Eliminasi baris "
                    + (i + 1)
                    + " (R"
                    + (i + 1)
                    + " - "
                    + factor
                    + "*R"
                    + (rP + 1)
                    + "):");
            System.out.println(M);
          }
        }
        rP++;
        cP++;
      }
    }
  }
}
