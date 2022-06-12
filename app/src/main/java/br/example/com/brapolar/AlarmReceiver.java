package br.example.com.brapolar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;
import br.example.com.brapolar.Activities.MoodActivity;
import br.example.com.brapolar.Activities.SleepActivity;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.Entities.EntitySleep;
//import android.support.v7.app.NotificationCompat;
import android.util.Log;
//import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    String[] AM_PM = {"am", "pm"};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        wl.acquire();

        String type = "";
        Log.i("aaaaa", type);
        MySqliteHandler mySqliteHandler = new MySqliteHandler(context);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            type = extras.getString("type");
        }
        if (type != null) {
            Log.i("alarm", "reciver");
            if (type.equals("mood")) {
                Calendar x = Calendar.getInstance();
                EntityMoodState mood = new EntityMoodState(-1, String.valueOf(x.get(Calendar.DAY_OF_MONTH)) + "/" + (x.get(Calendar.MONTH) + 1) + "/" + x.get(Calendar.YEAR));
                int id = mySqliteHandler.ExistMoodState(mood);
                if (id == -1) {
                    Intent intentX = new Intent(context, MoodActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, intentX, 0);
                    Notification.Builder builder = new Notification.Builder(context);
                    String title = context.getResources().getString(R.string.question_mood);
                    String subtitle = context.getResources().getString(R.string.sub_question_mood);
                    String neW = context.getResources().getString(R.string.sub_question_mood);
                    builder.setContentTitle(title);
                    builder.setContentText(subtitle);
                    builder.setTicker(neW);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.ic_app_icon);
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_icon));
                    builder.setContentIntent(pi);
                    builder.setVibrate(new long[]{Notification.DEFAULT_VIBRATE});
                    builder.setPriority(Notification.PRIORITY_MAX);
                    NotificationManager NM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NM.notify(0, builder.build());
                }
            } else if (type.equals("sleep")) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.MILLISECOND, 0);

                EntitySleep a = mySqliteHandler.getSleepByDate(c.getTimeInMillis());
                if (a.getId() != -1) {
                   /* Calendar aw = Calendar.getInstance();
                    Calendar sl = Calendar.getInstance();
                    aw.setTimeInMillis(a.getAwake());
                    sl.setTimeInMillis(a.getSleep());
                    StringBuilder date = new StringBuilder();
                    int ap1 = sl.get(Calendar.AM_PM);
                    int ap2 = aw.get(Calendar.AM_PM);
                    date.append(" " + sl.get(Calendar.HOUR) + " ").append(AM_PM[ap1]).append(" e as ").append(aw.get(Calendar.HOUR)).append(" " + AM_PM[ap2]);

                    String title = context.getResources().getString(R.string.question_sleep) + date;
                    String subtitle = context.getResources().getString(R.string.sub_question_sleep);
                    String neW = context.getResources().getString(R.string.sub_question_sleep);
                    Intent intentX = new Intent(context, SleepActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, intentX, 0);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setContentTitle(title);
                    builder.setContentText(subtitle);
                    builder.setTicker(neW);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                    builder.setContentIntent(pi);
                    builder.setVibrate(new long[]{Notification.DEFAULT_VIBRATE});
                    builder.setPriority(Notification.PRIORITY_MAX);
                    NotificationManager NM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    NM.notify(0, builder.build());*/
                } else {

                    ///find aproximated
                    Calendar c1 = Calendar.getInstance();
                    c1.add(Calendar.DAY_OF_MONTH, -1);
                    c1.set(Calendar.HOUR_OF_DAY, 21);
                    c1.set(Calendar.MINUTE, 0);
                    c1.set(Calendar.SECOND, 0);
                    c1.set(Calendar.MILLISECOND, 0);
                    EntityScreen a1 = mySqliteHandler.getFirstScreenByDate(c1.getTimeInMillis());
                    Calendar sleep, awake;
                    String f1, f2;
                    f1 = f2 = "-";
                    if (a1.getId() != -1) {
                        sleep = Calendar.getInstance();
                        sleep.setTimeInMillis(a1.getDate());

                        String time = String.valueOf(sleep.get(Calendar.HOUR));
                        int t1 = sleep.get(Calendar.AM_PM);
                        f1 = time + " " + AM_PM[t1];
                    }

                    Calendar c2 = Calendar.getInstance();
                    c2.set(Calendar.HOUR_OF_DAY, 9);
                    c2.set(Calendar.MINUTE, 0);
                    c2.set(Calendar.SECOND, 0);
                    c2.set(Calendar.MILLISECOND, 0);

                    EntityScreen a2 = mySqliteHandler.getLastScreenByDate(c2.getTimeInMillis());
                    if (a2.getId() != -1) {
                        awake = Calendar.getInstance();
                        awake.setTimeInMillis(a2.getDate());

                        String time = String.valueOf(awake.get(Calendar.HOUR));
                        int t1 = awake.get(Calendar.AM_PM);
                        f2 = time + " " + AM_PM[t1];
                    }

                    String title = context.getResources().getString(R.string.question_sleep) + " " + f1 + " e as " + f2;
                    String subtitle = context.getResources().getString(R.string.sub_question_sleep);
                    String neW = context.getResources().getString(R.string.sub_question_sleep);
                    Intent intentX = new Intent(context, SleepActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 1, intentX, 0);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setContentTitle(title);
                    builder.setContentText(subtitle);
                    builder.setTicker(neW);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.ic_app_icon);
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_icon));
                    builder.setContentIntent(pi);
                    builder.setVibrate(new long[]{Notification.DEFAULT_VIBRATE});
                    builder.setPriority(Notification.PRIORITY_MAX);
                    NotificationManager NM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NM.notify(0, builder.build());
                }
            }
        }
        wl.release();
    }
}
