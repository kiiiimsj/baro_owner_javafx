package com.baro.Dialog;

import com.baro.utils.DateConverter;
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

public class NoDataDialog implements Initializable {
    public HBox top_bar;
    public Label dialog_content;
    public Button okay;
    public AnchorPane dialog_base;
    public String content;

    double initialX;
    double initialY;

    public NoDataDialog() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog_base.setPrefWidth(LayoutSize.DIALOG_WIDTH);
        dialog_base.setPrefHeight(LayoutSize.DIALOG_HEIGHT);
        configureTopBar();
    }
    public void call(){
        try {
            FXMLLoader loader = new FXMLLoader(Class.forName("com.baro.Dialog.NoDataDialog").getResource("/no_data_dialog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            NoDataDialog noDataDialog = loader.getController();
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            noDataDialog.configureBottom();
            stage.setWidth(LayoutSize.DIALOG_WIDTH);
            stage.setHeight(LayoutSize.DIALOG_HEIGHT);
            stage.setX(LayoutSize.CENTER_IN_PARENT_X);
            stage.setY(LayoutSize.CENTER_IN_PARENT_Y);

            stage.show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void call(String content){
        try {
            FXMLLoader loader = new FXMLLoader(Class.forName("com.baro.Dialog.NoDataDialog").getResource("/no_data_dialog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            NoDataDialog noDataDialog = loader.getController();
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            noDataDialog.content = content;
            noDataDialog.configureBottom();
            stage.setWidth(LayoutSize.DIALOG_WIDTH);
            stage.setHeight(LayoutSize.DIALOG_HEIGHT);
            stage.setX(LayoutSize.CENTER_IN_PARENT_X);
            stage.setY(LayoutSize.CENTER_IN_PARENT_Y);

            stage.show();
        } catch (IOException | ClassNotFoundException e) {
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
        if(content.equals("")) {
            dialog_content.setText("해당 기간동안의\n데이터가 없습니다");
        }else {
            if(content.length() > 10) {
                dialog_content.setText(DateConverter.enter(content));
            }else {
                dialog_content.setText(content);
            }

        }
        okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
    }
}
