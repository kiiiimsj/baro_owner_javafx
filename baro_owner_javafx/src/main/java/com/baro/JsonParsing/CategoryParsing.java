package com.baro.JsonParsing;

import java.util.ArrayList;

public class CategoryParsing {
    public boolean result;
    public String message;
    public ArrayList<Category> category;

    @Override
    public String toString() {
        return "CategoryParsing{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", category=" + category +
                '}';
    }

    public CategoryParsing(boolean result, String message, ArrayList<Category> category) {
        this.result = result;
        this.message = message;
        this.category = category;
    }

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

    public ArrayList<Category> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<Category> category) {
        this.category = category;
    }
}
