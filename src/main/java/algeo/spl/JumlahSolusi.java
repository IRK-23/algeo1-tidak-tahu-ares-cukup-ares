package algeo.spl;

import algeo.core.*;
import algeo.determinant.*;

public class JumlahSolusi {
  public static int cekJumlahSolusiM(Matrix M) {
    int rows = M.rows();
    int cols = M.cols();

    Matrix A = M.submatrix(0, rows - 1, 0, cols - 2);

    if (A.isSquare()) {
      double detA = CofactorDeterminant.of(A, MatrixOps.EPS);
      if (Math.abs(detA) > MatrixOps.EPS) {
        return 1; // Solusi Tunggal
      }
    }

    // Gunakan Eliminasi Gauss untuk menentukan jenis solusi
    Matrix M_copy = M.copy();
    Gauss.makeEchelon(M_copy);

    // Cek baris kontradiksi (Tidak Ada Solusi)
    for (int i = 0; i < rows; i++) {
      boolean isZeroRow = true;
      for (int j = 0; j < cols - 1; j++) {
        if (Math.abs(M_copy.get(i, j)) > MatrixOps.EPS) {
          isZeroRow = false;
          break;
        }
      }
      if (isZeroRow && Math.abs(M_copy.get(i, cols - 1)) > MatrixOps.EPS) {
        return 0; // Tidak Ada Solusi
      }
    }

    // Cek jumlah pivot untuk menentukan Solusi Tunggal atau Banyak Solusi
    int pivotCount = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols - 1; j++) {
        if (Math.abs(M_copy.get(i, j)) > MatrixOps.EPS) {
          pivotCount++;
          break;
        }
      }
    }

    if (pivotCount == cols - 1) {
      return 1; // Solusi Tunggal
    } else {
      return 2; // Solusi Banyak
    }
  }
}
