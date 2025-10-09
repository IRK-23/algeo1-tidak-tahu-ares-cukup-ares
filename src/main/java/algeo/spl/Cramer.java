package algeo.spl;

import algeo.core.*;
import algeo.determinant.*;
import algeo.io.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Cramer {
  public static void cramer() {
    Matrix M = MatrixIO.inputAugmentedMatrix();
    PrintStream originalOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    System.setOut(new PrintStream(bos));

    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);

    System.setOut(originalOut);

    if (solutionType == 1) {
      System.out.println("Solusi tunggal:");
      solveCramer(M);
      System.out.println();

    } else { // solutionType == 2
      System.out.println(
          "Determinan nol, maka tidak dapat menggunakan Invers, silakan gunakan metode Gauss"
              + " atau Gauss-Jordan");
    }
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
