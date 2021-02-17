package com.baro.JsonParsing;

import java.util.ArrayList;

public class OrderHistoryList {
    public boolean result;
    public ArrayList<Order> orders;
    public String message;

    @Override
    public String toString() {
        return "OrderHistoryList{" +
                "result=" + result +
                ", orders=" + orders +
                ", message='" + message + '\'' +
                '}';
    }

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
}
