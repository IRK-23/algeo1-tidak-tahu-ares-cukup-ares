package algeo.core;

/**
 * Kelas utilitas untuk menghitung inverse matriks.
 * Implementasi menggunakan augment dengan identitas dan RREF (Gauss–Jordan) dari MatrixOps.
 */
public final class Inverse {
    private Inverse() {}

    /**
     * Kembalikan inverse dari A sebagai matriks baru.
     * Jika matriks tidak persegi atau singular (determinant ~ 0) akan melempar IllegalArgumentException.
     * @param A matriks persegi
     * @return new Matrix yang merupakan inverse A
     */
    public static Matrix of(Matrix A) {
        return of(A, true, MatrixOps.EPS);
    }

    /**
     * Versi yang menerima opsi pivoting dan eps numerik.
     */
    public static Matrix of(Matrix A, boolean pivoting, double eps) {
        if (A == null) throw new IllegalArgumentException("Matrix A tidak boleh null");
        if (!A.isSquare()) throw new IllegalArgumentException("Inverse hanya untuk matriks persegi");

        // Cek determinan cepat
        double det = MatrixOps.determinantOBE(A, pivoting, eps);
        if (Math.abs(det) <= eps) {
            throw new IllegalArgumentException("Matriks singular (determinant ~ 0), tidak memiliki inverse.");
        }

        int n = A.rows();
        Matrix I = Matrix.identity(n);
        Matrix aug = A.copy().augment(I);

        // Lakukan RREF pada augmented matrix
        MatrixOps.rref(aug, pivoting, eps);

        // Setelah RREF, left side harus menjadi identitas; right side adalah inverse
        Matrix inv = aug.submatrix(0, n - 1, n, 2 * n - 1);
        return inv;
    }
}
