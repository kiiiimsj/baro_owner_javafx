package sample;

import com.baro.controllers.OrderController;
import com.baro.utils.LayoutWidthHeight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image("icon/appicon_512_foreground.png"));
        primaryStage.setTitle("바로(BARO) 포스기");
        setPrimaryStage(primaryStage);

//        connect(); //웹소켓 잘 들어옴
//
//        ReceiptPrint print = new ReceiptPrint();
//        print.printReceipt();


        // 300 -> 500 -> 580
        Scene scene =  new Scene(root, LayoutWidthHeight.LOGIN_PAGE_WIDTH, LayoutWidthHeight.LOGIN_PAGE_HEIGHT);

//        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
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
