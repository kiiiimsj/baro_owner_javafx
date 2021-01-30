package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;

public class Main extends Application {

    //websocket 변수 선언
    private WebSocketClient webSocketClient;
    private static Stage pStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("file.encoding","UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);

        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("바로(BARO) 포스기");
        setPrimaryStage(primaryStage);
//        connect(); //웹소켓 잘 들어옴
//
//        ReceiptPrint print = new ReceiptPrint();
//        print.printReceipt();


        // 300 -> 500
        Scene scene =  new Scene(root, 500, 500);

        //외부 폰트 적용을 위해 scene를 따로 빼주었음.
        //앞으로 폰트 적용은 아래 css 파일에서 해주면됨.
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());

        primaryStage.setScene(scene);
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
    public static void main(String[] args) {
        launch(args);
    }
}
