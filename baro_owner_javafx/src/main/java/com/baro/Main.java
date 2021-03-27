package com.baro;

import com.baro.Dialog.InternetConnectDialog;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.LoginController;
import com.baro.controllers.MainController;
import com.baro.utils.LayoutSize;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.prefs.Preferences;

public class Main extends Application implements MainController.ReturnOrderListWhenApplicationClose, InternetConnectDialog.Reload {
    //websocket 변수 선언
    private WebSocketClient webSocketClient;
    private static Stage pStage;
    public static Preferences preferences = Preferences.userRoot();
    public static OrderList orderList;


    @Override
    public void init() throws Exception {
        super.init();
        Font.loadFont(Main.class.getResource("/fonts/NotoSansCJKkr-Thin.otf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("/fonts/NotoSansCJKkr-Regular.otf").toExternalForm(), 10);
        Font.loadFont(Main.class.getResource("/fonts/NotoSansCJKkr-Bold.otf").toExternalForm(), 10);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("file.encoding","UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image("icon/appicon_512_foreground.png"));
        primaryStage.setTitle("바로(BARO) 포스기");

        LoginController loginController = loader.<LoginController>getController();
        loginController.returnOrderListWhenApplicationClose = this;
        loginController.reload = this;

        setPrimaryStage(primaryStage);

//        connect(); //웹소켓 잘 들어옴
//
//        ReceiptPrint print = new ReceiptPrint();
//        print.printReceipt();


        // 300 -> 500 -> 580
        Scene scene =  new Scene(root, LayoutSize.LOGIN_PAGE_WIDTH, LayoutSize.LOGIN_PAGE_HEIGHT);
        primaryStage.setX(LayoutSize.CENTER_X);
        primaryStage.setY(LayoutSize.CENTER_Y);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void reload(Stage primaryStage) {
        primaryStage.close();
        Platform.runLater( () -> {
            try {
                new Main().start( new Stage() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static Stage getPrimaryStage() {
        return pStage;
    }
    private void setPrimaryStage(Stage pStage) {
        Main.pStage = pStage;
    }
    //================================================================
    //websocket
    private void connect() {
        System.out.println("aaa");
        URI uri;
        try {
            uri = new URI("ws://3.35.180.57:8080/websocket");
        }
        catch(Exception e){
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
                System.out.println(message);
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
    public static void main(String[] args) {


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("call stop");
                    for (int i = 0; i < orderList.orders.size(); i++) {
                        if (orderList.orders.get(i).completeTime != null && !orderList.orders.get(i).completeTime.equals("")) {
                            preferences.put(orderList.orders.get(i).receipt_id, orderList.orders.get(i).receipt_id);
                            preferences.put(orderList.orders.get(i).receipt_id + "time", orderList.orders.get(i).getCompleteTime());
                            System.out.println(orderList.orders.get(i).getCompleteTime());
                        }
                    }
                    System.out.println("finish for loop");
//                    store_is_open_change(false, true);
                }
        }));
        launch(args);
    }

    @Override
    public void returnOrderList(OrderList orderList) {
        this.orderList = orderList;
    }

    @Override
    public void reload() {
        this.reload(pStage);
    }
}
