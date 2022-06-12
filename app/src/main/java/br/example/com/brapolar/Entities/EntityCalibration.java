package br.example.com.brapolar.Entities;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntityCalibration {
    private int id, time;
    private String uid, date;
    private String user;

    public EntityCalibration(int id, int time) {
        this.id = id;
        this.time = time;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public EntityCalibration(int time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
