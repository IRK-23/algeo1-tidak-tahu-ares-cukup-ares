package algeo.core;

public final class MatrixOps {
  private MatrixOps() {}

  /** Epsilon default untuk komputasi numerik. */
  public static final double EPS = 1e-12;

  /* ===========  REF  ============= */

  /**
   * Ubah matriks menjadi REF (row echelon form) in-place.
   *
   * @param m matriks yang akan direduksi (diubah in-place)
   * @param pivoting true untuk partial pivoting (disarankan), false jika tidak
   * @param eps toleransi nol numerik
   * @return jumlah pivot (perkiraan rank setelah REF)
   */
  public static int ref(Matrix m, boolean pivoting, double eps) {
    final int R = m.rows();
    final int C = m.cols();
    int r = 0; // baris aktif (tempat menaruh pivot berikutnya)
    int pivots = 0;

    for (int c = 0; c < C && r < R; c++) {
      int pivotRow = r;
      double best = Math.abs(m.get(pivotRow, c));

      if (pivoting) {
        for (int rr = r + 1; rr < R; rr++) {
          double v = Math.abs(m.get(rr, c));
          if (v > best) {
            best = v;
            pivotRow = rr;
          }
        }
      }

      // Jika kolom ini ~ nol semua (di bawah baris r), lanjut ke kolom berikut
      if (best <= eps) continue;

      // Letakkan pivot terbesar di baris r
      if (pivotRow != r) m.swapRows(pivotRow, r);

      // Eliminasi ke bawah
      double pivotVal = m.get(r, c);
      if (Math.abs(pivotVal) <= eps) continue;
      for (int rr = r + 1; rr < R; rr++) {
        double factor = m.get(rr, c) / pivotVal;
        if (Math.abs(factor) <= eps) continue;
        // rr <- rr - factor * r
        m.addRowMultiple(rr, r, -factor);
        m.set(rr, c, 0.0);
      }

      r++;
      pivots++;
    }
    return pivots;
  }

  /** Overload dengan EPS default dan pivoting = true. */
  public static int ref(Matrix m) {
    return ref(m, true, EPS);
  }

  /* ========  RREF (Gauss–Jordan) ======= */

  /**
   * Ubah matriks menjadi RREF (reduced row echelon form) in-place.
   *
   * @param m matriks (diubah in-place)
   * @param pivoting true untuk partial pivoting
   * @param eps toleransi nol numerik
   */
  public static void rref(Matrix m, boolean pivoting, double eps) {
    final int R = m.rows();
    final int C = m.cols();
    int r = 0;

    for (int c = 0; c < C && r < R; c++) {
      int pivotRow = r;
      double best = Math.abs(m.get(pivotRow, c));

      if (pivoting) {
        for (int rr = r + 1; rr < R; rr++) {
          double v = Math.abs(m.get(rr, c));
          if (v > best) {
            best = v;
            pivotRow = rr;
          }
        }
      }

      if (best <= eps) continue;

      if (pivotRow != r) m.swapRows(pivotRow, r);

      // Normalisasi baris pivot agar pivot = 1
      double pv = m.get(r, c);
      if (Math.abs(pv) > eps && Math.abs(pv - 1.0) > eps) {
        double inv = 1.0 / pv;
        m.scaleRow(r, inv);
      }
      // Koreksi numerik
      m.set(r, c, 1.0);

      // Nolkan semua entri di kolom pivot selain baris r
      for (int rr = 0; rr < R; rr++) {
        if (rr == r) continue;
        double factor = m.get(rr, c);
        if (Math.abs(factor) <= eps) continue;
        m.addRowMultiple(rr, r, -factor);
        m.set(rr, c, 0.0);
      }

      r++;
    }
  }

  /** Overload RREF default: pivoting = true, eps = EPS. */
  public static void rref(Matrix m) {
    rref(m, true, EPS);
  }

  /* ==========  Rank  ============ */

  /** Hitung rank dengan melakukan REF pada salinan matriks. */
  public static int rank(Matrix A, boolean pivoting, double eps) {
    Matrix m = A.copy();
    return ref(m, pivoting, eps);
  }

  public static int rank(Matrix A) {
    return rank(A, true, EPS);
  }

  /* == Determinan via OBE (row-reduce) == */

  /**
   * Determinan dengan reduksi baris: - Track pertukaran baris (ubah tanda) - Track skala baris
   * (jarang diperlukan jika hanya eliminasi r <- r - k*rowPivot) - Hasil = produk diagonal atas
   * setelah REF dikali faktor tanda.
   *
   * <p>Catatan: - Tidak stabil untuk matriks singular ~epsilon; gunakan eps untuk deteksi nol. -
   * Operasi dilakukan pada salinan agar A tidak berubah.
   *
   * @param A matriks
   * @param pivoting true untuk partial pivoting
   * @param eps toleransi nol numerik
   */
  public static double determinantOBE(Matrix A, boolean pivoting, double eps) {
    if (!A.isSquare()) {
      throw new IllegalArgumentException("Determinan hanya untuk matriks persegi.");
    }
    Matrix m = A.copy();
    final int n = m.rows();

    double sign = 1.0; // flip -1 ketika swap baris
    int r = 0;

    for (int c = 0; c < n && r < n; c++) {
      // Cari pivot
      int pivotRow = r;
      double best = Math.abs(m.get(pivotRow, c));
      if (pivoting) {
        for (int rr = r + 1; rr < n; rr++) {
          double v = Math.abs(m.get(rr, c));
          if (v > best) {
            best = v;
            pivotRow = rr;
          }
        }
      }
      if (best <= eps) {
        // Kolom ~ nol, lanjut; ini menurunkan rank -> determinan akan 0
        continue;
      }

      if (pivotRow != r) {
        m.swapRows(pivotRow, r);
        sign *= -1.0;
      }

      // Eliminasi ke bawah
      double pv = m.get(r, c);
      for (int rr = r + 1; rr < n; rr++) {
        double factor = m.get(rr, c) / pv;
        if (Math.abs(factor) <= eps) continue;
        m.addRowMultiple(rr, r, -factor);
        m.set(rr, c, 0.0);
      }

      r++;
    }

    // Jika rank < n → determinan 0
    if (r < n) return 0.0;

    // Produk diagonal
    double det = sign;
    for (int i = 0; i < n; i++) {
      det *= m.get(i, i);
    }
    // Koreksi -0
    if (Math.abs(det) < eps) det = 0.0;
    return det;
  }

  public static double determinantOBE(Matrix A) {
    return determinantOBE(A, true, EPS);
  }
}
