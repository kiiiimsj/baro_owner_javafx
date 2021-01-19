package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
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
import javafx.scene.layout.*;
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

public class NewOrderController implements Initializable{
    @FXML
    private Label customer;
    @FXML
    private Label order_count;
    @FXML
    private Label price;
    @FXML
    private StackPane timeTable;
    private Order orderData ;
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
        customer.setText(orderData.phone);
        order_count.setText("메뉴 " + orderData.order_count + "개");
        price.setText(orderData.total_price+"원");
    }
    public void setData(Order data,int index) {
        this.orderData = data;
        this.index = index;
    }

}
