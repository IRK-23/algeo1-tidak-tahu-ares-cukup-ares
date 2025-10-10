package algeo.spl;

import algeo.core.*;
import algeo.io.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Gauss {
  public static void gauss() {
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
                  Matrix mRef = MatrixOps.ref(M);
                  Gauss.finishSPL(mRef);
              }
          default ->               {
                  // solutionType == 2
                  System.out.println("Solusi banyak:");
                  Matrix mRef = MatrixOps.ref(M);
                  Gauss.finishParametricSPL(mRef);
              }
      }
  }

  public static void finishSPL(Matrix M) {
    int n = M.cols() - 1;
    double[] solution = new double[n];

    System.out.println("\n--- Tahap Substitusi Mundur ---");

    for (int i = n - 1; i >= 0; i--) {
      double rhs = M.get(i, n);
      double sum = 0;
      StringBuilder equation = new StringBuilder();

      for (int j = 0; j < n; j++) {
        double coef = M.get(i, j);
        if (Math.abs(coef) > MatrixOps.EPS) {
          if (equation.length() > 0 && coef > 0) {
            equation.append(" + ");
          } else if (equation.length() > 0 && coef < 0) {
            equation.append(" - ");
          } else if (coef < 0) {
            equation.append("-");
          }

          if (Math.abs(coef) != 1.0) {
            equation.append(NumberFmt.format3(Math.abs(coef)));
          }
          equation.append("x").append(j + 1);
        }
      }

      for (int j = i + 1; j < n; j++) {
        sum += M.get(i, j) * solution[j];
      }

      solution[i] = rhs - sum;

      System.out.println("Persamaan: " + equation.toString() + " = " + NumberFmt.format3(rhs));
      System.out.println(
          "Substitusi: " + "x" + (i + 1) + " = " + NumberFmt.format3(solution[i]) + "\n");
    }

    for (int i = 0; i < n; i++) {
      System.out.println("x" + (i + 1) + " = " + NumberFmt.format3(solution[i]));
    }
  }

  public static void finishParametricSPL(Matrix M) {
    int rows = M.rows();
    int cols = M.cols();
    int n = cols - 1;
    int paramIndex = 1;

    List<Integer> nonZeroRows = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      boolean isZeroRow = true;
      for (int j = 0; j < cols; j++) {
        if (Math.abs(M.get(i, j)) > MatrixOps.EPS) {
          isZeroRow = false;
          break;
        }
      }
      if (!isZeroRow) {
        nonZeroRows.add(i);
      }
    }

    List<Integer> dependentVars = new ArrayList<>();
    List<Integer> freeVars = new ArrayList<>();
    int rowIdx = 0;
    for (int colIdx = 0; colIdx < n; colIdx++) {
      boolean isDependent = false;
      if (rowIdx < nonZeroRows.size()) {
        int r = nonZeroRows.get(rowIdx);
        if (Math.abs(M.get(r, colIdx) - 1.0) < MatrixOps.EPS) {
          dependentVars.add(colIdx);
          rowIdx++;
          isDependent = true;
        }
      }
      if (!isDependent) {
        freeVars.add(colIdx);
      }
    }

    String[] solution = new String[n];
    for (int i = 0; i < freeVars.size(); i++) {
      int varIndex = freeVars.get(i);
      solution[varIndex] = "s" + (paramIndex++);
    }

    for (int i = dependentVars.size() - 1; i >= 0; i--) {
      int varIndex = dependentVars.get(i);
      StringBuilder expr = new StringBuilder();
      double rhs = M.get(i, n);

      if (Math.abs(rhs) > MatrixOps.EPS) {
        expr.append(NumberFmt.format3(rhs));
      }

      for (int j = varIndex + 1; j < n; j++) {
        double coef = M.get(i, j);
        if (Math.abs(coef) > MatrixOps.EPS) {
          if (expr.length() > 0 && coef < 0) {
            expr.append(" + ");
          } else if (expr.length() > 0) {
            expr.append(" - ");
          } else if (coef < 0) {
            expr.append("-");
          }

          if (Math.abs(coef) != 1.0) {
            expr.append(NumberFmt.format3(Math.abs(coef)));
          }

          if (freeVars.contains(j)) {
            expr.append(solution[j]);
          } else {
            expr.append("x").append(j + 1);
          }
        }
      }
      solution[varIndex] = expr.toString();
    }

    for (int i = 0; i < n; i++) {
      System.out.println("x" + (i + 1) + " = " + solution[i]);
    }
  }
}
