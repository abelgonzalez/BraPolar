package br.example.com.brapolar.Entities;

import java.text.SimpleDateFormat;

public class EntitySleep {
    private int id;
    private String uid;
    private long date, sleep, awake, value;
    private String user;

    public EntitySleep(int id, long date, long sleep, long awake, long value) {
        this.id = id;
        this.date = date;
        this.sleep = sleep;
        this.awake = awake;
        this.value = value;
    }

    public EntitySleep(long date, long sleep, long awake, long value) {
        this.date = date;
        this.sleep = sleep;
        this.awake = awake;
        this.value = value;
    }

    public String Date() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        return format.format(date);
    }

    public long TimeSleep() {
        return Math.abs(awake - sleep);
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getSleep() {
        return sleep;
    }

    public void setSleep(long sleep) {
        this.sleep = sleep;
    }

    public long getAwake() {
        return awake;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setAwake(long awake) {
        this.awake = awake;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
