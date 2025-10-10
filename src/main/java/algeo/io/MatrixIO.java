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
    int n = askInt(sc, "Masukkan n (1-" + MAX_MANUAL + "): ", 1, MAX_MANUAL);
    double[][] data = readRows(sc, n, n);
    return new Matrix(data);
  }

  public static Matrix inputAugmentedMatrix(Scanner sc) {
    System.out.println("\n== Input Matriks Augmented (n x (n + 1)) untuk SPL ==");
    int n = askInt(sc, "Masukkan n (1-" + MAX_MANUAL + "): ", 1, MAX_MANUAL);
    double[][] data = readRows(sc, n, n + 1);
    return new Matrix(data);
  }

  public static Matrix inputRectMatrix(Scanner sc) {
      System.out.println("\n== Input Matriks Umum (n x m) ==");
      int rows = askInt(sc, "Masukkan jumlah baris n (1-" + MAX_MANUAL + "): ", 1, MAX_MANUAL);
      int cols = askInt(sc, "Masukkan jumlah kolom m (1-" + MAX_MANUAL + "): ", 1, MAX_MANUAL);
      double[][] data = readRows(sc, rows, cols);
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

  public static Matrix readMatrixFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (r > MAX_FILE || c > MAX_FILE) {
      throw new IllegalArgumentException("Dimensi file melebihi batas " + MAX_FILE + "x" + MAX_FILE + " (diberikan: " + r + "x" + c + ")");
    }
    return new Matrix(data);
  }

  public static Matrix readSPLFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (c < 2) throw new IllegalArgumentException("Matriks augmented harus memiliki ≥ 2 kolom (A|b).");
    if (r > MAX_FILE || c > (MAX_FILE + 1)) {
      throw new IllegalArgumentException("Dimensi SPL melebihi batas ~ " + MAX_FILE + " baris dan " + (MAX_FILE + 1) + " kolom (diberikan: " + r + "x" + c + ")");
    }
    return new Matrix(data);
  }

  public static Matrix readDeterminantFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (r != c) throw new IllegalArgumentException("Matriks untuk determinan harus persegi (nxn). Diberikan: " + r + "x" + c);
    if (r > MAX_FILE) {
      throw new IllegalArgumentException("Dimensi determinan melebihi batas " + MAX_FILE + "x" + MAX_FILE + " (diberikan: " + r + "x" + c + ")");
    }
    return new Matrix(data);
  }

  public static Matrix readInverseFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (r != c) throw new IllegalArgumentException("Matriks untuk invers harus persegi (nxn). Diberikan: " + r + "x" + c);
    if (r > MAX_FILE) {
      throw new IllegalArgumentException("Dimensi invers melebihi batas " + MAX_FILE + "x" + MAX_FILE + " (diberikan: " + r + "x" + c + ")");
    }
    return new Matrix(data);
  }

  public static Matrix readInterpolationFromFile(String path) throws IOException {
    double[][] data = readNumericTable(path);
    int r = data.length, c = (r == 0) ? 0 : data[0].length;
    if (r == 0 || c == 0) throw new IllegalArgumentException("Berkas kosong atau tidak berisi angka: " + path);
    if (c != 2) throw new IllegalArgumentException("File interpolasi harus memiliki 2 kolom (x, y). Diberikan: " + c + " kolom");
    return new Matrix(data);
  }

  public static Object[] readRegressionFromFile(String path) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        String trimmed = line.trim();
        if (!trimmed.isEmpty()) {
          lines.add(trimmed);
        }
      }
    }

    if (lines.isEmpty()) {
      throw new IllegalArgumentException("File regresi kosong: " + path);
    }
    if (lines.size() < 2) {
      throw new IllegalArgumentException("File regresi harus memiliki minimal 2 baris (minimal 1 sampel + 1 baris derajat).");
    }

    // Baris terakhir adalah derajat
    String lastLine = lines.get(lines.size() - 1);
    int degree;
    try {
      degree = Integer.parseInt(lastLine);
      if (degree < 0 || degree > 4) {
        throw new IllegalArgumentException("Derajat polinom harus antara 0-4 (sesuai spesifikasi). Diberikan: " + degree);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Baris terakhir harus berisi derajat polinom (integer 0-4). Diberikan: '" + lastLine + "'");
    }

    // Baris sebelum terakhir adalah sampel data
    int numSamples = lines.size() - 1;
    List<double[]> rows = new ArrayList<>();
    int cols = -1;

    for (int i = 0; i < numSamples; i++) {
      String line = lines.get(i);
      String[] toks = line.split("\\s+");
      double[] row = new double[toks.length];
      
      for (int j = 0; j < toks.length; j++) {
        try {
          row[j] = NumberFmt.parseNumber(toks[j]);
        } catch (RuntimeException ex) {
          throw new IllegalArgumentException("Token tidak valid di baris " + (i + 1) + ": '" + toks[j] + "'");
        }
      }

      if (cols == -1) {
        cols = row.length;
        if (cols < 2) {
          throw new IllegalArgumentException("Setiap baris sampel harus memiliki minimal 2 kolom (≥1 fitur + 1 kolom y).");
        }
        int k = cols - 1; // jumlah variabel
        if (k > 5) {
          throw new IllegalArgumentException("Jumlah variabel (k) maksimal 5 (sesuai spesifikasi). Diberikan: " + k);
        }
      } else if (cols != row.length) {
        throw new IllegalArgumentException("Jumlah kolom tidak konsisten di baris " + (i + 1) + ". Ekspektasi: " + cols + ", diberikan: " + row.length);
      }

      rows.add(row);
    }

    if (rows.isEmpty()) {
      throw new IllegalArgumentException("Tidak ada sampel data yang valid di file.");
    }

    double[][] data = rows.toArray(new double[0][]);
    Matrix samples = new Matrix(data);

    // Validasi jumlah sampel berdasarkan k dan degree
    int k = cols - 1; // jumlah variabel
    int minSamples = calculateMinSamples(k, degree);
    if (numSamples < minSamples) {
      throw new IllegalArgumentException(
        String.format("Jumlah sampel tidak cukup! Untuk k=%d variabel dan derajat d=%d, butuh minimal %d sampel. Diberikan: %d sampel.",
          k, degree, minSamples, numSamples)
      );
    }

    return new Object[] { samples, degree };
  }

  private static int calculateMinSamples(int k, int d) {
    return binomialCoeff(d + k, k);
  }

  private static int binomialCoeff(int n, int k) {
    if (k > n) return 0;
    if (k == 0 || k == n) return 1;
    
    long result = 1;
    for (int i = 0; i < k; i++) {
      result = result * (n - i) / (i + 1);
    }
    return (int) result;
  }

  public static Matrix readAugmentedFromFile(String path) throws IOException {
    return readSPLFromFile(path);
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
        System.out.print("Baris " + (i+1) + "/" + rows + ": ");
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
          throw new IllegalArgumentException("Jumlah kolom tidak konsisten (baris " + lineno + "). Ekspektasi: " + cols + ", diberikan: " + row.length);
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