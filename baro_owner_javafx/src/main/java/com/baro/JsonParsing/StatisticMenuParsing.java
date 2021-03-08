package com.baro.JsonParsing;

import java.util.ArrayList;

public class StatisticMenuParsing {
    public boolean result;
    public ArrayList<MenuStatistics> menuStatisticsList;

    public StatisticMenuParsing(boolean result, ArrayList<MenuStatistics> menuStatisticsList) {
        this.result = result;
        this.menuStatisticsList = menuStatisticsList;
    }

    @Override
    public String toString() {
        return "StatisticsMenuParsing{" +
                "result=" + result +
                ", menuStatisticsList=" + menuStatisticsList +
                '}';
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayList<MenuStatistics> getMenuStatisticsList() {
        return menuStatisticsList;
    }

    public void setMenuStatisticsList(ArrayList<MenuStatistics> menuStatisticsList) {
        this.menuStatisticsList = menuStatisticsList;
    }



}
