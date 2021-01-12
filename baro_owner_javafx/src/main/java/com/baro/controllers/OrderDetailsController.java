package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.Printer.ReceiptPrint;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.json.JSONObject;
import sample.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class OrderDetailsController implements Initializable {
    @FXML
    private Label phoneLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label requestLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label discountPriceLabel;
    @FXML
    private Label finalPriceLabel;
    @FXML
    private VBox orderBox;
    @FXML
    public Button setTime;
    @FXML
    public Button printButton;
    @FXML
    private SplitPane splitPane;
    private OrderDetailParsing data;
    private Order order;
    private double pos;
    private final SimpleBooleanProperty changeToAccept = new SimpleBooleanProperty();
    private final SimpleBooleanProperty changeToCancel = new SimpleBooleanProperty();

    private Preferences preferences = Preferences.userRoot();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pos = splitPane.getDividers().get(0).getPosition();
        splitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                splitPane.getDividers().get(0).setPosition(pos);
            }
        });

    }
    public SimpleBooleanProperty getChangeToAccept(){
        return changeToAccept;
    }
    public SimpleBooleanProperty getChangeToCancel(){
        return changeToCancel;
    }

    public void setData(OrderDetailParsing data,Order order) {
        this.data = data;
        this.order = order;
    }
    public void configureLeftUI(){
        phoneLabel.setText(order.phone);
        dateLabel.setText(order.order_date);
        requestLabel.setText(data.requests);
        totalPriceLabel.setText(order.total_price+" 원");
        discountPriceLabel.setText(order.discount_price + " 원");
        finalPriceLabel.setText((order.total_price - order.discount_price) + " 원");
        if (order.order_state.equals(Order.PREPARING)){
            setTime.setVisible(true);
        }else if (order.order_state.equals(Order.ACCEPT)){
            setTime.setVisible(false);
        }
        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
                    Stage stage = new Stage(StageStyle.UTILITY);
                    stage.initModality(Modality.WINDOW_MODAL);

                    stage.setTitle("프린터 옵션");

                    Parent parent = loader.load();
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                    stage.setResizable(false);

                    ReceiptPrint print = loader.getController();

                    print.order = data;
                    print.orderInfo = order;

                    print.startPrint();
                    if(!preferences.getBoolean("printBefore", false)) {
                        stage.show();
                    }
                    stage.onCloseRequestProperty().set(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            if(event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                                System.out.println("close interface");
                                if(print.serialPort.isOpen()) {
                                    try {
                                        print.printOutput.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    print.serialPort.closePort();
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void configureRightUI(){
        for (int i = 0;i<data.orders.size();i++){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetail_menuLayout.fxml"));
            try {
                VBox vBox = loader.load();
                OrderDetailMenuController controller = loader.<OrderDetailMenuController>getController();
                controller.setData(data.orders.get(i));
                controller.configureUI();
                orderBox.getChildren().add(vBox);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void clickSettingTimes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SettingTimer.fxml"));
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.setTitle("시간 설정");
            Parent parent = loader.load();
            SettingTimerController controller = loader.<SettingTimerController>getController();
            controller.getChangeToAccept().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue == true){
                        changeToAccept.set(newValue);
                        stage.close();
                        OrderController.DetailsStage.close();
                    }
                }
            });
            controller.setData(order.receipt_id,order.phone);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private boolean getRequestSuccess(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
    public void clickCancel(ActionEvent event) {
        try{
            URL url = new URL("http://3.35.180.57:8080/BillingCancel.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nick", "하태영");
            jsonObject.put("cancel_reason", "고객님의 소중한 주문이 가게의 사정으로인해 취소되었습니다");
            jsonObject.put("receipt_id", order.receipt_id);
            jsonObject.put("store_name", Preferences.userRoot().get("store_name",null));
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
                OrderController.DetailsStage.close();
                changeToCancel.set(true);
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
            jsonObject.put("phone",order.phone);
            jsonObject.put("title", "주문 취소");
            jsonObject.put("content", "고객님의 소중한 주문이 가게의 사정으로인해 취소되었습니다");

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
