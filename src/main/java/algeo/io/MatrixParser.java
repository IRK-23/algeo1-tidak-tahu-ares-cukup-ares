package algeo.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import algeo.core.NumberFmt;

public final class MatrixParser {
    private MatrixParser() {}

    /** Minta user memasukkan path file dan baca matriks dari file tersebut. */
    public static double[][] readMatrixFromUser() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Masukkan path file .txt yang ingin dibaca: ");
            String path = sc.nextLine().trim();

            try {
                double[][] m = readMatrixFromFile(path);
                System.out.println("\nMatrix berhasil dibaca dari: " + path);
                return m;
            } catch (Exception e) {
                System.out.println("Gagal membaca file: " + e.getMessage());
                System.out.println("Silakan coba lagi.\n");
            }
        }
    }

    /** Baca matriks dari file .txt. */
    public static double[][] readMatrixFromFile(String path) throws IOException {
        List<double[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\s+");
                double[] row = new double[tokens.length];

                for (int i = 0; i < tokens.length; i++) {
                    row[i] = NumberFmt.parseNumber(tokens[i]);
                }
                rows.add(row);
            }
        }

        if (rows.isEmpty())
            throw new IllegalArgumentException("File kosong: " + path);

        int cols = rows.get(0).length;
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).length != cols) {
                throw new IllegalArgumentException(
                    "Jumlah kolom tidak konsisten pada baris ke-" + (i + 1)
                );
            }
        }

        double[][] matrix = new double[rows.size()][cols];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }

        return matrix;
    }


}
