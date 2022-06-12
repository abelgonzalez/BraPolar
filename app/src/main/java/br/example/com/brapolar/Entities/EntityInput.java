package br.example.com.brapolar.Entities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;


public class EntityInput {
    private int id;
    private String uid, date, type, app;
    private String user;

    public static List<EntityInputText> Fragmentado(Context ctx) {
        List<EntityInputText> ret = new ArrayList<EntityInputText>();
        MySqliteHandler mySqliteHandler = new MySqliteHandler(ctx);
        DateFormat format = new SimpleDateFormat(ctx.getString(R.string.format));
        List<EntityInput> all = mySqliteHandler.getAllElementsInput();
        EntityInput last = new EntityInput(1, "", "", "");
        int countError = 0;
        List<EntityInput> text = new ArrayList<EntityInput>();
        try {
            for (int i = 0; i < all.size(); i++) {
                if (i == 0) {
                    last = all.get(i);
                    text.add(last);
                    if (last.getType().equals(ctx.getString(R.string.input_erase))) {
                        countError++;
                    }
                } else {
                    Date lx = format.parse(last.getDate());
                    Date fina = format.parse(all.get(i).getDate());
                    long time = (fina.getTime() - lx.getTime());
                    if (time >= Integer.parseInt(ctx.getString(R.string.input_lapse))) {
                        //new
                        ret.add(new EntityInputText(text, countError));

                        //reset
                        text = new ArrayList<>();
                        countError = 0;

                        last = all.get(i);
                        text.add(last);
                        if (last.getType().equals(ctx.getString(R.string.input_erase))) {
                            countError++;
                        }
                    } else {
                        last = all.get(i);
                        text.add(last);
                        if (last.getType().equals(ctx.getString(R.string.input_erase))) {
                            countError++;
                        }
                    }
                }
            }
            if (text.size() > 0) {
                EntityInputText nx = new EntityInputText(text, countError);
                ret.add(new EntityInputText(text, countError));
            }

        } catch (Exception e) {
            Log.i("errror", "error");
            Toast.makeText(ctx, "error", Toast.LENGTH_LONG).show();
        }
        return ret;
    }

    public EntityInput(String date, String type, String app) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
        this.type = type;
        this.app = app;
    }

    public EntityInput(int id, String date, String type, String app) {
        this.id = id;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
        this.type = type;
        this.app = app;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
