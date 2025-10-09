package algeo.io;

import algeo.core.Matrix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public final class ResultSaver {
    private ResultSaver() {}

    public static final String OUT_DIR = "test.java.output"; // tujuan save

    private static Path ensureOutDir() throws IOException {
        Path dir = Paths.get(OUT_DIR);
        if (Files.notExists(dir)) Files.createDirectories(dir);
        return dir;
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private static Path buildPath(String prefix) throws IOException {
        Path dir = ensureOutDir();
        String name = (prefix == null || prefix.isBlank()) ? "result" : prefix;
        String file = name + "-" + timestamp() + ".txt";
        return dir.resolve(file);
    }

    public static Path saveText(String prefix, String title, String body) {
        try {
            Path p = buildPath(prefix);
            String nl = System.lineSeparator();
            String content = (title == null ? "" : title) + nl + (body == null ? "" : body) + nl;
            Files.writeString(p, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            return p;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan hasil: " + e.getMessage(), e);
        }
    }

    public static Path saveMatrix(String prefix, String title, Matrix m) {
        String nl = System.lineSeparator();
        String body = (m == null) ? "(matrix null)" : m.toString();
        return saveText(prefix, title, body + nl);
    }

    public static Path saveLines(String prefix, String title, List<String> lines) {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        if (lines != null) for (String s : lines) sb.append(s).append(nl);
        return saveText(prefix, title, sb.toString());
    }

    /** Tanya user apakah mau menyimpan (y/n). Mengembalikan true jika 'y'. */
    public static boolean askSave(Scanner sc) {
        while (true) {
            System.out.print("Apakah Anda ingin menyimpan hasil ke file .txt? (y/n): ");
            String t = sc.nextLine().trim().toLowerCase();
            if (t.equals("y")) return true;
            if (t.equals("n")) return false;
            System.out.println("Masukkan 'y' atau 'n'.");
        }
    }

    public static void maybeSaveText(Scanner sc, String prefix, String title, String body) {
        if (askSave(sc)) {
            Path out = saveText(prefix, title, body);
            System.out.println("Hasil disimpan ke: " + out.toAbsolutePath());
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }

    public static void maybeSaveMatrix(Scanner sc, String prefix, String title, Matrix m) {
        if (askSave(sc)) {
            Path out = saveMatrix(prefix, title, m);
            System.out.println("Hasil disimpan ke: " + out.toAbsolutePath());
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }

    public static void maybeSaveLines(Scanner sc, String prefix, String title, List<String> lines) {
        if (askSave(sc)) {
            Path out = saveLines(prefix, title, lines);
            System.out.println("Hasil disimpan ke: " + out.toAbsolutePath());
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }
}
