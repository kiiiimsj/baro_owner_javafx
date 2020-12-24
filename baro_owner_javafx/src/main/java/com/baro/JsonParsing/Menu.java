package com.baro.JsonParsing;

public class Menu {
    public int store_id;
    public String is_soldout;
    public int category_id;
    public String menu_info;
    public String menu_name;
    public int menu_defaultprice;
    public String menu_image;
    public int menu_id;

    @Override
    public String toString() {
        return "Menu{" +
                "store_id=" + store_id +
                ", is_soldout='" + is_soldout + '\'' +
                ", category_id=" + category_id +
                ", menu_info='" + menu_info + '\'' +
                ", menu_name='" + menu_name + '\'' +
                ", menu_defaultprice=" + menu_defaultprice +
                ", menu_image='" + menu_image + '\'' +
                ", menu_id=" + menu_id +
                '}';
    }

    public Menu(int store_id, String is_soldout, int category_id, String menu_info, String menu_name, int menu_defaultprice, String menu_image, int menu_id) {
        this.store_id = store_id;
        this.is_soldout = is_soldout;
        this.category_id = category_id;
        this.menu_info = menu_info;
        this.menu_name = menu_name;
        this.menu_defaultprice = menu_defaultprice;
        this.menu_image = menu_image;
        this.menu_id = menu_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getIs_soldout() {
        return is_soldout;
    }

    public void setIs_soldout(String is_soldout) {
        this.is_soldout = is_soldout;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getMenu_info() {
        return menu_info;
    }

    public void setMenu_info(String menu_info) {
        this.menu_info = menu_info;
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

    public String getMenu_image() {
        return menu_image;
    }

    public void setMenu_image(String menu_image) {
        this.menu_image = menu_image;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }
}
