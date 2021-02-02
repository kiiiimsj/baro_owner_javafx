package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.OrderListController;
import com.baro.utils.DateConverter;
import com.baro.utils.GetBool;
import com.google.gson.Gson;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class OrderController implements Initializable{

    public Label state;
    public Rectangle timer;
    @FXML
    private Label customer;
    @FXML
    private Label order_count;
    @FXML
    private Label price;
    @FXML
    private StackPane timeTable;
    @FXML
    private HBox shell;
    @FXML
    public Label timeLabel;

    public Order orderData ;
    private OrderDetailParsing orderDetailParsing;
    public SimpleBooleanProperty is_Done = new SimpleBooleanProperty();
    public SimpleBooleanProperty is_Cancel = new SimpleBooleanProperty();
    private int index;
    public static Stage DetailsStage;
    Preferences preferences = Preferences.userRoot();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void configureUI() {
        customer.setText("고객번호 "+orderData.phone);
        order_count.setText("메뉴 " + orderData.order_count + "개 |");
        price.setText(orderData.total_price+"원");
        if (orderData.order_state.equals(Order.ACCEPT)){
            state.setText("제조중");
            state.setStyle("-fx-background-color: rgb(255,111,0); -fx-text-fill: white;-fx-background-radius: 5px;");
        }else if (orderData.order_state.equals(Order.PREPARING)) {
            state.setText("신규");
            state.setStyle("-fx-background-color: rgba(131,50,230,0.75); -fx-text-fill: white;-fx-background-radius: 5px; ");
        }
        System.out.println("intheorder : "+orderData.getCompleteTime());
        timeLabel.setText(orderData.getCompleteTime());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    double max = 10.0;
//                    double height = 60.0;
//                    for (double i = max;i > 0 ; --i) {
//                        final double iterator = i;
//                        Thread.sleep(1000);
//                        double finalI = i;
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                // (일부시간  / 전체시간) * 100 = 일부시간 백분율
//                                // timer는 가로 50, 세로 30의 사각형
//                                // 시간이 늘어 날수록 세로가 길어져 뒤에 원을 가리는 방식
//                                // timer의 처음시작 시 적정 크기는 30 (더 커지면 order h_box를 벗어남)
//                                // 일부시간의 백분율에 3.3 (최대한 근사치가 나오게 하기위해 0.3까지 씀) 나누면
//                                // 100 -> 0 으로 흘르는 백분율이 30 -> 0으로 흐르게 됨.
//                                // timer의 기존 크기는 60으로 두고 백분율의 시작은 30이므로
//                                // timer의 크기 연산은 60 - 30으로 시작해서 60 - 0 까지 흘러가게된다.
//                                timer.setHeight(height - ( ( iterator / max ) * 100.0 ) / 3.3);
//
//                                // (일부시간  / 전체시간) * 100 = 일부시간 백분율
//                                // timer가 원을 가리지 않는 적정 y 위치는 -30
//                                // 완전히 가리게 될때 y 위치는 0
//                                // 크기와 동일하게 -30 - > 0 으로 흘러가게 설정
//                                timer.setTranslateY(-( ( iterator / max ) * 100.0 ) / 3.3);
//
//                                // 소수점을 없애기 위한 캐스팅
//                                timeLabel.setText((int)finalI +"초");
//                            }
//                        });
//                    }
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            timeLabel.setText("완료");
//                        }
//                    });
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
    public void changeToAccept(){
        orderData.order_state = Order.ACCEPT;
        state.setText("제조중");
        state.setStyle("-fx-background-color: rgb(255,111,0);-fx-text-fill: white;-fx-background-radius: 5px;");
        timeLabel.setText(orderData.getCompleteTime());
    }
    public void setData(Order data,int index) {
        this.orderData = data;
        this.index = index;
    }
    private void parsingOrders(String toString) {
        orderDetailParsing = new Gson().fromJson(toString, OrderDetailParsing.class);
    }
    public OrderDetailParsing getDetail(){
        try{
            URL url = new URL("http://3.35.180.57:8080/OrderListDoneOrCancelForOwner.do?receipt_id="+orderData.receipt_id);
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
            boolean result = GetBool.getBool(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){
//                parsingOrders(bf.toString());
                orderDetailParsing = new Gson().fromJson(bf.toString(), OrderDetailParsing.class);
                return orderDetailParsing;
            }else{
                return null;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
