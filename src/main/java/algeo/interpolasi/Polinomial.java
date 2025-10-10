package algeo.interpolasi;

import algeo.core.*;
import algeo.io.MatrixIO;
import algeo.io.ResultSaver;
import algeo.io.UiPrompts;
import java.io.IOException;
import java.util.Scanner;

public class Polinomial {
  private static final Scanner sc = new Scanner(System.in);

  public static void polinomial() {
    System.out.println("\n=== Interpolasi Polinomial ===");

    UiPrompts.InputChoice choice = UiPrompts.askInputChoice(sc);
    Matrix samples = null;

    while (samples == null) {
      if (choice == UiPrompts.InputChoice.MANUAL) {
        samples = inputTitikSampelManual();
      } else {
        String path = UiPrompts.askPath(sc, "Masukkan path file interpolasi (.txt): ");
        try {
          // catatan: jika spesifikasinya TIDAK membatasi 10 titik, hilangkan validasi 10 di MatrixIO.readInterpolationFromFile
          samples = MatrixIO.readInterpolationFromFile(path);
          System.out.println("✓ File berhasil dibaca: " + samples.rows() + " titik (x,y)");
        } catch (IOException | IllegalArgumentException ex) {
          System.out.println("Patc" + ex.getMessage());
          boolean retry = UiPrompts.askYesNo(sc, "Coba file lain? (y/n): ");
          if (!retry) {
            boolean sw = UiPrompts.askYesNo(sc, "Beralih ke input manual? (y/n): ");
            if (sw) choice = UiPrompts.InputChoice.MANUAL; else { System.out.println("Operasi dibatalkan."); return; }
          }
        }
      }
    }

    final int n = samples.rows();
    // Bangun Vandermonde A dan Y
    Matrix A = new Matrix(n, n);
    Matrix Y = new Matrix(n, 1);
    for (int i = 0; i < n; i++) {
      double x = samples.get(i, 0);
      double y = samples.get(i, 1);
      for (int j = 0; j < n; j++) A.set(i, j, Math.pow(x, j));
      Y.set(i, 0, y);
    }
    Matrix aug = A.augment(Y);

    // cek det(A) via REF/RREF (opsional di output)
    Matrix rref = MatrixOps.rref(aug);

    // Ambil koefisien a0..a_{n-1}
    double[] coef = new double[n];
    for (int i = 0; i < n; i++) coef[i] = rref.get(i, n);

    // format persamaan
    StringBuilder poly = new StringBuilder("y(x) = ");
    boolean firstTerm = true;
    for (int i = 0; i < n; i++) {
      double c = coef[i];
      if (Math.abs(c) <= MatrixOps.EPS) continue;
      String cstr = NumberFmt.format3(Math.abs(c));
      if (!firstTerm) poly.append(c >= 0 ? " + " : " - ");
      else { if (c < 0) poly.append("-"); firstTerm = false; }
      if (i == 0) poly.append(cstr);
      else if (i == 1) poly.append(cstr).append("x");
      else poly.append(cstr).append("x^").append(i);
    }
    if (firstTerm) poly.append(NumberFmt.format3(0.0));

    System.out.println("\nPersamaan hasil:");
    System.out.println(poly);

    // domain interpolasi (sesuai spesifikasi): aku set ke [min(x), max(x)] dari sampel
    double xmin = samples.get(0,0), xmax = samples.get(0,0);
    for (int i = 1; i < n; i++) {
      double x = samples.get(i,0);
      if (x < xmin) xmin = x;
      if (x > xmax) xmax = x;
    }
    String domainStr = "[" + NumberFmt.format3(xmin) + ", " + NumberFmt.format3(xmax) + "]";

    // siapkan isi file
    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    out.append("Metode interpolasi: Polinomial (Vandermonde)").append(nl).append(nl);
    out.append("Input yang digunakan (x, y):").append(nl).append(samples).append(nl);
    out.append("Domain interpolasi: ").append(domainStr).append(nl).append(nl);
    out.append("Persamaan hasil: ").append(poly).append(nl);

    ResultSaver.maybeSaveText(sc, "interpolasi_polinomial", "Hasil Interpolasi Polinomial", out.toString());

    // evaluasi opsional
    while (true) {
      System.out.print("\nMasukkan nilai x untuk dievaluasi (atau -9999 untuk kembali): ");
      String t = sc.nextLine().trim();
      if (t.equals("-9999")) break;
      try {
        double x = NumberFmt.parseNumber(t);
        double y = 0.0;
        for (int i = 0; i < n; i++) y += coef[i] * Math.pow(x, i);
        System.out.println("y(" + NumberFmt.format3(x) + ") = " + NumberFmt.format3(y));
      } catch (RuntimeException ex) {
        System.out.println("Input tidak valid. Contoh: 2.5, 3/4, -7/3.");
      }
    }
  }

  public static Matrix inputTitikSampelManual() {
    int n = UiPrompts.askInt(sc, "Masukkan jumlah titik sampel: ", 1, MatrixIO.MAX_MANUAL);
    double[][] data = new double[n][2];
    System.out.println("Masukkan tiap baris sebagai: x y");
    for (int i = 0; i < n; i++) {
      while (true) {
        System.out.print("Baris " + (i+1) + "/" + n + ": ");
        String line = sc.nextLine().trim();
        String[] toks = line.split("\\s+");
        if (toks.length != 2) { System.out.println("Format salah. Harus dua angka: x y"); continue; }
        try {
          data[i][0] = NumberFmt.parseNumber(toks[0]);
          data[i][1] = NumberFmt.parseNumber(toks[1]);
          break;
        } catch (RuntimeException ex) {
          System.out.println("Token tidak valid. Contoh: 2,5 | 2.5 | 3/4 | -7/3");
        }
      }
    }
    return new Matrix(data);
  }
}
