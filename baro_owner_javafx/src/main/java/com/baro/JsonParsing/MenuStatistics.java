package com.baro.JsonParsing;

public class MenuStatistics {
    public String menu_name;
    public int menu_count;
    public int menu_total_price;

    @Override
    public String toString() {
        return "MenuStatistics{" +
                "menu_name='" + menu_name + '\'' +
                ", menu_count=" + menu_count +
                ", menu_total_price=" + menu_total_price +
                '}';
    }

    public MenuStatistics(String menu_name, int menu_count, int menu_total_price) {
        this.menu_name = menu_name;
        this.menu_count = menu_count;
        this.menu_total_price = menu_total_price;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_count() {
        return menu_count;
    }

    public void setMenu_count(int menu_count) {
        this.menu_count = menu_count;
    }

    public int getMenu_total_price() {
        return menu_total_price;
    }

    public void setMenu_total_price(int menu_total_price) {
        this.menu_total_price = menu_total_price;
    }
}
