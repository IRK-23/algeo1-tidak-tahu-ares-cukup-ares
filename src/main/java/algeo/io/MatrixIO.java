package algeo.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import algeo.core.Matrix;
import algeo.core.NumberFmt;

public class MatrixIO {
  private MatrixIO() {}

  public static final int MAX_MANUAL = 11;
  public static final int MAX_FILE = 1001;

  private static final Scanner INTERNAL_SCANNER = new Scanner(System.in);

  public static Matrix inputMatrix() { 
    return inputMatrix(INTERNAL_SCANNER); 
  }

  public static Matrix inputAugmentedMatrix() { 
    return inputAugmentedMatrix(INTERNAL_SCANNER); 
  }

  public static Matrix readMatrixFromUser() {
    return readMatrixFromUser(INTERNAL_SCANNER);
  }

  public static Matrix inputMatrix(Scanner sc) {
    System.out.println("\n== Input Matriks (n x n) ==");
    int n = askInt(sc, "Masukkan n (1-" + MAX_MANUAL + "):", 1, MAX_MANUAL);
    double[][] data = readRows(sc, n, n);
    return new Matrix(data);
  }

  public static Matrix inputAugmentedMatrix(Scanner sc) {
    System.out.println("\n== Input Matriks Augmented (n x (n + 1)) untuk SPL ==");
    int n = askInt(sc, "Masukkan n (1-" + MAX_MANUAL + "): ", 1, MAX_MANUAL);
    double[][] data = readRows(sc, n, n + 1);
    return new Matrix(data);
  }

  public static Matrix readMatrixFromUser(Scanner sc) {
    System.out.println("\n== Baca Matriks dari File ==");
    while (true) {
      System.out.print("Masukkan path file .txt yang ingin dibaca: ");
      String path = sc.nextLine().trim();

      if (path.isEmpty()) {
        System.out.println("Path tidak boleh kosong.\n");
        continue;
      }

      try {
        Matrix m = readMatrixFromFile(path);
        System.out.println("\nMatriks berhasil dibaca dari: " + path);
        return m;
      } catch (IOException | IllegalArgumentException e) {
        System.out.println("Gagal membaca file: " + e.getMessage());
        System.out.println("Silakan coba lagi.\n");
      }
    }
  }

  public static Matrix readAugmentedFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (c < 2) throw new IllegalArgumentException("Matriks augmented harus memiliki ≥ 2 kolom (A|b).");
    if (r > MAX_FILE || c > (MAX_FILE + 1)) {
      throw new IllegalArgumentException("Dimensi augmented melebihi batas ~ " + MAX_FILE + " baris dan " + (MAX_FILE + 1) + " kolom (diberikan: " + r + "×" + c + ")");
    }
    return new Matrix(data);
  }

  public static Matrix readMatrixFromFile(String path) throws IOException {
        double[][] data = readNumericTable(path);
        int r = data.length, c = (r == 0) ? 0 : data[0].length;
        if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
        if (r > MAX_FILE || c > MAX_FILE) {
          throw new IllegalArgumentException("Dimensi file melebihi batas " + MAX_FILE + "×" + MAX_FILE + " (diberikan: " + r + "×" + c + ")");
        }
        return new Matrix(data);
    }

  private static int askInt(Scanner sc, String prompt, int min, int max) {
    while (true) {
      System.out.print(prompt);
      String tok = sc.nextLine().trim();
      try {
        int v = Integer.parseInt(tok);
        if (v < min || v > max) {
          System.out.printf("Nilai harus di antara %d dan %d.%n", min, max);
          continue;
        }
        return v;
      } catch (NumberFormatException e) {
        System.out.println("Input tidak valid. Harap masukkan bilangan bulat.");
      }
    }
  }

  private static double[][] readRows(Scanner sc, int rows, int cols) {
    System.out.println("Masukkan elemen matriks baris demi baris (pisahkan elemen dengan spasi):");

    double[][] data = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      while (true) {
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
          System.out.println("Baris tidak boleh kosong.");
          continue;
        }
        String[] toks = line.split("\\s+");
        if (toks.length != cols) {
          System.out.printf("Jumlah elemen (%d) tidak sesuai dengan kolom (%d). Coba lagi.%n", toks.length, cols);
          continue;
        }
        try {
          for (int j = 0; j < cols; j++) {
            data[i][j] = NumberFmt.parseNumber(toks[j]);
          }
          break;
        } catch (RuntimeException e) {
          System.out.println("Ada token yang tidak valid. Contoh valid: 2,5  |  2.5  |  3/4  |  -7/3");
        }
      }
    }
    return data;
  }

  private static double[][] readNumericTable(String path) throws IOException {
    List<double[]> rows = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
      String line;
      int cols = -1;
      int lineno = 0;
      while ((line = br.readLine()) != null) {
        lineno++;
        String s = line.trim();
        if (s.isEmpty()) continue;

        String[] toks = s.split("\\s+");
        double[] row = new double[toks.length];
        for (int i = 0; i < toks.length; i++) {
          try {
              row[i] = NumberFmt.parseNumber(toks[i]);
          } catch (RuntimeException ex) {
              throw new IllegalArgumentException("Token tidak valid di baris " + lineno + ": '" + toks[i] + "'");
          }
        }

        if (cols == -1) cols = row.length;
        else if (cols != row.length) {
          throw new IllegalArgumentException("Jumlah kolom tidak konsisten (baris " + lineno + ").");
        }

        rows.add(row);
        if (rows.size() > MAX_FILE) {
          throw new IllegalArgumentException("Jumlah baris melebihi batas file: " + MAX_FILE);
        }
      }
    }
    return rows.toArray(new double[0][]);
  }
}
