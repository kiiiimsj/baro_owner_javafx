package com.baro.controllers.orderDetail;

import com.baro.JsonParsing.Extras;
import com.baro.JsonParsing.OrderDetail;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.utils.LayoutSize;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailMenuController implements Initializable {

    public VBox menu_content;
    public VBox menu_layout;
    private OrderDetailParsing data;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void configureUI(){
        System.out.println("menu size : " + LayoutSize.ORDER_MENUS_WIDTH);
        menu_layout.setMaxWidth(LayoutSize.ORDER_MENUS_WIDTH);
        menu_content.setMaxWidth(LayoutSize.ORDER_MENUS_WIDTH);
        System.out.println("get menu size : " + menu_layout.getPrefWidth());

        for (int i = 0; i < data.orders.size(); i++) {
            OrderDetail orderDetail = data.orders.get(i);
            GridPane menuCell = new GridPane();

            menuCell.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH);
            ColumnConstraints columnConstraints1 = new ColumnConstraints();
            columnConstraints1.setHgrow(Priority.ALWAYS);
            columnConstraints1.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH/3.0);


            ColumnConstraints columnConstraints2 = new ColumnConstraints();
            columnConstraints2.setHgrow(Priority.ALWAYS);
            columnConstraints2.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH/3.0);
            columnConstraints2.setHalignment(HPos.CENTER);

            ColumnConstraints columnConstraints3 = new ColumnConstraints();
            columnConstraints3.setHgrow(Priority.ALWAYS);
            columnConstraints3.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH/3.0);
            columnConstraints3.setHalignment(HPos.RIGHT);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);

            menuCell.getColumnConstraints().add(0, columnConstraints1);
            menuCell.getColumnConstraints().add(1, columnConstraints2);
            menuCell.getColumnConstraints().add(2, columnConstraints3);

            menuCell.getRowConstraints().add(0, rowConstraints);
            menuCell.setStyle("-fx-background-color: white");

            Text menuNameText = new Text(orderDetail.menu_name);
            Text menuCountText = new Text(orderDetail.order_count+"");

            Text menuDefaultPrice;
//            if(data.discount_rate != 0 ) {
//                menuDefaultPrice = new Text( orderDetail.menu_defaultprice - (int)(orderDetail.menu_defaultprice * (data.discount_rate / 100.0))+"원");
//            }else {
//                menuDefaultPrice = new Text( orderDetail.menu_defaultprice+"원");
//            }

            menuDefaultPrice = new Text( orderDetail.menu_defaultprice+"원");

            menuCell.addRow(0, menuNameText, menuCountText, menuDefaultPrice);
            menuNameText.setStyle("-fx-font-size: 15pt");
            menuCountText.setStyle("-fx-font-size: 15pt");
            menuDefaultPrice.setStyle("-fx-font-size: 15pt");
            menu_content.getChildren().add(menuCell);
            AddChilds(orderDetail);
        }
    }

    private void AddChilds(OrderDetail orderDetail) {
        for (int i = 0;i< orderDetail.extras.size();i++){
            Extras extras = orderDetail.extras.get(i);
            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetail.extraLayout.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderDetail_extraLayout.fxml"));
//                loader.setLocation(getClass().com.baro.controllers.orderDetail.OrderDetailExtraController").getResource("/orderDetail_extraLayout.fxml"));
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
    public void setData(OrderDetailParsing data){
        this.data = data;
    }
}
