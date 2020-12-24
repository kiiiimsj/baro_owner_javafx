package com.baro.JsonParsing;

public class Statistics {
    public String date;
    public int price;

    @Override
    public String toString() {
        return "Statistics{" +
                "date='" + date + '\'' +
                ", price=" + price +
                '}';
    }

    public Statistics(String date, int price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
