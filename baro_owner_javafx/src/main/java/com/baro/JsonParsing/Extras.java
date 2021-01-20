package com.baro.JsonParsing;

public class Extras {
    public int extra_price;
    public String extra_name;
    public int extra_count;

    @Override
    public String toString() {
        return "Extras{" +
                "extra_price=" + extra_price +
                ", extra_name='" + extra_name + '\'' +
                ", extra_count=" + extra_count +
                '}';
    }

    public int getExtra_price() {
        return extra_price;
    }

    public void setExtra_price(int extra_price) {
        this.extra_price = extra_price;
    }

    public String getExtra_name() {
        return extra_name;
    }

    public void setExtra_name(String extra_name) {
        this.extra_name = extra_name;
    }

    public int getExtra_count() {
        return extra_count;
    }

    public void setExtra_count(int extra_count) {
        this.extra_count = extra_count;
    }
}
