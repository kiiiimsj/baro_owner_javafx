package com.baro.Dialog;

import com.jfoenix.controls.JFXToggleButton;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailDialog implements Initializable {
    public interface OrderDetailDialogInterface {
        void ORDER_CANCEL();
        void ORDER_COMPLETE();
    }
    public final static int ORDER_CANCEL = 0;
    public final static int ORDER_COMPLETE = 1;

    public HBox top_bar;
    public Label dialog_content;
    public Button no;
    public Button yes;

    double initialX;
    double initialY;

    public int buttonType;
    public OrderDetailDialogInterface orderDetailDialogInterface;
    public OrderDetailDialog() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTopBar();
    }
    public void call(OrderDetailDialogInterface orderDetailDialogInterface, int buttonType){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/order_detail_dialog.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            OrderDetailDialog orderDetailDialog = loader.getController();
            orderDetailDialog.orderDetailDialogInterface = orderDetailDialogInterface;
            orderDetailDialog.buttonType = buttonType;

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            orderDetailDialog.configureBottom();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void configureTopBar() {
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
            case ORDER_CANCEL:
                dialog_content.setText("주문결제를 취소하시겠습니까?\n취소 시 취소결과가 고객에게 발송됩니다.");
                break;
            case ORDER_COMPLETE:
                dialog_content.setText("주문결제를 완료하시겠습니까?\n완료 시 완료결과가 고객에게 발송됩니다.");
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
                    case ORDER_CANCEL:
                        orderDetailDialogInterface.ORDER_CANCEL();
                        break;
                    case ORDER_COMPLETE:
                        orderDetailDialogInterface.ORDER_COMPLETE();
                        break;
                }
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
    }
}
