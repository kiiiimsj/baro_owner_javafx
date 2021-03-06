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

public class PrintDialog implements Initializable {
    public interface PrintDialogInterface {
      void MODIFY_PRINT(int index);
      void ADD_PRINT(int index);
      void DEL_PRINT(int index);
      void SET_MAIN(int index);
      void DEL_MAIN(int index);
    }
    public static final int MODIFY_PRINT = 0;
    public static final int ADD_PRINT = 1;
    public static final int DEL_PRINT = 3;
    public static final int SET_MAIN = 4;
    public static final int DEL_MAIN = 5;

    public Label dialog_content;
    public Button yes;
    public Button no;
    public HBox top_bar;
    public int buttonType;
    public AnchorPane dialog_base;

    public PrintDialogInterface printDialogInterface;

    double initialX;
    double initialY;

    public int index;
    public PrintDialog() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialog_base.setPrefWidth(LayoutSize.DIALOG_WIDTH);
        dialog_base.setPrefHeight(LayoutSize.DIALOG_HEIGHT);
        configureTopBar();
    }
    public void call(PrintDialogInterface printDialogInterface, int index, int buttonType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/print_dialog.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            PrintDialog printDialog = loader.getController();
            printDialog.printDialogInterface = printDialogInterface;
            printDialog.index = index;
            printDialog.buttonType = buttonType;

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            printDialog.configureBottom();
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
    private void configureBottom() {
        System.out.println(buttonType);
        switch (buttonType) {
            case MODIFY_PRINT:
                dialog_content.setText("수정사항을 저장하시겠습니까?");
                break;
            case ADD_PRINT:
                dialog_content.setText("새 프린트 설정을 저장하시겠습니까?");
                break;
            case DEL_PRINT:
                dialog_content.setText("프린트 설정을 삭제하시겠습니까?");
                break;
            case DEL_MAIN:
                dialog_content.setText("해당 프린트를 메인으로 설정하시겠습니까?\n메인으로 설정된 프린트로\n영수증이 출력됩니다.");
                break;
            case SET_MAIN:
                dialog_content.setText("메인 프린트를 삭제하시겠습니까?\n메인으로 설정된 프린트로\n영수증이 출력됩니다.");
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
                    case MODIFY_PRINT:
                        printDialogInterface.MODIFY_PRINT(index);
                        break;
                    case ADD_PRINT:
                        printDialogInterface.ADD_PRINT(index);
                        break;
                    case DEL_PRINT:
                        printDialogInterface.DEL_PRINT(index);
                        break;
                    case DEL_MAIN:
                        printDialogInterface.DEL_MAIN(index);
                        break;
                    case SET_MAIN:
                        printDialogInterface.SET_MAIN(index);
                        break;
                }
                Stage stage = (Stage)top_bar.getScene().getWindow();
                stage.close();
            }
        });
    }

}
