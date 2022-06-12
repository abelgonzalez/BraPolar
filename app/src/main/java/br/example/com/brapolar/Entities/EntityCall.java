package br.example.com.brapolar.Entities;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.example.com.brapolar.MySqliteHandler;

public class EntityCall {
    private int id;
    private String uid;
    private String number;
    private String type;
    private String date;
    private Integer duration;
    private String user;

    public EntityCall() {
        super();
    }

    public EntityCall(int id, String number, String type, String date, Integer duration) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.duration = duration;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public EntityCall(String number, String type, String date, Integer duration) {
        this.number = number;
        this.type = type;
        this.duration = duration;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

/*
    public static void LogCall(Context ctx) {
        Cursor managedCursor = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        assert managedCursor != null;
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
        managedCursor.moveToLast();
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        Date callDayTime = new Date(Long.valueOf(callDate));
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dateString = formater.format(callDayTime);
        String callDuration = managedCursor.getString(duration);
        String dir = null;
        int idx = Integer.parseInt(managedCursor.getString(id));
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "Entrante";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                dir = "Sainte";
                break;
            case CallLog.Calls.MISSED_TYPE:
                dir = "Perdida";
                break;
        }
        EntityCall xx = new EntityCall(idx, phNumber, dir, dateString, Integer.parseInt(callDuration));
        MySqliteHandler mySqliteHandler = new MySqliteHandler(ctx);
        mySqliteHandler.addCall(xx);
        Log.i("para ", phNumber);

        //FirebaseSave(xx);// salvar en firebase

        managedCursor.close();
        Toast.makeText(ctx, "EntityCall registered successfully", Toast.LENGTH_LONG).show();
    }

*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
