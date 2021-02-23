package com.baro.Dialog;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

    double initialX;
    double initialY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTopBar();
        configureBottom();
    }

    public interface Reload{
        void reload();
    }
    public Reload reload;

    public InternetConnectDialog() {
        super();
    }
    private void configureTopBar() {
//        final Timeline digitalTime = new Timeline(
//                new KeyFrame(Duration.seconds(0),
//                        new EventHandler<ActionEvent>() {
//                            @Override public void handle(ActionEvent actionEvent) {
//                                Calendar calendar            = GregorianCalendar.getInstance();
//                                String hourString   = DateConverter.pad(2, '0', calendar.get(Calendar.HOUR)   == 0 ? "12" : calendar.get(Calendar.HOUR) + "");
//                                String minuteString = DateConverter.pad(2, '0', calendar.get(Calendar.MINUTE) + "");
//                                //String secondString = pad(2, '0', calendar.get(Calendar.SECOND) + "");
//                                String ampmString   = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
//
//                                //":" + secondString +
//                                digital_clock.setText(hourString + ":" + minuteString + " " + ampmString);
//                            }
//                        }
//                ),
//                new KeyFrame(Duration.seconds(1))
//        );
//        digitalTime.setCycleCount(Animation.INDEFINITE);
//        digitalTime.play();
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
//        top_bar.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                top_bar.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent event) {
//                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//                        stage.setMaxWidth(1400);
//                        stage.setMaxHeight(900);
//                    }
//                });
//            }
//        });
//        minimum.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//                stage.setIconified(true);
//            }
//        });
//        maximum.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//                if(stage.isFullScreen()) {
//                    stage.setFullScreen(false);
//                }else {
//                    //stage.setFullScreenExitHint(" ");
//                    stage.setFullScreen(true);
//                }
//            }
//        });
//        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                Stage stage = (Stage)close.getScene().getWindow();
//                stage.close();
//            }
//        });
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

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
