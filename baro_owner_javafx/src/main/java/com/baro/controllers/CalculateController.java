package com.baro.controllers;

import com.baro.utils.DateConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class CalculateController implements Initializable {
    public Label week_sum_money_label;
    Preferences preferences = Preferences.userRoot();
    private String owner_store_id;
    private int couponPrice;
    private int menuTotalPrice;
    private int thisWeekTotalPrice;

    @FXML public Label this_week_total_price;
    @FXML public Label menu_total_price;
    @FXML public Label coupon_price;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        owner_store_id = preferences.get("store_id", "");
        getStatisticsSalesValue();
    }
    private void getStatisticsSalesValue() {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerCalculate.do?store_id="+owner_store_id);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(new JSONObject(bf.toString()).getBoolean("result")){

                setCalCulateText(bf.toString());
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCalCulateText(String jsonString) {
        //LocalDate.now().minus().DateTimeFormatter.ofPattern("MM/dd"))
        week_sum_money_label.setText("일주일 ("+DateConverter.getFirstDayOfWeek().format(DateTimeFormatter.ofPattern("MM/dd"))+"(월)"+
                " ~ "+ LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd")) + "(" + DateConverter.nameOfDay().charAt(0) + ") )" + " 정산금액");

        couponPrice = new JSONObject(jsonString.toString()).getInt("coupon_price");
        menuTotalPrice = new JSONObject(jsonString.toString()).getInt("menu_total_price");
        thisWeekTotalPrice = menuTotalPrice - couponPrice;

        this_week_total_price.setText(thisWeekTotalPrice + " 원");
        coupon_price.setText(couponPrice + " 원");
        menu_total_price.setText("= " +menuTotalPrice + " 원");

        this_week_total_price.setAlignment(Pos.BASELINE_RIGHT);
        coupon_price.setAlignment(Pos.BASELINE_RIGHT);
        menu_total_price.setAlignment(Pos.BASELINE_RIGHT);

        this_week_total_price.setStyle("-fx-font-size: 50px; -fx-font-family: 'NotoSansRegular';" +
                " -fx-text-fill: black;  -fx-border-radius: 0px 0px 10px 10px; -fx-border-color: #8333e6");
        coupon_price.setStyle("-fx-font-size: 50px; -fx-font-family: 'NotoSansRegular';" +
                " -fx-text-fill: black;  -fx-border-radius: 0px 0px 10px 10px; -fx-border-color: #8333e6");
        menu_total_price.setStyle("-fx-font-size: 50px; -fx-font-family: 'NotoSansRegular';" +
                " -fx-text-fill: black; -fx-border-radius: 0px 0px 10px 10px; -fx-border-color: #8333e6");
    }
}
