package algeo.io;

import algeo.core.Matrix;
import java.util.Scanner;

public class MatrixIO {
  // Scanner global untuk konsistensi dengan Menu
  private static Scanner scanner = new Scanner(System.in);
  
  public static Matrix inputMatrix() {

    System.out.print("Masukkan jumlah baris: ");
    int rows = scanner.nextInt();
    System.out.print("Masukkan jumlah kolom: ");
    int cols = scanner.nextInt();

    if (rows <= 0 || cols <= 0) {
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

    // JANGAN tutup scanner karena akan menutup System.in
    // scanner.close();
    return matrixIn;
  }
}
