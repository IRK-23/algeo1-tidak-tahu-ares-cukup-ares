package algeo.spl;

import algeo.core.*;
import algeo.determinant.*;
import algeo.io.*;

public class Cramer {
  public static void cramer() {
    Matrix M = MatrixIO.inputMatrix();
    solveCramer(M);
  }

  public static void solveCramer(Matrix M) {
    int rows = M.rows();
    int cols = M.cols();

    Matrix A = M.submatrix(0, rows - 1, 0, cols - 2);
    Matrix B = M.colAsMatrix(cols - 1);

    // Pastikan matriks koefisien adalah matriks persegi
    if (!A.isSquare()) {
      System.out.println(
          "Kaidah Cramer hanya berlaku untuk sistem dengan matriks koefisien persegi.");
      return;
    }

    double detA = CofactorDeterminant.of(A);

    for (int j = 0; j < rows; j++) {
      Matrix Aj = A.copy();
      for (int i = 0; i < rows; i++) {
        Aj.set(i, j, B.get(i, 0));
      }

      double detAj = CofactorDeterminant.of(Aj);

      double xj = detAj / detA;

      System.out.println("Solusi: x" + (j + 1) + " = " + NumberFmt.format3(xj));
    }
  }
}
