package com.baro;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.OrderController;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXTabPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.prefs.Preferences;


public class OrderListController {

    private final String TAG = this.getClass().getSimpleName();

    @FXML
    private JFXTabPane tabContainer;

    @FXML
    private Tab orderListTab;
    @FXML
    private AnchorPane orderListSideContainer;

    @FXML
    private Tab inventoryManagementTab;

    @FXML
    private AnchorPane inventoryManagementContainer;

    @FXML
    private Tab infoChangeTab;
    @FXML
    private AnchorPane infoChangeContainer;

    @FXML
    private Tab calculateTab;
    @FXML
    private AnchorPane calculateContainer;

    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane statisticsContainer;

    @FXML
    private Tab settingsTab;
    @FXML
    private AnchorPane settingsContainer;
    @FXML
    private TilePane orderListContainer;
    @FXML
    private ToggleButton isOpenBtn;
    private WebSocketClient webSocketClient;

    public static OrderList orderList;

    private double tabWidth = 90.0;
    public static int lastSelectedTabIndex = 0;
    String store_id;
    Preferences preferences = Preferences.userRoot();

    /// Life cycle
    @FXML
    public void initialize() {
        isOpenBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println(newValue);
                store_is_open_change(newValue);
            }
        });
        store_id = preferences.get("store_id", null);
        connect();
        configureSideView();
        configureOrderListView();
    }

    public void store_is_open_change(boolean is_open) {
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
                isOpenBtn.setText("영업 중");
                isOpenBtn.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                jsonObject.put("is_open", "N");
                isOpenBtn.setText("영업 종료");
                isOpenBtn.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
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
                System.out.println("성공");
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

    //주문 들어온 리스트 찍기
    private void configureOrderListView() {
        try {
            URL url = new URL("http://3.35.180.57:8080/OrderFindByStoreId.do?store_id=1");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());
            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if (result) {
                parsingOrders(bf.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsingOrders(String toString) {
        orderList = new Gson().fromJson(toString, OrderList.class);

        setList();
    }

    private VBox makeCell(int index) {
        VBox vBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/order.fxml"));
            vBox = loader.load();
            OrderController controller = loader.<OrderController>getController();
            controller.setData(orderList.orders.get(index));
            controller.configureUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vBox;
    }

    private void setList() {

        for (int i = orderList.orders.size() - 1; i >= 0; i--) {
            orderListContainer.getChildren().add(makeCell(i));
        }
    }

    private boolean menuUpdateSaveSoldOutParsing(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }

    /// Private
    private void configureSideView() {
        tabContainer.setTabMinWidth(tabWidth);
        tabContainer.setTabMaxWidth(tabWidth);
        tabContainer.setTabMinHeight(tabWidth);
        tabContainer.setTabMaxHeight(tabWidth);
        tabContainer.setRotateGraphic(true);
        EventHandler<Event> replaceBackgroundColorHandler = event -> {
            lastSelectedTabIndex = tabContainer.getSelectionModel().getSelectedIndex();
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                currentTab.setStyle("-fx-background-color: -fx-focus-color;");
            } else {
                currentTab.setStyle("-fx-background-color: -fx-accent;");
            }
        };
        EventHandler<Event> logoutHandler = event -> {
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                tabContainer.getSelectionModel().select(lastSelectedTabIndex);
            }
        };

        configureTab(orderListTab, "주 문", orderListSideContainer, getClass().getResource("order_list.fxml"), replaceBackgroundColorHandler);
        configureTab(inventoryManagementTab, "재고관리", inventoryManagementContainer, getClass().getResource("/inventory_management.fxml"), replaceBackgroundColorHandler);
        configureTab(infoChangeTab, "정보변경", infoChangeContainer, getClass().getResource("/info_change.fxml"), replaceBackgroundColorHandler);
        configureTab(calculateTab, "정 산", calculateContainer, getClass().getResource("/calculate.fxml"), replaceBackgroundColorHandler);
        configureTab(statisticsTab, "통 계", statisticsContainer, getClass().getResource("/statistics.fxml"), replaceBackgroundColorHandler);
        configureTab(settingsTab, "설 정", settingsContainer, getClass().getResource("/settings.fxml"), replaceBackgroundColorHandler);

        orderListTab.setStyle("-fx-background-color: -fx-focus-color;");
    }

    //이미지경로 넣기 위한 title 뒤에 String iconPath 뺏음
    private void configureTab(Tab tab, String title, AnchorPane containerPane, URL resourceURL, EventHandler<Event> onSelectionChangedEvent) {
        double imageWidth = 40.0;
        //ImageView imageView = new ImageView(new Image(iconPath));
        //imageView.setFitHeight(imageWidth);
        //imageView.setFitWidth(imageWidth);
        Label label = new Label(title);
        label.setMaxWidth(tabWidth - 20);
        label.setPadding(new Insets(5, 0, 0, 0));
        label.setStyle("-fx-text-fill: black; -fx-font-size: 8pt; -fx-font-weight: normal;");
        label.setTextAlignment(TextAlignment.CENTER);
        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setMaxWidth(tabWidth);
        //tabPane.setCenter(imageView);
        tabPane.setBottom(label);
        tab.setText("");
        tab.setGraphic(tabPane);
        tab.setOnSelectionChanged(onSelectionChangedEvent);
        if (containerPane != null && resourceURL != null) {
            try {
                Parent contentView = FXMLLoader.load(resourceURL);
                containerPane.getChildren().add(contentView);
                AnchorPane.setTopAnchor(contentView, 0.0);
                AnchorPane.setBottomAnchor(contentView, 0.0);
                AnchorPane.setRightAnchor(contentView, 0.0);
                AnchorPane.setLeftAnchor(contentView, 0.0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect() {
        System.out.println("aaa");
        URI uri;
        try {
            uri = new URI("ws://3.35.180.57:8080/websocket");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

                webSocketClient.send("connect:::" + 1);
                System.out.println("open!!");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("message!!");
                System.out.println(message);
                JSONObject jsonObject = new JSONObject(message);
//                JSONObject orderJsonObject = jsonObject.getJSONObject(orders);
                Order order = new Gson().fromJson(message, Order.class);
                order.order_state = Order.PREPARING;
                orderList.orders.add(order);
                System.out.println(orderList.orders.size());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        orderListContainer.getChildren().add(0, makeCell(orderList.orders.size() - 1));
                    }
                });

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("close! reaseon :" + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("error! :" + ex);
            }
        };
        webSocketClient.connect();
    }
}
