package br.example.com.brapolar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Utils.UserSingleton;

public class CallLogService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE", "STARTED");
        //-- Here is the filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.setPriority(-1);
        registerReceiver(receiver, filter);
        //-- Inser your Code here ----
        //ScanCallLog(this); //-- For exapmle a function for scaning the log
        getCallDetails(this);
        //
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d("SERVICE", "STOPPED");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                //-- Because you've got to wait before the phone write into the call log
                new CountDownTimer(5000, 1000) {
                    public void onFinish() {
                        getCallDetails(context);
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();

            }
        }
    };

    private void getCallDetails(Context ctx) {
        EntityCall x = new EntityCall();
        //Toast.makeText(ctx, "Cambio", Toast.LENGTH_SHORT).show(); // NO JODAS MAS VIEJO,,, ANDAAA!
        MySqliteHandler mySqliteHandler = new MySqliteHandler(ctx);
        StringBuffer sb = new StringBuffer();
        StringBuffer ed = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        assert managedCursor != null;
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String ID = managedCursor.getString(id);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = ctx.getString(R.string.call_outgoing_sing);
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = ctx.getString(R.string.call_incoming_sing);
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = ctx.getString(R.string.call_missed_sing);
                    break;
            }
            x = new EntityCall(Integer.parseInt(ID), phNumber, dir, callDayTime.toString(), Integer.parseInt(callDuration));
            mySqliteHandler.CheckToAddCall(x);
        }
        managedCursor.close();
    }
}
