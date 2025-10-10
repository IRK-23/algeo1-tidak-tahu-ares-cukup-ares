package algeo.spl;

import algeo.core.*;
import algeo.io.*;
import java.io.IOException;
import java.util.Scanner;

public class GaussJordan {
  private static final Scanner sc = new Scanner(System.in);

  public static void gaussjordan() {
    System.out.println("\n=== Eliminasi Gauss-Jordan (RREF) ===");

    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix M = null;

    // Input loop
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
            if (sw) choice = UiPrompts.InputChoice.MANUAL; else { System.out.println("Operasi dibatalkan."); return; }
          }
        }
      }
    }

    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);
    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    out.append("Metode penyelesaian SPL: Gauss-Jordan (RREF)").append(nl).append(nl);
    out.append("Input yang digunakan (A|b):").append(nl).append(M).append(nl);

    switch (solutionType) {
      case 0 -> {
        String msg = "Tidak ada solusi (sistem inkonsisten).";
        System.out.println("\n" + msg);
        out.append("Hasil SPL: ").append(msg).append(nl);
      }
      case 1 -> {
        System.out.println("\nSolusi tunggal:");
        Matrix mRref = MatrixOps.rref(M);
        String sol = Gauss.finishSPL(mRref);
        System.out.println(sol);
        out.append("Hasil SPL (solusi tunggal):").append(nl).append(sol);
      }
      default -> {
        System.out.println("\nSolusi banyak (parametrik):");
        Matrix mRref = MatrixOps.rref(M);
        String sol = Gauss.finishParametricSPL(mRref);
        System.out.println(sol);
        out.append("Hasil SPL (banyak solusi):").append(nl).append(sol);
      }
    }

    // simpan (format SPL)
    ResultSaver.maybeSaveText(sc, "spl_gaussjordan", "Hasil SPL - Gauss-Jordan", out.toString());
  }
}
