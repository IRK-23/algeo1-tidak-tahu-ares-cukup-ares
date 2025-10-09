package algeo.io;

import algeo.determinant.*;
import algeo.interpolasi.*;
import algeo.inverse.*;
import algeo.spl.*;
import java.util.Scanner;

public class Menu {
  private static final Scanner globalScanner = new Scanner(System.in);

  public static void menu() {
    System.out.println("██╗  ██╗  █████╗  ██╗     ██╗ ███╗   ██╗");
    System.out.println("██║ ██╔╝ ██╔══██╗ ██║     ██║ ████╗  ██║");
    System.out.println("█████╔╝  ███████║ ██║     ██║ ██╔██╗ ██║");
    System.out.println("██╔═██╗  ██╔══██║ ██║     ██║ ██║╚██╗██║");
    System.out.println("██║  ██╗ ██║  ██║ ███████╗██║ ██║ ╚████║");
    System.out.println("╚═╝  ╚═╝ ╚═╝  ╚═╝ ╚══════ ╚═╝ ╚═╝  ╚═══╝");
    System.out.println("----------------------------------------");
    System.out.println("Kalkulator Matrix Aljabar Linear AZZEEEK");

    System.out.println("Modul Kalkulator yang dapat digunakan!");
    System.out.println("---------------------------------------------");
    System.out.println("1.  Sistem Persamaan Linear");
    System.out.println("2.  Determinan");
    System.out.println("3.  Matriks Balikan");
    System.out.println("4.  Interpolasi");
    System.out.println("5.  Regresi Polinomial Berganda");
    System.out.println("6.  Operasi Standar Matrix");
    System.out.println("7.  Keluar");
    System.out.println("---------------------------------------------");
  }

  public static void choice() {
    int menu = -9999;

    do {
      menu();
      System.out.print("\nSilakan pilih operasi yang ingin Anda lakukan: ");
      try {
        menu = globalScanner.nextInt();
      } catch (Exception e) {
        System.out.println("Input tidak valid. Silakan masukkan angka.");
        globalScanner.nextLine();
        continue;
      }

      switch (menu) {
        case 1 -> {
          splIO();
          menu = -1;
          waitForEnter();
        }
        case 2 -> {
          determinanIO();
          menu = -1;
          waitForEnter();
        }
        case 3 -> {
          inverseIO();
          menu = -1;
          waitForEnter();
        }
        case 4 -> {
          interpolasiIO();
          menu = -1;
          waitForEnter();
        }
        // case 5 -> {
        //   regresiPolinomialBergandaIO();
        //   menu = -1;
        // }
        // case 6 -> {
        //   MatrixOperationIO.run();
        case 7 -> {
          System.out.print("\nTekan ENTER untuk kembali ke menu...");
          globalScanner.nextLine();
          globalScanner.nextLine();
        }
        default -> System.out.println("Pilihan tidak valid. Silakan pilih 1-7.");
      }

    } while (menu != 7);
  }

  public static void splIO() {

    int menu = -9999;

    do {
      System.out.println("Metode SPL yang dapat dipilih");
      System.out.println("---------------------------------------------");
      System.out.println("1.  Eliminasi Gauss");
      System.out.println("2.  Eliminasi Gauss-Jordan");
      System.out.println("3.  Kaidah Cramer");
      System.out.println("4.  Metode Matriks Balikan");
      System.out.println("5.  Keluar");
      System.out.println("---------------------------------------------");
      System.out.print("\nSilakan pilih metode operasi yang ingin Anda lakukan: ");

      try {
        menu = globalScanner.nextInt();
      } catch (Exception e) {
        System.out.println("Input tidak valid. Silakan masukkan angka.");
        globalScanner.nextLine();
        continue;
      }

      switch (menu) {
        case 1 -> {
          Gauss.gauss();
          menu = -1;
          waitForEnter();
        }
        case 2 -> {
          GaussJordan.gaussjordan();
          menu = -1;
          waitForEnter();
        }
        case 3 -> {
          Cramer.cramer();
          menu = -1;
          waitForEnter();
        }
        case 4 -> InverseMethod.inverseMethod();
        case 5 -> System.out.println("Kembali ke menu utama...");
        default -> System.out.println("Pilihan tidak valid. Silakan pilih 1-5.");
      }

    } while (menu != 5 && menu != -1);
  }

  public static void determinanIO() {
    int menu = -9999;
    do {
      System.out.println("Metode Determinan yang dapat dipilih");
      System.out.println("---------------------------------------------");
      System.out.println("1.  Matriks Kofaktor ");
      System.out.println("2.  OBE");
      System.out.println("3.  Keluar");
      System.out.println("---------------------------------------------");
      System.out.print("\nSilakan pilih metode operasi yang ingin Anda lakukan: ");

      try {
        menu = globalScanner.nextInt();
      } catch (Exception e) {
        System.out.println("Input tidak valid. Silakan masukkan angka.");
        globalScanner.nextLine();
        continue;
      }

      switch (menu) {
        case 1 -> {
          CofactorDeterminant.run();
          menu = -1;
          waitForEnter();
        }
        case 2 -> {
          RowReductionDeterminant.run();
          menu = -1;
          waitForEnter();
        }
        case 3 -> System.out.println("Kembali ke menu utama...");
        default -> System.out.println("Pilihan tidak valid. Silakan pilih 1-3.");
      }

    } while (menu != 3 && menu != -1);
  }

  public static void inverseIO() {
    int menu = -9999;
    do {
      System.out.println("Metode Matriks Balikan yang dapat dipilih");
      System.out.println("---------------------------------------------");
      System.out.println("1.  Augment + RREF");
      System.out.println("2.  Adjoint / Adjoin");
      System.out.println("3.  Keluar");
      System.out.println("---------------------------------------------");
      System.out.print("\nSilakan pilih metode operasi yang ingin Anda lakukan: ");

      try {
        menu = globalScanner.nextInt();
      } catch (Exception e) {
        System.out.println("Input tidak valid. Silakan masukkan angka.");
        globalScanner.nextLine();
        continue;
      }

      switch (menu) {
        case 1 -> {
          AugmentInverse.run();
          menu = -1;
          waitForEnter();
        }
        case 2 -> {
          AdjointInverse.run();
          menu = -1;
          waitForEnter();
        }
        case 3 -> System.out.println("Kembali ke menu utama...");
        default -> System.out.println("Pilihan tidak valid. Silakan pilih 1-3.");
      }
    } while (menu != 3 && menu != -1);
  }

  public static void interpolasiIO() {
    int menu = -9999;
    do {
      System.out.println("Metode Interpolasi yang dapat dipilih");
      System.out.println("---------------------------------------------");
      System.out.println("1.  Interpolasi Polinomial");
      System.out.println("2.  Interpolasi splina Bezier kubik");
      System.out.println("3.  Keluar");
      System.out.println("---------------------------------------------");
      System.out.print("\nSilakan pilih metode operasi yang ingin Anda lakukan: ");

      try {
        menu = globalScanner.nextInt();
      } catch (Exception e) {
        System.out.println("Input tidak valid. Silakan masukkan angka.");
        globalScanner.nextLine();
        continue;
      }
      switch (menu) {
        case 1 -> {
          Polinomial.polinomial();
          menu = -1;
          waitForEnter();
        }
        case 3 -> {
          System.out.println("Kembali ke menu utama...");
          waitForEnter();
          break;
        }
        default -> {
          System.out.println("Pilihan tidak valid. Silakan pilih 1-3.");
          waitForEnter();
        }
      }
    } while (menu != 3 && menu != -1);
  }

  public static void regresiPolinomialBergandaIO() {
    // int menu = -9999;
    // do {
    // //isi langsung dengan metode IO regresi polinomial berganda
    // }
  }

  public static void waitForEnter() {
    System.out.println("Tekan ENTER untuk melanjutkan program");
    globalScanner.nextLine();
  }

  public static void cleanup() {
    if (globalScanner != null) {
      globalScanner.close();
    }
  }
}
