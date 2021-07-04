package com.baro.controllers;

import com.baro.Dialog.InternetConnectDialog;
import com.baro.JsonParsing.OrderList;
import com.baro.utils.DateConverter;
import com.baro.utils.LayoutSize;
import com.jfoenix.controls.JFXTabPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;


public class MainController implements OrderListController.MoveToSetting{
    public interface ReturnOrderListWhenApplicationClose {
        void returnOrderList(OrderList orderList);
    }
    @Override
    public void moveSetting() {
        tabContainer.getSelectionModel().selectLast();
    }
    OrderListController orderListController;

    public Label digital_clock;
    @FXML public HBox top_bar;
    public FontAwesomeIconView minimum;
//    public FontAwesomeIconView maximum;
    public FontAwesomeIconView close;

    public StackPane main_page_stack_pane;

    public OrderList orderList;
    public InternetConnectDialog.Reload reload;
    @FXML
    private JFXTabPane tabContainer;
    @FXML
    private Tab order_listTab;
    @FXML
    private AnchorPane orderListSideContainer;
    @FXML
    private Tab inventory_managementTab;
    @FXML
    private AnchorPane inventoryManagementContainer;
    @FXML
    private Tab orderHistoryTab;
    @FXML
    private AnchorPane infoChangeContainer;
    @FXML
    private Tab calculateTab;
    @FXML
    private AnchorPane calculateContainer;
    @FXML
    private Tab statisticTab;
    @FXML
    private AnchorPane statisticContainer;
    @FXML
    private Tab settingsTab;
    @FXML
    private AnchorPane settingsContainer;
    @FXML
    private TilePane orderListContainer;

    private final double tabWidth = LayoutSize.MAIN_TAB_PANE_WIDTH;
    private final double tabHeight = LayoutSize.MAIN_TAB_PANE_HEIGHT;

    double initialX;
    double initialY;

    private SimpleIntegerProperty notReadedOrder = new SimpleIntegerProperty();
    public ReturnOrderListWhenApplicationClose returnOrderListWhenApplicationClose;
    String store_id;
    Preferences preferences = Preferences.userRoot();
    public MainController(ReturnOrderListWhenApplicationClose returnOrderListWhenApplicationClose) {
        this.returnOrderListWhenApplicationClose = returnOrderListWhenApplicationClose;
    }
    @FXML
    public void initialize() {
        main_page_stack_pane.setPrefHeight(LayoutSize.MAIN_PAGE_HEIGHT);
        main_page_stack_pane.setPrefWidth(LayoutSize.MAIN_PAGE_WIDTH);


        store_id = preferences.get("store_id", null);

        configureSideView();
        configureTopBar();

//        try {
//            connect();
//        } catch (NoRouteToHostException e) {
//            InternetConnectDialog internetConnectDialog = new InternetConnectDialog();
//            internetConnectDialog.call(reload);
//        }
    }
    private void configureTopBar() {
        top_bar.setPrefHeight(LayoutSize.TOP_BAR_HEIGHT);

        final Timeline digitalTime = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent actionEvent) {
                                Calendar calendar            = GregorianCalendar.getInstance();
                                String hourString   = DateConverter.pad(2, '0', calendar.get(Calendar.HOUR)   == 0 ? "12" : calendar.get(Calendar.HOUR) + "");
                                String minuteString = DateConverter.pad(2, '0', calendar.get(Calendar.MINUTE) + "");
                                //String secondString = pad(2, '0', calendar.get(Calendar.SECOND) + "");
                                String ampmString   = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                                //":" + secondString +
                                digital_clock.setText(hourString + ":" + minuteString + " " + ampmString);
                            }
                        }
                ),
                new KeyFrame(Duration.seconds(1))
        );
        digitalTime.setCycleCount(Animation.INDEFINITE);
        digitalTime.play();



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
        minimum.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });
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
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Main : " + orderList.toString());
                for (int i = 0; i < orderList.orders.size(); i++) {
                    if(orderList.orders.get(i).completeTime != null &&!orderList.orders.get(i).completeTime.equals("")) {
                        preferences.put(orderList.orders.get(i).receipt_id, orderList.orders.get(i).receipt_id);
                        preferences.put(orderList.orders.get(i).receipt_id+"time", orderList.orders.get(i).getCompleteTime());
                    }
                }
                store_is_open_change(false, true);
            }
        });
    }

    public void store_is_open_change(boolean is_open, boolean isFromClose) {
        try {
            URL url = new URL("http://3.35.180.57:8080/OwnerSetStoreStatus.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            if (is_open) {
                jsonObject.put("is_open", "Y");
            } else {
                jsonObject.put("is_open", "N");
            }

            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();
            System.out.println("response" + bf.toString());
            boolean result = getBool(bf.toString());

            if (result) {
                if(isFromClose) {
//                    Stage stage = (Stage)main_page_stack_pane.getScene().getWindow();
//                    stage.close();
                    System.exit(-1);
                    Platform.exit();

                }
            } else {
                System.out.println("실패");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getBool(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
    private void configureSideView() {
        preConfigureTabForOrderList(order_listTab, orderListSideContainer);

        setTabs(order_listTab, "주문");
        setTabs(inventory_managementTab, "재고관리");
        setTabs(orderHistoryTab, "주문내역");
        setTabs(calculateTab, "정산");
        setTabs(statisticTab, "통계");
        setTabs(settingsTab, "설정");

        tabContainer.setTabMinWidth(tabWidth);
        tabContainer.setTabMaxWidth(tabWidth);
        tabContainer.setTabMinHeight(tabHeight);
        tabContainer.setTabMaxHeight(tabHeight);
        tabContainer.setRotateGraphic(true);
        tabContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                switch (observable.getValue().getId()) {
                    case "order_listTab":
                        configureTab(order_listTab, orderListSideContainer);
                        break;
                    case "inventory_managementTab":
                        configureTab(inventory_managementTab, inventoryManagementContainer );
                        break;
                    case "orderHistoryTab":
                        configureTab(orderHistoryTab, infoChangeContainer);
                        break;
                    case "calculateTab":
                        configureTab(calculateTab,  calculateContainer);
                        break;
                    case "statisticTab":
                        configureTab(statisticTab,  statisticContainer);
                        break;
                    case "settingsTab":
                        configureTab(settingsTab,  settingsContainer);
                        break;
                    default:
                        break;
                }
                oldValue.setStyle("-fx-background-color: #8D45E7");
            }
        });
        order_listTab.setStyle("-fx-background-color: #8333e6");
        //tabContainer.getSelectionModel().select(inventory_managementTab);
        tabContainer.getSelectionModel().select(order_listTab);
    }
    private void setTabs(Tab tab, String title) {
        double imageWidth = 40.0;
//        ImageView imageView = new ImageView(new Image());
//        imageView.setFitHeight(imageWidth);
//        imageView.setFitWidth(imageWidth);
//        tabPane.setCenter(imageView);
        Label label = new Label(title);
        label.setMaxWidth(tabHeight);
        label.setMinWidth(tabHeight);
        label.setPadding(new Insets(0, 0, 0, 0));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: normal;");
        label.setAlignment(Pos.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setMaxWidth(tabHeight);
        tabPane.setMinWidth(tabHeight);
        tabPane.setCenter(label);
        tab.setGraphic(tabPane);
    }
    //이미지경로 넣기 위한 title 뒤에 String iconPath 뺏음
    private void preConfigureTabForOrderList(Tab tab, AnchorPane containerPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/order_list.fxml"));
            Parent contentView = loader.load();

            orderListController = loader.<OrderListController>getController();
            orderListController.moveToSetting = this::moveSetting;
            orderListController.reload = reload;

            DateConverter.onHourTimerStart(orderListController.baro_discount_timer, orderListController);

            orderList = orderListController.orderList;
            returnOrderListWhenApplicationClose.returnOrderList(orderList);

            containerPane.getChildren().add(contentView);

            AnchorPane.setTopAnchor(contentView, 0.0);
            AnchorPane.setBottomAnchor(contentView, 0.0);
            AnchorPane.setRightAnchor(contentView, 0.0);
            AnchorPane.setLeftAnchor(contentView, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void configureTab(Tab tab, AnchorPane containerPane) {
        tab.setStyle("-fx-background-color: #8333e6");
        try {
            if(tab.getId().equals("order_listTab")) {
                orderListController.configureOrderListView();
            }else {
                containerPane.getChildren().clear();
                System.out.println("getId" + tab.getId().substring(0, tab.getId().indexOf("Tab")));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/"+tab.getId().substring(0, tab.getId().indexOf("Tab"))+".fxml"));
                Parent contentView = loader.load();
//                DateConverter.fifteenTimerStop();
                containerPane.getChildren().add(contentView);

                AnchorPane.setTopAnchor(contentView, 0.0);
                AnchorPane.setBottomAnchor(contentView, 0.0);
                AnchorPane.setRightAnchor(contentView, 0.0);
                AnchorPane.setLeftAnchor(contentView, 0.0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
