package algeo.io;

import algeo.core.Matrix;
import java.util.Scanner;

public class MatrixIO {
  public static Matrix inputMatrix() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Masukkan jumlah baris: ");
    int rows = scanner.nextInt();
    System.out.println("Masukkan jumlah kolom: ");
    int cols = scanner.nextInt();

    if (rows <= 0 || cols <= 0) {
      scanner.close();
      throw new IllegalArgumentException("Jumlah baris dan kolom harus lebih dari 0.");
    }

    double[][] doublearrayIn = new double[rows][cols];

    System.out.println("Masukkan elemen matriks:");
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        System.out.printf("Elemen [%d][%d]: ", i, j);
        doublearrayIn[i][j] = scanner.nextDouble();
      }
    }

    Matrix matrixIn = new Matrix(doublearrayIn);

    scanner.close();
    return matrixIn;
  }
}
