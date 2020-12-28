package com.baro.controllers;

import com.baro.JsonParsing.OrderDetail;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailMenuController implements Initializable {
    @FXML
    private Label menuNameLabel;
    @FXML
    private Label defaultPriceLabel;
    @FXML
    private Label eachPriceLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private VBox extraBox;

    private OrderDetail data;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void configureUI(){
        menuNameLabel.setText(data.menu_name);
        defaultPriceLabel.setText(data.menu_defaultprice+"원");
        countLabel.setText(" X " + data.order_count);

        AddChilds();
    }

    private void AddChilds() {
        int extrasPrice = 0;
        for (int i = 0;i<data.extras.size();i++){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetail_extraLayout.fxml"));
            try {
                HBox hBox = loader.load();
                OrderDetailExtraController controller = loader.<OrderDetailExtraController>getController();
                controller.setData(data.extras.get(i));
                controller.configureUI();
                extrasPrice += data.extras.get(i).extra_count * data.extras.get(i).extra_price ;
                extraBox.getChildren().add(hBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int each = data.menu_defaultprice + extrasPrice;
        eachPriceLabel.setText(each+"");
        totalPriceLabel.setText(each * data.order_count + " 원");
    }

    public void setData(OrderDetail data){
        this.data = data;
    }
}
