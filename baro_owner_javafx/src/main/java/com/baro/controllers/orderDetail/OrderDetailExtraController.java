package com.baro.controllers.orderDetail;

import com.baro.JsonParsing.Extras;
import com.baro.utils.LayoutSize;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailExtraController implements Initializable {
    public Label extraCountLabel;
    public GridPane extra_grid_pane;
    @FXML
    private Label extraNameLabel;
    @FXML
    private Label extraPriceLabel;
    private Extras extras;
    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();
    ColumnConstraints col3 = new ColumnConstraints();

    RowConstraints row1 = new RowConstraints();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        col1.setHgrow(Priority.ALWAYS);
        col1.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH / 3.0);

        col2.setHgrow(Priority.ALWAYS);
        col2.setHalignment(HPos.CENTER);
        col2.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH / 3.0);

        col3.setHgrow(Priority.ALWAYS);
        col3.setHalignment(HPos.RIGHT);
        col3.setMinWidth(LayoutSize.ORDER_MENUS_WIDTH / 3.0);

        row1.setVgrow(Priority.ALWAYS);

        extra_grid_pane.getColumnConstraints().addAll(col1, col2, col3);
        extra_grid_pane.getRowConstraints().addAll(row1);
    }
    public void configureUI(){
        extraPriceLabel.setText(extras.extra_count * extras.extra_price+" 원");
        extraCountLabel.setText(extras.extra_count+"");
        extraNameLabel.setText("   -"+extras.extra_name);
    }
    public void setData(Extras extras){
        this.extras = extras;
    }
}
