package br.example.com.brapolar.Entities;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.example.com.brapolar.R;


public class EntityInputText { // TODO Evaluate if this goes here

    private String uid;
    private List<EntityInput> keys;
    private EntityInput start, end;
    private int error = 0;
    private String user;

    public EntityInputText(List<EntityInput> keys, int error) {
        this.keys = keys;
        start = keys.get(0);
        end = keys.get(keys.size() - 1);
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public double getSpeed(Context ctx) {
        double speedWord = 0;
        int cant = keys.size();
        long cantWord = cant / 5 + (cant % 5 == 0 ? 0 : 1);
        return EntityInputText.UtilToWPM(getTime(ctx), cantWord);
    }

    public double getTime(Context ctx) {
        try {
            DateFormat format = new SimpleDateFormat(ctx.getString(R.string.format));
            Date lx = format.parse(start.getDate());
            Date fina = format.parse(end.getDate());
            return (double) (fina.getTime() - lx.getTime());
        } catch (Exception e) {
            Log.i("error", "error parse date");
            return 0;
        }
    }

    public String getApp() {
        return keys.get(0).getApp();
    }

    public static double UtilToWPM(double time_miliseconds, long count_word) {
        return count_word * 60000 / time_miliseconds;
    }

    public long getCount() {
        return keys.size();
    }

    public List<EntityInput> getKeys() {
        return keys;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
