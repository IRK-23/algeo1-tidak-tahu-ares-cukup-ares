package algeo.io;

import java.util.Scanner;

public final class UiPrompts {
    private UiPrompts() {}

    public enum InputChoice { FILE, MANUAL }

    public static InputChoice askInputChoice(Scanner sc) {
        while (true) {
            System.out.print("Input dari (1) file .txt atau (2) manual? ");
            String t = sc.nextLine().trim();
            if ("1".equals(t) || ".txt".equals(t)) return InputChoice.FILE;
            if ("2".equals(t) || "manual".equals(t)) return InputChoice.MANUAL;
            System.out.println("Jawaban harus 1/2/.txt/manual.");
        }
    }

    public static String askPath(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String path = sc.nextLine().trim();
            if (!path.isEmpty()) return path;
            System.out.println("Path tidak boleh kosong.");
        }
    }

    public static int askInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String t = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(t);
                if (v < min || v > max) {
                    System.out.printf("Nilai harus di rentang [%d..%d].%n", min, max);
                } else return v;
            } catch (NumberFormatException e) {
                System.out.println("Masukkan bilangan bulat yang valid.");
            }
        }
    }

    public static double askDouble(Scanner sc, String prompt, double minIncl, double maxExcl) {
        while (true) {
            System.out.print(prompt);
            String t = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(t);
                if (!(v >= minIncl && v < maxExcl)) {
                    System.out.printf("Nilai harus di rentang [%.3g .. %.3g).%n", minIncl, maxExcl);
                } else return v;
            } catch (NumberFormatException e) {
                System.out.println("Masukkan bilangan desimal valid (contoh: 1e-12).");
            }
        }
    }

    public static boolean askYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String ans = sc.nextLine().trim().toLowerCase();
            if (ans.equals("y")) return true;
            if (ans.equals("n")) return false;
            System.out.println("Input tidak valid. Masukkan 'y' atau 'n'.");
        }
    }
}
