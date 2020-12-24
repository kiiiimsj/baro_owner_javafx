package com.baro.JsonParsing;

public class Category {
    public int store_id;
    public String category_name;
    public int category_id;

    public Category(int store_id, String category_name, int category_id) {
        this.store_id = store_id;
        this.category_name = category_name;
        this.category_id = category_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    @Override
    public String toString() {
        return "Category{" +
                "store_id=" + store_id +
                ", category_name='" + category_name + '\'' +
                ", category_id=" + category_id +
                '}';
    }
}
