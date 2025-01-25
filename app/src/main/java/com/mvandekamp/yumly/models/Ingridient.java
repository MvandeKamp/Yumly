package com.mvandekamp.yumly.models;

import com.mvandekamp.yumly.utils.MetricCookingUnitConverter;

public class Ingridient {
    public String name;
    public double amount;
    public MetricCookingUnitConverter.MetricUnit unit;
    public String Price;
    public String estimatedExpirationDate;

    public Ingridient(String name, double amount, MetricCookingUnitConverter.MetricUnit unit, String estimatedExpirationDate, String price) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.estimatedExpirationDate = estimatedExpirationDate;
        this.Price = price;
    }

    public Ingridient(String name, double amount, MetricCookingUnitConverter.MetricUnit unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.estimatedExpirationDate = null;
        this.Price = "";
    }


    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public MetricCookingUnitConverter.MetricUnit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s of %s", amount, unit.getUnit(), name);
    }
}