package com.baro.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class GetBool {

    public static boolean getBool(String toString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(toString);
            jsonObject.getBoolean("result");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return (jsonObject.getBoolean("result"));
    }
}
