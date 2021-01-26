package com.baro.utils;

public class GetState {
    public static String getState(String state) {
        String stateStr = "알 수 없음";
        switch (state) {
            case "CANCEL" :
                stateStr = "취소";
                break;
            case "DONE" :
                stateStr = "완료";
                break;
            case "PREPARING" :
                stateStr = "준비중";
                break;
        }
        return stateStr;
    }
}
