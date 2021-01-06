package sample;

import com.baro.Printer.ReceiptPrint;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Main extends Application {

    //websocket 변수 선언
    private WebSocketClient webSocketClient;
    private static Stage pStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("바로(BARO) 포스기");
        setPrimaryStage(primaryStage);
//        connect(); //웹소켓 잘 들어옴
//
//        ReceiptPrint print = new ReceiptPrint();
//        print.printReceipt();
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
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
}
