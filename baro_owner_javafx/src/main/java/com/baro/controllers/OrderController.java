package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.OrderListController;
import com.google.gson.Gson;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import sample.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ResourceBundle;

public class OrderController implements Initializable{
    @FXML
    private Button acceptBtn;
    @FXML
    private Button showDetailBtn;
    @FXML
    private Label customer;
    @FXML
    private Label price;
    @FXML
    private Label order_time;
    @FXML
    private Label order_count;
    @FXML
    public  VBox shell;
    private Order orderData ;
    private OrderDetailParsing orderDetailParsing;
    public static Stage DetailsStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    public static void changeBackGround(){

    }
    public void configureUI() {
        order_time.setText(orderData.order_date);
        price.setText(orderData.total_price+" 원");
        customer.setText(orderData.phone);
        order_count.setText(orderData.order_count+" 개의 주문");
        if (orderData.order_state.equals(Order.ACCEPT)){
            shell.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            showDetailBtn.setVisible(true);
        }else if (orderData.order_state == Order.PREPARING) {

        }
        System.out.println(orderData.order_state);
    }

    public void setData(Order data) {
        orderData = data;
    }

    public void clickAccept(ActionEvent event){
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
            boolean result = getRequestSuccess(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){
                parsingOrders(bf.toString());
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
    private boolean getRequestSuccess(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
    private void parsingOrders(String toString) {
        orderDetailParsing = new Gson().fromJson(toString, OrderDetailParsing.class);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetails.fxml"));
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.setTitle("주문상세");
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            OrderDetailsController controller = loader.<OrderDetailsController>getController();
            controller.setData(orderDetailParsing,orderData.phone,orderData.order_date,orderData.total_price,orderData.discount_price,orderData.receipt_id);
            controller.configureLeftUI();
            controller.configureRightUI();
            controller.getChangeToAccept().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    shell.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    showDetailBtn.setVisible(true);
                    acceptBtn.setText("완료");
                }
            });
            stage.show();
            DetailsStage = stage;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
