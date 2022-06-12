package br.example.com.brapolar;

public class EntityTower {
    int id;
    String uid, date, user;
    int cid, lac;

    public EntityTower(int id, String date, int cid, int lac) {
        this.id = id;
        this.date = date;
        this.cid = cid;
        this.lac = lac;
    }

    public EntityTower(String date, int cid, int lac) {
        this.date = date;
        this.cid = cid;
        this.lac = lac;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }
}
