package br.example.com.brapolar.Entities;

import java.io.Serializable;

public class EntityMedication implements Serializable {
    private int id;
    private String uid, name, unit, dose;
    private int quantity, frequency, enable;
    private long date;
    private String user;

    public EntityMedication(int id, String name, String unit, String dose, int quantity, int frequency, int enable, long date) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.dose = dose;
        this.quantity = quantity;
        this.frequency = frequency;
        this.enable = enable;
        this.date = date;
    }

    public EntityMedication(String name, String unit, String dose, int quantity, int frequency, int enable, long date) {
        this.name = name;
        this.unit = unit;
        this.dose = dose;
        this.quantity = quantity;
        this.frequency = frequency;
        this.enable = enable;
        this.date = date;
    }

    public EntityMedication() {
    }

    public void TakePild() {
        quantity--;
    }

    @Override
    public String toString() {
        return getDose() + " " + getUnit();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
