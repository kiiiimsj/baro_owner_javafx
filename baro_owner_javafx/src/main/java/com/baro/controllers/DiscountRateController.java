package com.baro.controllers;

import com.baro.Dialog.DiscountRateDialog;
import com.baro.utils.GetBool;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class DiscountRateController implements Initializable, DiscountRateDialog.DiscountRateDialogInterface {
    public HBox top_bar;
    public Button plus;
    public Button minus;

    public interface ClickClose{
        void clickClose();
        void clickSet();
    }
    public int discountRate = 0;
    public Label setNewDiscountRate;
    public Preferences preferences = Preferences.userRoot();
    public Button setButton;
    public String storeId;
    public FontAwesomeIconView close;
    public ClickClose clickClose;

    private double initialX;
    private double initialY;

    public DiscountRateDialog discountRateDialog;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        discountRateDialog = new DiscountRateDialog();
        top_bar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    initialX = me.getSceneX();
                    initialY = me.getSceneY();
                }
            }
        });

        top_bar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    top_bar.getScene().getWindow().setX(me.getScreenX() - initialX);
                    top_bar.getScene().getWindow().setY(me.getScreenY() - initialY);
                }
            }
        });
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clickClose.clickClose();
                Stage stage = (Stage) setButton.getScene().getWindow();
                stage.close();
            }
        });
        plus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String getNewDiscountRateString = setNewDiscountRate.getText().toString();
                int discountRate = Integer.parseInt(getNewDiscountRateString);
                if(discountRate == 100) {
                    return;
                }
                discountRate++;
                setNewDiscountRate.setText(discountRate+"");
            }
        });
        minus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String getNewDiscountRateString = setNewDiscountRate.getText().toString();
                int discountRate = Integer.parseInt(getNewDiscountRateString);
                if(discountRate == 0) {
                    return;
                }
                discountRate--;
                setNewDiscountRate.setText(discountRate+"");
            }
        });
    }
    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
        setNewDiscountRate.setText(discountRate+"");
    }

    public void clickSetButton(ActionEvent actionEvent) {
        String getText = setNewDiscountRate.getText().toString();
        if(getText.equals("")) {
            return;
        }
        int newDiscountRate = Integer.parseInt(getText);
        if(discountRate == newDiscountRate) {
            System.out.println("equals");
            return;
        }
        discountRateDialog.call(this, DiscountRateDialog.CHANGE_DISCOUNT_RATE);
    }
    @Override
    public void CHANGE_DISCOUNT_RATE() {
        int newDiscountRate = Integer.parseInt(setNewDiscountRate.getText());
        preferences.putInt("new_discount_rate", newDiscountRate);


        clickClose.clickSet();
        Stage stage = (Stage) setButton.getScene().getWindow();
        stage.close();
    }
}
