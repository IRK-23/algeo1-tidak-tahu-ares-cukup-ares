package algeo.core;

public class Matrix {

  // ---- Field ----

  private final int rows;
  private final int cols;
  private final double[][] a;

  // ---- Konstruktor ----

  /** Buat matriks r x c terisi 0. */
  public Matrix(int rows, int cols) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Ukuran matriks harus positif (rows > 0, cols > 0).");
    }
    this.rows = rows;
    this.cols = cols;
    this.a = new double[rows][cols];
  }

  /** Buat dari array sumber (deep copy). */
  public Matrix(double[][] data) {
    if (data == null || data.length == 0 || data[0].length == 0) {
      throw new IllegalArgumentException("Array sumber tidak valid.");
    }
    this.rows = data.length;
    this.cols = data[0].length;
    this.a = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
      if (data[i].length != cols) {
        throw new IllegalArgumentException("Semua baris harus memiliki jumlah kolom yang sama.");
      }
      System.arraycopy(data[i], 0, this.a[i], 0, cols);
    }
  }

  /** Pabrik identitas n x n. */
  public static Matrix identity(int n) {
    if (n <= 0) throw new IllegalArgumentException("Ukuran identitas harus positif.");
    Matrix I = new Matrix(n, n);
    for (int i = 0; i < n; i++) I.a[i][i] = 1.0;
    return I;
  }

    public static int sumRows(Matrix M, int n) {
        int sum = 0;
        for (int i = 0; i < M.cols(); i++) {
            sum += M.get(n, i);
        }
        return sum;
    }

  // ---- Ukuran & properti ----

  public int rows() {
    return rows;
  }

  public int cols() {
    return cols;
  }

  public boolean isSquare() {
    return rows == cols;
  }

  // ---- Akses elemen ----

  public double get(int r, int c) {
    checkIndex(r, c);
    return a[r][c];
  }

  public void set(int r, int c, double val) {
    checkIndex(r, c);
    a[r][c] = val;
  }

  private void checkIndex(int r, int c) {
    if (r < 0 || r >= rows || c < 0 || c >= cols) {
      throw new IndexOutOfBoundsException("Indeks di luar batas: (" + r + "," + c + ")");
    }
  }

  private void checkRow(int r) {
    if (r < 0 || r >= rows) throw new IndexOutOfBoundsException("Baris tidak valid: " + r);
  }

  private void checkCol(int c) {
    if (c < 0 || c >= cols) throw new IndexOutOfBoundsException("Kolom tidak valid: " + c);
  }

  // ---- Utilitas umum ----

  /** Deep copy. */
  public Matrix copy() {
    Matrix m = new Matrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      System.arraycopy(this.a[i], 0, m.a[i], 0, cols);
    }
    return m;
  }

  /** Transpose (menghasilkan copy matrix baru). */
  public Matrix transpose() {
    Matrix t = new Matrix(cols, rows);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        t.a[j][i] = this.a[i][j];
      }
    }
    return t;
  }

  /** Submatrix salinan baru: baris [r0..r1], kolom [c0..c1] (inklusif). */
  public Matrix submatrix(int r0, int r1, int c0, int c1) {
    if (r0 < 0 || r1 >= rows || r0 > r1 || c0 < 0 || c1 >= cols || c0 > c1) {
      throw new IllegalArgumentException("Rentang submatrix tidak valid.");
    }
    Matrix m = new Matrix(r1 - r0 + 1, c1 - c0 + 1);
    for (int i = r0, ii = 0; i <= r1; i++, ii++) {
      for (int j = c0, jj = 0; j <= c1; j++, jj++) {
        m.a[ii][jj] = this.a[i][j];
      }
    }
    return m;
  }

  /** Ambil satu baris sebagai matriks 1×cols (salinan). */
  public Matrix rowAsMatrix(int r) {
    checkRow(r);
    Matrix m = new Matrix(1, cols);
    System.arraycopy(a[r], 0, m.a[0], 0, cols);
    return m;
  }

  /** Ambil satu kolom sebagai matriks rows×1 (salinan). */
  public Matrix colAsMatrix(int c) {
    checkCol(c);
    Matrix m = new Matrix(rows, 1);
    for (int i = 0; i < rows; i++) m.a[i][0] = a[i][c];
    return m;
  }

  /** Augment [this | B]; jumlah baris harus sama. */
  public Matrix augment(Matrix B) {
    if (B == null || this.rows != B.rows) {
      throw new IllegalArgumentException(
          "Augment: jumlah baris harus sama dan B tidak boleh null.");
    }
    Matrix m = new Matrix(this.rows, this.cols + B.cols);
    for (int i = 0; i < rows; i++) {
      System.arraycopy(this.a[i], 0, m.a[i], 0, this.cols);
      System.arraycopy(B.a[i], 0, m.a[i], this.cols, B.cols);
    }
    return m;
  }

  // ---- Operasi Baris Elementer (in-place) ----

  /** Tukar baris r1 dan r2. */
  public void swapRows(int r1, int r2) {
    if (r1 == r2) return;
    checkRow(r1);
    checkRow(r2);
    double[] tmp = a[r1];
    a[r1] = a[r2];
    a[r2] = tmp;
  }

  /** Skala baris r dengan faktor k. */
  public void scaleRow(int r, double k) {
    checkRow(r);
    for (int j = 0; j < cols; j++) a[r][j] *= k;
  }

  /** r ← r + k * src. */
  public void addRowMultiple(int r, int src, double k) {
    checkRow(r);
    checkRow(src);
    if (k == 0.0) return;
    double[] rr = a[r];
    double[] ss = a[src];
    for (int j = 0; j < cols; j++) rr[j] += k * ss[j];
  }

  // ---- Representasi string (delegasi ke NumberFmt) ----

  public void printMatrix(Matrix M) {
    System.out.println(M);
  }

  @Override
  public String toString() {
    // Representasi ringkas untuk debugging/CLI.
    // Formatting angka ditangani NumberFmt agar konsisten seluruh aplikasi.
    StringBuilder sb = new StringBuilder();
    final int pad = 14; // lebar kolom agar rapi saat dicetak
    for (int i = 0; i < rows; i++) {
      sb.append("[");
      for (int j = 0; j < cols; j++) {
        String s = NumberFmt.format3(a[i][j]);
        if (j + 1 < cols) {
          sb.append(rightPad(s, pad));
          sb.append("");
        } else {
          sb.append(s);
        }
      }
      sb.append("]\n");
    }
    return sb.toString();
  }

  private static String rightPad(String s, int width) {
    if (s.length() >= width) return s;
    StringBuilder sb = new StringBuilder(width);
    sb.append(s); // Tambahkan string terlebih dahulu
    for (int i = s.length(); i < width; i++) sb.append(' ');
    return sb.toString();
  }
}
