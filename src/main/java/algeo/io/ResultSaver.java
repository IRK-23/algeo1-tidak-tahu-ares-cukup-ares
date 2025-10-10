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

    private static Path resolveOutDir() {
        String env = System.getenv("ALGEO_OUT_DIR");
        if (env != null && !env.isBlank()) {
            return Paths.get(env).toAbsolutePath().normalize();
        }
        // Sesuai struktur project: test/output
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        return projectRoot.resolve("src").resolve("test").resolve("java").resolve("output")
                          .toAbsolutePath()
                          .normalize();
    }

    private static Path ensureOutDir() throws IOException {
        Path dir = resolveOutDir();
        if (Files.notExists(dir)) Files.createDirectories(dir);
        return dir;
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private static String safePrefix(String prefix) {
        String p = (prefix == null || prefix.isBlank()) ? "result" : prefix.trim();
        // ganti karakter ilegal dengan underscore
        return p.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
    }

    private static Path buildPath(String prefix) throws IOException {
        Path dir = ensureOutDir();
        String name = safePrefix(prefix) + "_" + timestamp() + ".txt";
        return dir.resolve(name);
    }

    public static Path saveText(String prefix, String title, String body) {
        try {
            Path p = buildPath(prefix);
            String nl = System.lineSeparator();
            StringBuilder content = new StringBuilder();
            
            if (title != null && !title.isBlank()) {
                content.append(title).append(nl);
                content.append("=".repeat(title.length())).append(nl);
                content.append(nl);
            }
            
            if (body != null) {
                content.append(body);
            }
            
            Files.writeString(p, content.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
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
            System.out.print("\nApakah Anda ingin menyimpan hasil ke file .txt? (y/n): ");
            String t = sc.nextLine().trim().toLowerCase();
            if (t.equals("y")) return true;
            if (t.equals("n")) return false;
            System.out.println("Masukkan 'y' atau 'n'.");
        }
    }

    public static void maybeSaveText(Scanner sc, String prefix, String title, String body) {
        if (askSave(sc)) {
            try {
                Path out = saveText(prefix, title, body);
                System.out.println("Hasil berhasil disimpan ke: " + out.toAbsolutePath());
            } catch (RuntimeException e) {
                System.out.println("Gagal menyimpan: " + e.getMessage());
            }
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }

    public static void maybeSaveMatrix(Scanner sc, String prefix, String title, Matrix m) {
        if (askSave(sc)) {
            try {
                Path out = saveMatrix(prefix, title, m);
                System.out.println("Hasil berhasil disimpan ke: " + out.toAbsolutePath());
            } catch (RuntimeException e) {
                System.out.println("Gagal menyimpan: " + e.getMessage());
            }
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }

    public static void maybeSaveLines(Scanner sc, String prefix, String title, List<String> lines) {
        if (askSave(sc)) {
            try {
                Path out = saveLines(prefix, title, lines);
                System.out.println("Hasil berhasil disimpan ke: " + out.toAbsolutePath());
            } catch (RuntimeException e) {
                System.out.println("Gagal menyimpan: " + e.getMessage());
            }
        } else {
            System.out.println("Hasil tidak disimpan.");
        }
    }
}