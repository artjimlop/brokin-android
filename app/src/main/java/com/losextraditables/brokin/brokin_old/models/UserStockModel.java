package com.losextraditables.brokin.brokin_old.models;

import com.j256.ormlite.field.DatabaseField;

public class UserStockModel {

    public static final String ID = "_id";
    public static final String USER_ID = "_userId";
    public static final String SYMBOL = "symbol";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String CURRENT_VALUE = "currentValue";
    public static final String NUMBER_OF_STOCKS = "numberOfStocks";

    @DatabaseField(generatedId = true, columnName = ID)
    int userStockId;
    @DatabaseField(columnName = USER_ID)
    private int userId;
    @DatabaseField(columnName = SYMBOL)
    private String symbol;
    @DatabaseField(columnName = NAME)
    private String name;
    @DatabaseField(columnName = VALUE)
    private Float value;
    @DatabaseField(columnName = CURRENT_VALUE)
    private Float currentValue;
    @DatabaseField(columnName = NUMBER_OF_STOCKS)
    private Long numberOfStocks;

    public UserStockModel() {
    }

    public Long getNumberOfStocks() {
        return numberOfStocks;
    }

    public void setNumberOfStocks(Long numberOfStocks) {
        this.numberOfStocks = numberOfStocks;
    }

    public int getUserStockId() {
        return userStockId;
    }

    public void setUserStockId(int userStockId) {
        this.userStockId = userStockId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Float currentValue) {
        this.currentValue = currentValue;
    }
}
