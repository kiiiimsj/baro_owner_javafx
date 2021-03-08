package com.baro.Dialog;

import com.baro.utils.LayoutSize;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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

public class InternetConnectDialog implements Initializable {
    public FontAwesomeIconView close;
    public HBox top_bar;
    public Button okay;
    public AnchorPane dialog_base;
    public Label dialog_content;

    double initialX;
    double initialY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureContent();
        configureTopBar();
        configureBottom();
    }

    private void configureContent() {
        dialog_base.setPrefWidth(LayoutSize.DIALOG_WIDTH);
        dialog_base.setPrefHeight(LayoutSize.DIALOG_HEIGHT);
        dialog_content.setText("인터넷 연결이 끊겼습니다.\n확인 버튼을 누르면 프로그램을\n재시작 합니다.");
    }

    public interface Reload{
        void reload();
    }
    public Reload reload;

    public InternetConnectDialog() {
        super();
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
    private void configureBottom() {
        okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage)okay.getScene().getWindow();
                stage.close();
                reload.reload();
            }
        });
    }
    public void call(Reload reload) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/internet_connect_dialog.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            InternetConnectDialog internetConnectDialog = loader.getController();
            internetConnectDialog.reload = reload;
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setWidth(LayoutSize.DIALOG_WIDTH);
            stage.setHeight(LayoutSize.DIALOG_HEIGHT);
            stage.setX(LayoutSize.CENTER_IN_PARENT_X);
            stage.setY(LayoutSize.CENTER_IN_PARENT_Y);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
