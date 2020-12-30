package com.baro.JsonParsing;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Order {
    public static final String PREPARING ="PREPARING";
    public static final String ACCEPT ="ACCEPT";
    public String order_date;
    public int order_count;
    public int total_price;
    public String phone;
    public int discount_price;
    public String receipt_id;
    public String order_state;

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

}
