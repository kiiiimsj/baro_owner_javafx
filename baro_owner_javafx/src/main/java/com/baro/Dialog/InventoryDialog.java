package com.baro.Dialog;

import com.baro.utils.LayoutSize;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InventoryDialog implements Initializable {
    public interface InventoryDialogInterface {
        void SET_STOCK_OUT(int menuId,JFXToggleButton toggleButton);
        void SET_RE_STOCK(int menuId,JFXToggleButton toggleButton);
    }
    public static final int SET_STOCK_OUT = 0;
    public static final int SET_RE_STOCK = 1;

    public HBox top_bar;
    public Label dialog_content;
    public Button no;
    public Button yes;
    public int menuId;
    public int buttonType;
    public AnchorPane dialog_base;

    double initialX;
    double initialY;

    public JFXToggleButton toggleButton;

    public InventoryDialogInterface inventoryDialogInterface;

    public InventoryDialog() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog_base.setPrefWidth(LayoutSize.DIALOG_WIDTH);
        dialog_base.setPrefHeight(LayoutSize.DIALOG_HEIGHT);
        configureTopBar();
    }

    public void call(InventoryDialogInterface inventoryDialogInterface, int menuId, int buttonType, JFXToggleButton toggleButton){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventory_dialog.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            InventoryDialog inventoryDialog = loader.getController();
            Stage stage = new Stage(StageStyle.UNDECORATED);
            inventoryDialog.inventoryDialogInterface = inventoryDialogInterface;
            inventoryDialog.menuId = menuId;
            inventoryDialog.buttonType = buttonType;
            inventoryDialog.toggleButton = toggleButton;
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            inventoryDialog.configureBottom();
            stage.setWidth(LayoutSize.DIALOG_WIDTH);
            stage.setHeight(LayoutSize.DIALOG_HEIGHT);
            stage.setX(LayoutSize.CENTER_IN_PARENT_X);
            stage.setY(LayoutSize.CENTER_IN_PARENT_Y);

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
            case SET_STOCK_OUT:
                dialog_content.setText("상품을 품절 상태로\n변경하시겠습니까?");
                break;
            case SET_RE_STOCK:
                dialog_content.setText("상품을 판매가능 상태로\n변경하시겠습니까?");
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
                    case SET_STOCK_OUT:
                        inventoryDialogInterface.SET_STOCK_OUT(menuId, toggleButton);
                        break;
                    case SET_RE_STOCK:
                        inventoryDialogInterface.SET_RE_STOCK(menuId, toggleButton);
                        break;
                }
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
    }
}
