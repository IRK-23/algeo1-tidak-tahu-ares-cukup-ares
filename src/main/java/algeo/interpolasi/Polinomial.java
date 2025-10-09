package algeo.interpolasi;

import algeo.core.*;
import algeo.spl.*;
import java.util.Scanner;

public class Polinomial {
  public static void polinomial() {
    double[][] data = inputTitikSampel();
    int n = data.length;

    Matrix A = new Matrix(n, n);

    for (int i = 0; i < n; i++) {
      double x = data[i][0];
      for (int j = 0; j < n; j++) {
        A.set(i, j, Math.pow(x, j));
      }
    }

    Matrix Y = new Matrix(n, 1);
    for (int i = 0; i < n; i++) {
      Y.set(i, 0, data[i][1]);
    }

    Matrix augmentedM = A.augment(Y);
    System.out.println(augmentedM);
    GaussJordan.makeReductedEchelon(augmentedM);
    Gauss.finishSPL(augmentedM);

    double[] coefficients = new double[n];
    for (int i = 0; i < n; i++) {
      coefficients[i] = augmentedM.get(i, n);
    }

    StringBuilder polynomial = new StringBuilder("y(x) = ");
    for (int i = 0; i < n; i++) {
      double coeff = coefficients[i];
      if (Math.abs(coeff) > MatrixOps.EPS) {
        if (i > 0 && coeff > 0) {
          polynomial.append(" + ");
        } else if (i > 0 && coeff < 0) {
          polynomial.append(" - ");
        }

        double absCoeff = Math.abs(coeff);
        if (i == 0) {
          polynomial.append(NumberFmt.format3(absCoeff));
        } else if (i == 1) {
          polynomial.append(NumberFmt.format3(absCoeff)).append("x");
        } else {
          polynomial.append(NumberFmt.format3(absCoeff)).append("x^").append(i);
        }
      }
    }

    System.out.println("\nPersamaan Polinomial: ");
    System.out.println(polynomial.toString().replace(" - -", " + "));

    Scanner scanner = new Scanner(System.in);
    System.out.print("\nMasukkan nilai x untuk dievaluasi (-9999 untuk keluar): ");

    while (scanner.hasNextDouble()) {
      double x = scanner.nextDouble();

      try {
        if (x == -9999) {
          break;
        }

        double result = 0;
        for (int i = 0; i < coefficients.length; i++) {
          result += coefficients[i] * Math.pow(x, i);
        }
        System.out.println("y(" + NumberFmt.format3(x) + ") = " + NumberFmt.format3(result));
        System.out.print("Masukkan nilai x lain (atau '-9999' untuk keluar): ");
      } catch (NumberFormatException e) {
        System.out.println("Input tidak valid. Harap masukkan angka atau '-9999'.");
        System.out.print("Masukkan nilai x: ");
      }
    }
  }

  public static double[][] inputTitikSampel() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Masukkan jumlah titik sampel: ");
    int n = scanner.nextInt();

    if (n <= 0) {
      throw new IllegalArgumentException("Jumlah titik sampel harus lebih dari 0.");
    }

    double[][] data = new double[n][2];

    System.out.println("Masukkan titik sampel dalam format 'x y' (dipisahkan spasi dan tekan ENTER setiap barisnya");
    scanner.nextLine();

    for (int i = 0; i < n; i++) {
      String line = scanner.nextLine();
      String[] tokens = line.trim().split("\\s+");

      if (tokens.length != 2) {
        System.out.println("Format input salah. Harap masukkan dua nilai (x dan y).");
        i--;
        continue;
      }

      try {
        data[i][0] = Double.parseDouble(tokens[0]);
        data[i][1] = Double.parseDouble(tokens[1]);
      } catch (NumberFormatException e) {
        System.out.println("Input tidak valid. Harap masukkan angka.");
        i--;
        continue;
      }
    }

    return data;
  }
}
