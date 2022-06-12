package br.example.com.brapolar;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class UStats {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();

    @SuppressWarnings("ResourceType")
    public static void getStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null) {
                Log.d(TAG, "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
            }
        }
    }

    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }

    public static void printUsageStats(List<UsageStats> usageStatsList) {
        for (UsageStats u : usageStatsList) {
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground());

        }
    }

    public static List<UsageStats> Get1Year(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }

    public static String printYear(Context context) {
        List<UsageStats> data = Get1Year(context);
        String ret = "";
        for (UsageStats u : data) {
            if (u.getPackageName().equals("com.whatsapp")) {
                long tt = u.getTotalTimeInForeground() / 1000;
                long h = tt / 3600;
                tt %= 3600;
                long m = tt / 60;
                tt %= 60;

                ret += "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                        + h + ":" + m + ":" + tt + "\n" /*+ "inicio" + u.getFirstTimeStamp() + " - " +u.getLastTimeUsed() + "\n"*/;
            }
        }
        return ret;
    }

    public static void printCurrentUsageStatus(Context context) {
        printUsageStats(getUsageStatsList(context));
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
}
