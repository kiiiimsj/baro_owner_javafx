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
}
