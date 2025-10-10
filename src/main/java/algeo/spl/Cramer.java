package algeo.spl;

import algeo.core.*;
import algeo.determinant.*;
import algeo.io.*;

import java.io.IOException;
import java.util.Scanner;

public class Cramer {
  private static final Scanner sc = new Scanner(System.in);

  public static void cramer() {
    System.out.println("\n=== Kaidah Cramer ===");
    
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
    output.append("Metode: Kaidah Cramer").append(nl);
    output.append(nl).append("Input Matriks Augmented:").append(nl);
    output.append(M.toString()).append(nl);
    output.append(nl);
    
    if (solutionType == 0) {
      String msg = "Tidak ada solusi (sistem inkonsisten).";
      System.out.println("\n" + msg);
      output.append("Hasil: ").append(msg).append(nl);
      
    } else if (solutionType == 2) {
      String msg = "Sistem memiliki banyak solusi (infinite solutions).\n" +
                   "Kaidah Cramer hanya berlaku untuk sistem dengan solusi tunggal.\n" +
                   "Silakan gunakan metode Gauss atau Gauss-Jordan.";
      System.out.println("\n" + msg);
      output.append("Hasil: ").append(msg).append(nl);
      
    } else { // solutionType == 1
      System.out.println("\nSolusi tunggal ditemukan:");
      output.append("Hasil: Solusi Tunggal").append(nl).append(nl);
      
      String solution = solveCramer(M);
      System.out.println(solution);
      output.append(solution);
    }
    
    // Save hasil
    ResultSaver.maybeSaveText(sc, "spl_cramer", "Hasil SPL - Kaidah Cramer", output.toString());
  }

  /**
   * Menyelesaikan SPL menggunakan Kaidah Cramer.
   * @return String berisi langkah-langkah dan solusi
   */
  public static String solveCramer(Matrix M) {
    StringBuilder result = new StringBuilder();
    String nl = System.lineSeparator();
    
    int rows = M.rows();
    int cols = M.cols();
    
    // Ekstrak A dan b
    Matrix A = M.submatrix(0, rows - 1, 0, cols - 2);
    Matrix b = M.colAsMatrix(cols - 1);
    
    // Validasi matriks persegi
    if (!A.isSquare()) {
      result.append("ERROR: Kaidah Cramer hanya berlaku untuk matriks koefisien persegi.").append(nl);
      return result.toString();
    }
    
    int n = A.rows();
    
    // Hitung det(A)
    double detA = CofactorDeterminant.of(A);
    result.append("Langkah 1: Hitung determinan matriks koefisien A").append(nl);
    result.append("det(A) = ").append(NumberFmt.format3(detA)).append(nl).append(nl);
    
    if (Math.abs(detA) < MatrixOps.EPS) {
      result.append("det(A) = 0, sistem tidak dapat diselesaikan dengan Cramer.").append(nl);
      return result.toString();
    }
    
    result.append("Langkah 2: Untuk setiap variabel x_i, ganti kolom ke-i dari A dengan vektor b").append(nl);
    result.append("           kemudian hitung x_i = det(A_i) / det(A)").append(nl).append(nl);
    
    // Hitung setiap x_i
    double[] solutions = new double[n];
    for (int j = 0; j < n; j++) {
      Matrix Aj = A.copy();
      
      // Ganti kolom j dengan b
      for (int i = 0; i < n; i++) {
        Aj.set(i, j, b.get(i, 0));
      }
      
      double detAj = CofactorDeterminant.of(Aj);
      solutions[j] = detAj / detA;
      
      result.append("x").append(j + 1).append(":").append(nl);
      result.append("  det(A").append(j + 1).append(") = ").append(NumberFmt.format3(detAj)).append(nl);
      result.append("  x").append(j + 1).append(" = det(A").append(j + 1).append(") / det(A) = ");
      result.append(NumberFmt.format3(detAj)).append(" / ").append(NumberFmt.format3(detA));
      result.append(" = ").append(NumberFmt.format3(solutions[j])).append(nl).append(nl);
    }
    
    result.append("Solusi Akhir:").append(nl);
    for (int i = 0; i < n; i++) {
      result.append("x").append(i + 1).append(" = ").append(NumberFmt.format3(solutions[i])).append(nl);
    }
    
    return result.toString();
  }
}