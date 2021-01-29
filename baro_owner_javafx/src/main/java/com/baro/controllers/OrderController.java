package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetail;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.baro.utils.GetBool;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import sample.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
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
    private Label timeLabel;
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
        customer.setText("고객번호 : "+orderData.phone);
        order_count.setText("메뉴 " + orderData.order_count + "개");
        price.setText(orderData.total_price+"원");
        if (orderData.order_state.equals(Order.ACCEPT)){
            state.setText("제조중");
            state.setStyle("-fx-background-color: rgb(255,111,0); -fx-text-fill: white;-fx-background-radius: 5px;");
        }else if (orderData.order_state.equals(Order.PREPARING)) {
            state.setText("신규");
            state.setStyle("-fx-background-color: rgba(131,50,230,0.75); -fx-text-fill: white;-fx-background-radius: 5px; ");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double max = 10.0;
                    double height = 60.0;
                    for (double i = max;i > 0 ; --i) {
                        final double iterator = i;
                        Thread.sleep(1000);
                        double finalI = i;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timer.setTranslateY(-( ( iterator / max ) * 100.0 ) / 3.3);
                                timer.setHeight(height - ( ( iterator / max ) * 100.0 ) / 3.3);
                                System.out.println(-( ( iterator / max ) * 100.0 ) / 3.3 );
                                timeLabel.setText((int)finalI +"초");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public void changeToAccept(){
        orderData.order_state = Order.ACCEPT;
        state.setText("제조중");
        state.setStyle("-fx-background-color: rgb(255,111,0);-fx-text-fill: white;-fx-background-radius: 5px;");
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
