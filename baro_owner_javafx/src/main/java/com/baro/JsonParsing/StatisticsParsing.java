package com.baro.JsonParsing;

import java.util.ArrayList;

public class StatisticsParsing {
    public boolean result;
    public String message;
    public ArrayList<Statistics> statistics;

    @Override
    public String toString() {
        return "StatisticsParsing{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", statistics=" + statistics +
                '}';
    }

    public StatisticsParsing(boolean result, String message, ArrayList<Statistics> statistics) {
        this.result = result;
        this.message = message;
        this.statistics = statistics;
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

    public ArrayList<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(ArrayList<Statistics> statistics) {
        this.statistics = statistics;
    }
}
