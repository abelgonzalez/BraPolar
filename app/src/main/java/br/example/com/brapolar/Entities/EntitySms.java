package br.example.com.brapolar.Entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntitySms {
    private int id;
    private String uid, body, address, type, date;
    private String user;

    public EntitySms() {
    }

    public EntitySms(String body, String address, String type) {
        this.body = body;
        this.address = address;
        this.type = type;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public EntitySms(int id, String body, String address, String type, String date) {
        this.id = id;
        this.body = body;
        this.address = address;
        this.type = type;
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        this.date = formater.format(new Date());
    }

    public EntitySms(String date, String uid, String address, int id, String body, String type, String user) {
        this.id = id;
        this.uid = uid;
        this.body = body;
        this.address = address;
        this.type = type;
        this.date = date;
        this.user = user;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
