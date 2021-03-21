package com.baro.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import com.baro.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class PopUpController implements Initializable {
    @FXML
    private Label count;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void toTopPostion(ActionEvent event) {
        Popup popup = (Popup) count.getScene().getWindow();
        popup.hide();
        if (popup == null){
            System.out.println("null1");
        }
        if (popup.equals(null)){
            System.out.println("null2");
        }
        Main.getPrimaryStage().toFront();
    }
    public void changeCount(int newCount){
        count.setText(newCount+"");
    }
}
