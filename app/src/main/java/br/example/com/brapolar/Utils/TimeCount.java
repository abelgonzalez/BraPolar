package br.example.com.brapolar.Utils;

public class TimeCount {

    public static String toLongTime(long m) {
        if (m < 60) return m + " Segundos" + (m > 1 ? "s " : " ");
        if (m < 3600) {
            return m / 60 + " Minutos" + (m / 60 > 1 ? "s " : " ") + TimeCount.toLongTime(m % 60);
        }
        if (m < 86400) {
            return m / 3600 + " Horas" + (m / 3600 > 1 ? "s " : " ") + TimeCount.toLongTime(m % 3600);
        }
        return m / 86400 + " Dias" + (m / 86400 > 1 ? "s " : " ") + TimeCount.toLongTime(m % 86400);
    }

    public static String toShortTime(long m) {
        if (m < 60) return m + "s";
        if (m < 3600) {
            return m / 60 + "m" + TimeCount.toShortTime(m % 60);
        }
        if (m < 86400) {
            return m / 3600 + "h" + TimeCount.toShortTime(m % 3600);
        }
        return m / 86400 + "d" + TimeCount.toShortTime(m % 86400);
    }
}
