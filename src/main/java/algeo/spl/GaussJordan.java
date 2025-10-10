package algeo.spl;

import algeo.core.*;
import algeo.io.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class GaussJordan {
  public static void gaussjordan() {
    Matrix M = MatrixIO.inputAugmentedMatrix();

    PrintStream originalOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    System.setOut(new PrintStream(bos));

    int solutionType = JumlahSolusi.cekJumlahSolusiM(M);

    System.setOut(originalOut);

    if (solutionType == 0) {
      System.out.println("Determinan = 0, Tidak ada solusi.\n");

    } else if (solutionType == 1) {
      System.out.println("Solusi tunggal:");
      Matrix mRref = MatrixOps.rref(M);
      Gauss.finishSPL(mRref);

    } else { // solutionType == 2
      System.out.println("Solusi banyak:");
      Matrix mRref = MatrixOps.rref(M);
      Gauss.finishParametricSPL(mRref);
    }
  }
}
