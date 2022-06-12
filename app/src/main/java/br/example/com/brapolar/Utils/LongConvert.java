package br.example.com.brapolar.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LongConvert {

    public static String FromLongToDateFormatString(long longValue) {
        Date date = new Date(longValue);
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        return formater.format(date);
    }
}
