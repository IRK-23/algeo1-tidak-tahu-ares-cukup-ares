package algeo.core;

/**
 * Utility kecil untuk menghitung determinan menggunakan MatrixOps.
 * Menyediakan wrapper yang mudah dipanggil dari kode lain.
 */
public final class Determinant {
    private Determinant() {}

    /**
     * Hitung determinan matriks (menggunakan reduksi baris MatrixOps.determinantOBE).
     * @param A matriks persegi
     * @return nilai determinan
     */
    public static double of(Matrix A) {
        return MatrixOps.determinantOBE(A);
    }

    /**
     * Versi dengan opsi pivoting dan toleransi eps.
     */
    public static double of(Matrix A, boolean pivoting, double eps) {
        return MatrixOps.determinantOBE(A, pivoting, eps);
    }

    /**
     * Kembalikan determinan terformat (3 desimal) untuk keperluan tampilan.
     */
    public static String ofFormatted(Matrix A) {
        return NumberFmt.format3(of(A));
    }
}
