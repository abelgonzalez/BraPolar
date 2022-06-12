package br.example.com.brapolar.Entities;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntityMoodState {
    private int id, value;
    private String uid, date;
    private String user;

    public EntityMoodState() {
    }

    public EntityMoodState(int id, int value, String date) {
        this.id = id;
        this.value = value;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public EntityMoodState(int id, int value, String date, String user) {
        this.id = id;
        this.value = value;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
        this.user = user;
    }

    public EntityMoodState(int value, String date) {
        this.value = value;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
