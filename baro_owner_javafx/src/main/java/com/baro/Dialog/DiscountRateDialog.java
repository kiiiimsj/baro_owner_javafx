package com.baro.Dialog;

import com.baro.utils.LayoutSize;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscountRateDialog implements Initializable {
    public AnchorPane dialog_base;

    public interface DiscountRateDialogInterface {
        void CHANGE_DISCOUNT_RATE();
    }
    public final static int CHANGE_DISCOUNT_RATE = 0;

    public HBox top_bar;
    public Label dialog_content;
    public Button no;
    public Button yes;

    double initialX;
    double initialY;

    public int buttonType;
    public DiscountRateDialogInterface discountRateDialogInterface;
    public DiscountRateDialog() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog_base.setPrefWidth(LayoutSize.DIALOG_WIDTH);
        dialog_base.setPrefHeight(LayoutSize.DIALOG_HEIGHT);
        configureTopBar();
    }
    public void call(DiscountRateDialogInterface discountRateDialogInterface, int buttonType){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/discount_rate_dialog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            DiscountRateDialog discountRateDialog = loader.getController();
            discountRateDialog.discountRateDialogInterface = discountRateDialogInterface;
            discountRateDialog.buttonType = buttonType;


            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setWidth(LayoutSize.DIALOG_WIDTH);
            stage.setHeight(LayoutSize.DIALOG_HEIGHT);
            stage.setX(LayoutSize.CENTER_IN_PARENT_X);
            stage.setY(LayoutSize.CENTER_IN_PARENT_Y);

            stage.setScene(scene);
            discountRateDialog.configureBottom();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void configureTopBar() {
        top_bar.setPrefHeight(LayoutSize.DIALOG_TOP_BAR_HEIGHT);
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
    }
    public void configureBottom() {
        switch (buttonType) {
            case CHANGE_DISCOUNT_RATE:
                dialog_content.setText("할인률을 변경하시겠습니까?\n변경된 할인률은 1시간 뒤에 적용됩니다.");
                break;
        }
        no.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (buttonType) {
                    case CHANGE_DISCOUNT_RATE:
                        discountRateDialogInterface.CHANGE_DISCOUNT_RATE();
                        break;
                }
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
    }
}
