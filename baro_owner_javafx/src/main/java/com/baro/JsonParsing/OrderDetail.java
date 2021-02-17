package com.baro.JsonParsing;

import java.util.ArrayList;

public class OrderDetail {
    public static final String PREPARING ="PREPARING";
    public static final String ACCEPT ="ACCEPT";
    public int order_count;
    public String menu_name;
    public int menu_defaultprice;

    public int order_id;
    public String order_state;
    public ArrayList<Extras> extras;

    public static String getPREPARING() {
        return PREPARING;
    }

    public static String getACCEPT() {
        return ACCEPT;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_defaultprice() {
        return menu_defaultprice;
    }

    public void setMenu_defaultprice(int menu_defaultprice) {
        this.menu_defaultprice = menu_defaultprice;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_state() {
        return order_state;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public ArrayList<Extras> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<Extras> extras) {
        this.extras = extras;
    }
}
