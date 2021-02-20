package com.baro.Dialog;

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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrintDialog implements Initializable {
    public interface PrintDialogInterface {
        void no();
        void yes();
    }
    public static final int SAVE = 0;
    public static final int DEL = 1;
    public static final int ADD_MAIN = 2;

    public Label dialog_content;
    public Button yes;
    public Button no;
    public HBox top_bar;
    public FontAwesomeIconView close;
    public int buttonType;

    public PrintDialogInterface printDialogInterface;

    double initialX;
    double initialY;
    public PrintDialog() {
        super();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTopBar();
        configureBottom();
    }
    public void call(PrintDialogInterface printDialogInterface, int buttonType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/print_dialog.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            PrintDialog printDialog = loader.getController();
            printDialog.printDialogInterface = printDialogInterface;
            printDialog.buttonType = buttonType;
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);

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
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage)close.getScene().getWindow();
                stage.close();
            }
        });
    }
    private void configureBottom() {
        System.out.println(buttonType);
        switch (buttonType) {
            case SAVE:
                dialog_content.setText("프린트 설정을 저장하시겠습니까?");
                break;
            case DEL:
                dialog_content.setText("프린트 설정을 삭제하시겠습니까?");
                break;
            case ADD_MAIN:
                dialog_content.setText("해당 프린트를 메인으로 설정하시겠습니까?\n메인으로 설정된 프린트로 영수증이 출력됩니다.");
                break;

        }
        no.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                printDialogInterface.no();
                Stage stage = (Stage)close.getScene().getWindow();
                stage.close();
            }
        });
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                printDialogInterface.yes();
                Stage stage = (Stage)close.getScene().getWindow();
                stage.close();
            }
        });
    }

}
