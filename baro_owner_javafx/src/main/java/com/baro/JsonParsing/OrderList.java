package com.baro.JsonParsing;

import java.util.ArrayList;

public class OrderList {
    public boolean result;
    public ArrayList<Order> orders;
    public String message;
    public int discount_rate;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDiscount_rate() {
        return discount_rate;
    }

    public void setDiscount_rate(int discount_rate) {
        this.discount_rate = discount_rate;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "result=" + result +
                ", orders=" + orders +
                ", message='" + message + '\'' +
                ", discount_rate=" + discount_rate +
                '}';
    }
}
