package algeo.core;

import java.util.Locale;

public final class NumberFmt {
    private NumberFmt() {}

    /** Format dengan 3 desimal, perbaiki -0.000 → 0.000, gunakan '.' (Locale.US). */
    public static String format3(double x) {
        if (Math.abs(x) < 0.0005) x = 0.0; // hindari -0.000
        return String.format(Locale.US, "%.3f", x);
    }

    /** Format dengan n desimal. */
    public static String formatN(double x, int decimals) {
        if (decimals < 0) throw new IllegalArgumentException("decimals tidak boleh negatif");
        double thr = Math.pow(10, -(decimals + 1));
        if (Math.abs(x) < thr) x = 0.0;
        String fmt = "%." + decimals + "f";
        return String.format(Locale.US, fmt, x);
    }

    /** Parse angka yang mungkin memakai koma atau titik sebagai desimal dan menangani pecahan. */
    public static double parseNumber(String token) {
        if (token == null) throw new IllegalArgumentException("token null");
        String norm = token.trim().replace(',', '.');

        int slash = norm.indexOf('/');
        if (slash >= 0) {
            String num = norm.substring(0, slash).trim();
            String den = norm.substring(slash + 1).trim();
            if (num.isEmpty() || den.isEmpty())
                throw new NumberFormatException("Pecahan tidak valid: " + token);
            double a = Double.parseDouble(num);
            double b = Double.parseDouble(den);
            if (b == 0.0) throw new ArithmeticException("Penyebut nol pada pecahan: " + token);
            return a / b;
        }

        return Double.parseDouble(norm);
    }
}
