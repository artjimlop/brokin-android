package com.extraditables.los.brokin.models;

import com.j256.ormlite.field.DatabaseField;

public class UserModel {

    public static final String ID = "_id";
    public static final String NAME = "userName";
    public static final String FLUSH = "userCash";

    @DatabaseField(generatedId = true, columnName = ID)
    int userId;
    @DatabaseField(columnName = NAME)
    private String userName;
    @DatabaseField(columnName = FLUSH)
    private Float cash;

    public UserModel() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Float getCash() {
        return cash;
    }

    public void setCash(Float cash) {
        this.cash = cash;
    }
}
