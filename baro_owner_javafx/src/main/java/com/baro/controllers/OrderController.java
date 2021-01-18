package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.google.gson.Gson;
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
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class OrderController implements Initializable{
    @FXML
    private Button bottomBtn;
    @FXML
    private Button cancelBtn;
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
    public SimpleBooleanProperty is_Done = new SimpleBooleanProperty();
    public SimpleBooleanProperty is_Cancel = new SimpleBooleanProperty();
    public static Stage DetailsStage;
    public int index;
    Preferences preferences = Preferences.userRoot();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    public void configureUI() {
        order_time.setText(orderData.order_date);
        price.setText(orderData.total_price+" 원 " + index);
        customer.setText(orderData.phone);
        order_count.setText(orderData.order_count+" 개의 주문");
        if (orderData.order_state.equals(Order.ACCEPT)){
            shell.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            showDetailBtn.setVisible(true);
            bottomBtn.setText("완료");
        }else if (orderData.order_state.equals(Order.PREPARING)) {

        }
        System.out.println(orderData.order_state);
    }
    public void setData(Order data,int index) {
        orderData = data;
        this.index = index;
    }
    public void getDetail(){
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
            controller.setData(orderDetailParsing,orderData);
            controller.configureLeftUI();
            controller.makeReceiptPreView();
            controller.getChangeToAccept().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    shell.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    showDetailBtn.setVisible(true);
                    bottomBtn.setText("완료");
                    orderData.order_state = Order.ACCEPT;
                }
            });
            controller.getChangeToCancel().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    is_Cancel.set(true);
                }
            });
            stage.show();
            DetailsStage = stage;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showDetail(ActionEvent event) {
        getDetail();
    }
    public void clickBottomBtn(ActionEvent event) {
        if (orderData.order_state.equals(Order.ACCEPT)){
            setOrderDone();
        }else if (orderData.order_state.equals(Order.PREPARING)){
            getDetail();
        }
    }
    public void setOrderDone(){
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSetOrderStatusComplete.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receipt_id", orderData.receipt_id);
            jsonObject.put("store_id", preferences.get("store_id",null));
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
            boolean result = getRequestSuccess(bf.toString());

            if (result) {
                System.out.println("성공");
                is_Done.set(true);
                sendCustomerMessage();
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
    private void sendCustomerMessage() {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSendMessage.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", orderData.phone);
            jsonObject.put("title", "제조 완료");
            jsonObject.put("content", "고객님의 주문이 완료되었습니다. 수령해가세요!");

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
            boolean result = getRequestSuccess(bf.toString());

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
}
