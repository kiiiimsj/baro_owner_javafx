package com.baro.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

public class SettingTimerController implements Initializable {
    @FXML
    private Button button1;
    @FXML
    private Button button3;
    @FXML
    private Button button5;
    @FXML
    private Button button10;
    @FXML
    private Button button30;
    @FXML
    private Button button60;
    @FXML
    private Button reset;
    @FXML
    private Label time;
    public int timeInt = 0;
    private String receipt_id;
    private String store_id;
    private String phone;
    Preferences preferences = Preferences.userRoot();
    private final SimpleBooleanProperty changeToAccept = new SimpleBooleanProperty();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public SimpleBooleanProperty getChangeToAccept(){
        return changeToAccept;
    }


    public void add1(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 1));
    }
    public void add3(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 3));
    }
    public void add5(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 5));
    }
    public void add10(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 10));
    }
    public void add30(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 30));
    }
    public void add60(ActionEvent event) {
        time.setText(String.valueOf(new Integer(time.getText()) + 60));
    }

    public void resetTimer(ActionEvent event) {
        time.setText(String.valueOf(0));
    }
    public void orderAccept(ActionEvent event) {
        timeInt = Integer.parseInt(time.getText());
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSetOrderStatus.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receipt_id", receipt_id);
            jsonObject.put("store_id", store_id);
            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();
            System.out.println("response" + bf.toString());
            boolean result = getBool(bf.toString());

            if (result) {
                changeToAccept.set(true);
                System.out.println("inTimer: " + timeInt);
                sendCustomerMessage(timeInt);
                System.out.println("성공");
            }else{
                System.out.println("실패");
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
    private void sendCustomerMessage(int time) {
        System.out.println("click button1");
//        timeInt = Integer.parseInt(time);
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSendMessage.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", phone);
            jsonObject.put("title", "주문 접수 완료");
            jsonObject.put("content", "고객님의 소중한 주문이 접수되어 " + time+ "분 뒤에 완성될 예정입니다");

            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();
            System.out.println("response" + bf.toString());
            boolean result = getBool(bf.toString());

            if (result) {
                System.out.println("성공");
            }else{
                System.out.println("실패");
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

    private boolean getBool(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
    public void setData(String receipt_id,String phone){
        this.receipt_id = receipt_id;
        this.phone = phone;
        store_id = preferences.get("store_id",null);
    }
}
