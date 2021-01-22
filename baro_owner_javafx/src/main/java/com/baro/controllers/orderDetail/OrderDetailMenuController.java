package com.baro.controllers.orderDetail;

import com.baro.JsonParsing.Extras;
import com.baro.JsonParsing.OrderDetail;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrderDetailMenuController implements Initializable {

    public VBox menu_content;
    private ArrayList<OrderDetail> data;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void configureUI(){

        for (int i = 0; i < data.size(); i++) {
            OrderDetail orderDetail = data.get(i);
            GridPane menuCell = new GridPane();
            menuCell.setMaxWidth(460);
            menuCell.setMinWidth(460);
            ColumnConstraints columnConstraints1 = new ColumnConstraints();
            columnConstraints1.setHgrow(Priority.ALWAYS);
            columnConstraints1.setMaxWidth(153);


            ColumnConstraints columnConstraints2 = new ColumnConstraints();
            columnConstraints2.setHgrow(Priority.ALWAYS);
            columnConstraints2.setMaxWidth(153);
            columnConstraints2.setHalignment(HPos.CENTER);

            ColumnConstraints columnConstraints3 = new ColumnConstraints();
            columnConstraints3.setHgrow(Priority.ALWAYS);
            columnConstraints3.setMaxWidth(153);
            columnConstraints3.setHalignment(HPos.RIGHT);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);

            menuCell.getColumnConstraints().add(0, columnConstraints1);
            menuCell.getColumnConstraints().add(1, columnConstraints2);
            menuCell.getColumnConstraints().add(2, columnConstraints3);

            menuCell.getRowConstraints().add(0, rowConstraints);
            Text menuNameText = new Text(orderDetail.menu_name);
            Text menuCountText = new Text(orderDetail.order_count+"");
            Text menuDefaultPrice = new Text(orderDetail.menu_defaultprice+"원");
            menuCell.addRow(0, menuNameText, menuCountText, menuDefaultPrice);
            menuNameText.setStyle("-fx-font-size: 20pt");
            menuCountText.setStyle("-fx-font-size: 20pt");
            menuDefaultPrice.setStyle("-fx-font-size: 20pt");
            menu_content.getChildren().add(menuCell);
            AddChilds(orderDetail);
        }
    }

    private void AddChilds(OrderDetail orderDetail) {
        for (int i = 0;i< orderDetail.extras.size();i++){
            Extras extras = orderDetail.extras.get(i);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetail_extraLayout.fxml"));
            try {
                GridPane gridPane = loader.load();
                OrderDetailExtraController controller = loader.<OrderDetailExtraController>getController();
                controller.setData(extras);
                controller.configureUI();
                menu_content.getChildren().add(gridPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void setData(ArrayList<OrderDetail> data){
        this.data = data;
    }
}
