package algeo.io;

import algeo.spl.*;
import java.util.Scanner;

public class Menu {
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
    System.out.println("5.  Regresi Polinomial");
    System.out.println("6.  Operasi Standar Matrix");
    System.out.println("7.  Keluar");
    System.out.println("---------------------------------------------");
  }

  public static void choice() {
    Scanner cMenu = new Scanner(System.in);
    System.out.println("\nSilakan pilih operasi yang ingin Anda lakukan:");

    int menu = cMenu.nextInt();

    do {
      if (menu == 1) {
        splIO();
        menu = -1;
      } else if (menu == 2) {
        determinanIO();
        menu = -1;
      } else if (menu == 3) {
        inverseIO();
        menu = -1;
      } else if (menu == 4) {
        interpolasiIO();
        menu = -1;
      } else if (menu == 5) {
        regresiIO();
        menu = -1;
      } else if (menu == 7) {
        System.out.print("\nTekan ENTER untuk kembali ke menu...");
        cMenu.nextLine();
        cMenu.nextLine();
        menu = -1;
      }

    } while (menu != 7);
    cMenu.close();
  }

  public static void splIO() {
    System.out.println("Metode SPL yang dapat dipilih");
    System.out.println("---------------------------------------------");
    System.out.println("1.  Eliminasi Gauss");
    System.out.println("2.  Eliminasi Gauss-Jordan");
    System.out.println("3.  Kaidah Cramer");
    System.out.println("4.  Metode Matriks Balikan");
    System.out.println("5.  Keluar");
    System.out.println("---------------------------------------------");

    Scanner cSPL = new Scanner(System.in);
    System.out.println("\nSilakan pilih metode operasi yang ingin Anda lakukan:");

    int menu = cSPL.nextInt();

    do {
      if (menu == 1) {
        Gauss.gauss();
        menu = -1;
      }
      // else if (menu == 2) spl.gaussjordan();
      // else if (menu == 3) inverseIO();
      // else if (menu == 4) interpolasiIO();
      // else if (menu == 5) regresiIO();
      // else if (menu == 7) {
      //   System.out.print("\nTekan ENTER untuk kembali ke menu...");
      //   cSPL.nextLine();
      //   cSPL.nextLine();
      // }

    } while (menu != 7);
    cSPL.close();
  }

  public static void determinanIO() {}

  public static void inverseIO() {}

  public static void interpolasiIO() {}

  public static void regresiIO() {}
}
