package com.extraditables.los.brokin.brokin_old.models;

import java.util.Comparator;

public class StockModel {

    private String symbol;
    private String name;
    private Float value;
    private Float change;
    private Float changePercentage;

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

    public Float getChange() {
        return change;
    }

    public void setChange(Float change) {
        this.change = change;
    }

    public Float getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(Float changePercentage) {
        this.changePercentage = changePercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockModel)) return false;

        StockModel that = (StockModel) o;

        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (change != null ? !change.equals(that.change) : that.change != null) return false;
        return !(changePercentage != null ? !changePercentage.equals(that.changePercentage) : that.changePercentage != null);

    }

    @Override
    public int hashCode() {
        int result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (change != null ? change.hashCode() : 0);
        result = 31 * result + (changePercentage != null ? changePercentage.hashCode() : 0);
        return result;
    }

    public static Comparator<StockModel> alphabeticallyComparator = new Comparator<StockModel>() {
        public int compare(StockModel one, StockModel other) {
            return one.getSymbol().compareTo(other.getSymbol());
        }
    };
}
