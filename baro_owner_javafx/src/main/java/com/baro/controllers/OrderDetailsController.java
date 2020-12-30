package com.baro.controllers;

import com.baro.JsonParsing.OrderDetail;
import com.baro.JsonParsing.OrderDetailParsing;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private SplitPane splitPane;
    private OrderDetailParsing data;
    private String phone;
    private String date;
    private int pay;
    private int discount;
    private String receipt_id;
    private double pos;
    private final SimpleBooleanProperty changeToAccept = new SimpleBooleanProperty();
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

    public void setData(OrderDetailParsing data,String phone,String date,int pay,int discount,String receipt_id) {
        this.data = data;
        this.phone = phone;
        this.date = date;
        this.pay = pay;
        this.discount = discount;
        this.receipt_id = receipt_id;
    }
    public void configureLeftUI(){
        phoneLabel.setText(phone);
        dateLabel.setText(date);
        requestLabel.setText(data.requests);
        totalPriceLabel.setText(pay+" 원");
        discountPriceLabel.setText(discount + " 원");
        finalPriceLabel.setText((pay - discount) + " 원");
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
            controller.setData(receipt_id);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
