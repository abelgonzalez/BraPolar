package br.example.com.brapolar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;
import br.example.com.brapolar.Activities.MedicationActivity;
import br.example.com.brapolar.Entities.EntityMedication;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import android.support.v7.app.NotificationCompat;

public class AlarmReceiverMedication extends BroadcastReceiver {
    String[] AM_PM = {"am", "pm"};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        wl.acquire();

        Calendar x = Calendar.getInstance();
        EntityMedication EM = (EntityMedication) intent.getBundleExtra("DATA").getSerializable("EntityMedication");
        long time = intent.getBundleExtra("DATA").getLong("time");
        x.setTimeInMillis(time);
        String title = context.getResources().getString(R.string.question_medication) + " " + EM.getName();
        String subtitle = context.getResources().getString(R.string.sub_question_Medication) + " de las ";
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
        String dateString = formater.format(time);
        subtitle += dateString;
        Intent intentX = new Intent(context, MedicationActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 1, intentX, 0);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentTitle(title);
        builder.setContentText(subtitle);
        builder.setTicker(subtitle);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_app_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_icon));
        builder.setContentIntent(pi);
        builder.setVibrate(new long[]{Notification.DEFAULT_VIBRATE});
        builder.setPriority(Notification.PRIORITY_MAX);
        NotificationManager NM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
        wl.release();
    }
}
