package br.example.com.brapolar.Entities;

public class EntitySetting {
    private int id;
    private String uid, property;
    private int value;
    private String user;

    public EntitySetting(int id, String property, int value) {
        this.id = id;
        this.property = property;
        this.value = value;
    }

    public EntitySetting(String property, int value) {
        this.property = property;
        this.value = value;
    }

    public boolean isEnable() {
        return value == 1;
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
