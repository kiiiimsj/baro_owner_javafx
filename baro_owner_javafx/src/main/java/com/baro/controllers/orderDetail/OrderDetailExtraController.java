package com.baro.controllers.orderDetail;

import com.baro.JsonParsing.Extras;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailExtraController implements Initializable {
    public Label extraCountLabel;
    @FXML
    private Label extraNameLabel;
    @FXML
    private Label extraPriceLabel;
    private Extras extras;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
