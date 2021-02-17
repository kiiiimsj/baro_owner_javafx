package com.baro.JsonParsing;

import java.util.ArrayList;

public class OrderDetailParsing {
    public boolean result;
    public String message;
    public String requests;
    public ArrayList<OrderDetail> orders;
    public int discount_rate;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public ArrayList<OrderDetail> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderDetail> orders) {
        this.orders = orders;
    }

    public int getDiscount_rate() {
        return discount_rate;
    }

    public void setDiscount_rate(int discount_rate) {
        this.discount_rate = discount_rate;
    }
}
