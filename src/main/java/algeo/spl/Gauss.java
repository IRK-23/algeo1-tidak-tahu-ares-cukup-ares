package algeo.spl;

import algeo.core.*;
import algeo.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Gauss {
  private static final Scanner sc = new Scanner(System.in);

  public static void gauss() {
    System.out.println("\n=== Eliminasi Gauss ===");
    
    // Pilih input method
    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix M = null;
    
    while (M == null) {
      if (choice == UiPrompts.InputChoice.MANUAL) {
        M = MatrixIO.inputAugmentedMatrix(sc);
      } else { // FILE
        String path = UiPrompts.askPath(sc, "Masukkan path file SPL (.txt): ");
        try {
          M = MatrixIO.readSPLFromFile(path);
          System.out.println("File berhasil dibaca: " + M.rows() + "x" + M.cols() + " matriks augmented");
        } catch (IOException | IllegalArgumentException ex) {
          System.out.println("Gagal membaca file: " + ex.getMessage());
          boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
          if (!retry) {
            boolean switchManual = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
            if (switchManual) {
              choice = UiPrompts.InputChoice.MANUAL;
            } else {
              System.out.println("Operasi dibatalkan.");
              return;
            }
          }
        }
      }
    }

    // Cek jumlah solusi
    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);
    
    StringBuilder output = new StringBuilder();
    String nl = System.lineSeparator();
    
    // Header
    output.append("Metode: Eliminasi Gauss").append(nl);
    output.append(nl).append("Input Matriks Augmented:").append(nl);
    output.append(M.toString()).append(nl);
    output.append(nl);

    switch (solutionType) {
      case 0 -> {
        String msg = "Tidak ada solusi (sistem inkonsisten).";
        System.out.println("\n" + msg);
        output.append("Hasil: ").append(msg).append(nl);
      }
      case 1 -> {
        System.out.println("\nSolusi tunggal ditemukan:");
        output.append("Hasil: Solusi Tunggal").append(nl).append(nl);
        Matrix mRef = MatrixOps.ref(M);
        
        output.append("Matriks setelah eliminasi Gauss (REF):").append(nl);
        output.append(mRef.toString()).append(nl).append(nl);
        
        String solution = finishSPL(mRef);
        System.out.println(solution);
        output.append(solution);
      }
      default -> {
        System.out.println("\nSistem memiliki banyak solusi:");
        output.append("Hasil: Banyak Solusi (Parametrik)").append(nl).append(nl);
        Matrix mRef = MatrixOps.ref(M);
        
        output.append("Matriks setelah eliminasi Gauss (REF):").append(nl);
        output.append(mRef.toString()).append(nl).append(nl);
        
        String solution = finishParametricSPL(mRef);
        System.out.println(solution);
        output.append(solution);
      }
    }
    
    // Save hasil
    ResultSaver.maybeSaveText(sc, "spl_gauss", "Hasil SPL - Eliminasi Gauss", output.toString());
  }

  /**
   * Menyelesaikan SPL dengan solusi tunggal menggunakan substitusi mundur.
   * @return String berisi langkah-langkah substitusi
   */
  public static String finishSPL(Matrix M) {
    StringBuilder result = new StringBuilder();
    String nl = System.lineSeparator();
    
    int n = M.cols() - 1;
    double[] solution = new double[n];

    result.append("Substitusi Mundur (Back Substitution):").append(nl).append(nl);

    for (int i = n - 1; i >= 0; i--) {
      double rhs = M.get(i, n);
      double sum = 0;
      StringBuilder equation = new StringBuilder();

      // Build persamaan untuk baris ini
      for (int j = 0; j < n; j++) {
        double coef = M.get(i, j);
        if (Math.abs(coef) > MatrixOps.EPS) {
          if (equation.length() > 0 && coef > 0) {
            equation.append(" + ");
          } else if (equation.length() > 0 && coef < 0) {
            equation.append(" - ");
            coef = Math.abs(coef);
          } else if (coef < 0) {
            equation.append("-");
            coef = Math.abs(coef);
          }

          if (Math.abs(coef - 1.0) > MatrixOps.EPS) {
            equation.append(NumberFmt.format3(coef));
          }
          equation.append("x").append(j + 1);
        }
      }

      // Substitusi nilai yang sudah diketahui
      for (int j = i + 1; j < n; j++) {
        sum += M.get(i, j) * solution[j];
      }

      solution[i] = rhs - sum;

      result.append("Baris ").append(i + 1).append(": ");
      result.append(equation.toString()).append(" = ").append(NumberFmt.format3(rhs)).append(nl);
      
      if (i < n - 1) {
        result.append("  Substitusi nilai yang sudah diketahui: ");
        result.append("x").append(i + 1).append(" = ").append(NumberFmt.format3(rhs));
        if (Math.abs(sum) > MatrixOps.EPS) {
          result.append(" - (").append(NumberFmt.format3(sum)).append(")");
        }
        result.append(" = ").append(NumberFmt.format3(solution[i])).append(nl);
      } else {
        result.append("  x").append(i + 1).append(" = ").append(NumberFmt.format3(solution[i])).append(nl);
      }
      result.append(nl);
    }

    result.append("Solusi Akhir:").append(nl);
    for (int i = 0; i < n; i++) {
      result.append("x").append(i + 1).append(" = ").append(NumberFmt.format3(solution[i])).append(nl);
    }

    return result.toString();
  }

  /**
   * Menyelesaikan SPL dengan banyak solusi (parametrik).
   * @return String berisi solusi parametrik
   */
  public static String finishParametricSPL(Matrix M) {
    StringBuilder result = new StringBuilder();
    String nl = System.lineSeparator();
    
    int rows = M.rows();
    int cols = M.cols();
    int n = cols - 1;
    int paramIndex = 1;

    // Identifikasi baris non-zero
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

    // Identifikasi variabel dependen dan bebas (free)
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

    result.append("Variabel bebas (free variables): ");
    if (freeVars.isEmpty()) {
      result.append("tidak ada").append(nl);
    } else {
      for (int i = 0; i < freeVars.size(); i++) {
        if (i > 0) result.append(", ");
        result.append("x").append(freeVars.get(i) + 1);
      }
      result.append(nl);
    }
    result.append(nl);

    // Assign parameter untuk variabel bebas
    String[] solution = new String[n];
    for (int i = 0; i < freeVars.size(); i++) {
      int varIndex = freeVars.get(i);
      String param = "s" + paramIndex++;
      solution[varIndex] = param;
      result.append("x").append(varIndex + 1).append(" = ").append(param).append(nl);
    }
    
    if (!freeVars.isEmpty()) result.append(nl);

    // Ekspresikan variabel dependen dalam terms of variabel bebas
    for (int i = dependentVars.size() - 1; i >= 0; i--) {
      int varIndex = dependentVars.get(i);
      StringBuilder expr = new StringBuilder();
      double rhs = M.get(i, n);

      // Konstanta
      if (Math.abs(rhs) > MatrixOps.EPS) {
        expr.append(NumberFmt.format3(rhs));
      }

      // Terms untuk variabel lain
      for (int j = varIndex + 1; j < n; j++) {
        double coef = M.get(i, j);
        if (Math.abs(coef) > MatrixOps.EPS) {
          if (expr.length() > 0) {
            expr.append(coef < 0 ? " + " : " - ");
          } else if (coef < 0) {
            expr.append("-");
          }

          double absCoef = Math.abs(coef);
          if (Math.abs(absCoef - 1.0) > MatrixOps.EPS) {
            expr.append(NumberFmt.format3(absCoef));
          }

          if (freeVars.contains(j)) {
            expr.append(solution[j]);
          } else {
            expr.append("x").append(j + 1);
          }
        }
      }
      
      solution[varIndex] = expr.length() > 0 ? expr.toString() : "0";
    }

    result.append("Solusi Parametrik:").append(nl);
    for (int i = 0; i < n; i++) {
      result.append("x").append(i + 1).append(" = ").append(solution[i]).append(nl);
    }

    return result.toString();
  }
}