package br.example.com.brapolar.Entities;

public class EntityScreen {
    private int id;
    private String uid, type;
    private int islock;
    private long date;
    private String user;

    public EntityScreen() {
    }

    public EntityScreen(int id, long date, String type, int islock) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.islock = islock;
    }

    public EntityScreen(long date, String type, int islock) {
        this.date = date;
        this.type = type;
        this.islock = islock;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIslock() {
        return islock;
    }

    public void setIslock(int islock) {
        this.islock = islock;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
