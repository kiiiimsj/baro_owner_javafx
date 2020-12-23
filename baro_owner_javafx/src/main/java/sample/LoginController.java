package sample;

import com.baro.JsonParsing.LoginParsing;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


public class LoginController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField phone_tf;
    @FXML private PasswordField password_tf;
    @FXML private Button login_btn;

    //데이터 저장을 위한 preferences
    Preferences preferences = Preferences.userRoot();

    LoginParsing loginParsing;

   @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("로그인 컨트롤러 실행!!");

    }

    public void loginAction(ActionEvent event) {

       //점주의 휴대폰번호 및 비밀번호
        String owner_phone = phone_tf.getText();
        String owner_pass = password_tf.getText();

        System.out.println("phone1" + owner_phone);
        System.out.println("pass1" + owner_pass);

       System.out.println("로그인 버튼 클릭!!");
       try{
           URL url = new URL("http://3.35.180.57:8080/OwnerLogin.do");
           URLConnection con = url.openConnection();
           HttpURLConnection http = (HttpURLConnection) con;
           http.setRequestMethod("POST");
           http.setRequestProperty("Content-Type","application/json;utf-8");
           http.setRequestProperty("Accept","application/json");
           http.setDoOutput(true);
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("phone", owner_phone);
           jsonObject.put("pass", owner_pass);
           OutputStream os = http.getOutputStream();

           byte[] input = jsonObject.toString().getBytes("utf-8");
           os.write(input, 0, input.length);
           BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
           String line;
           StringBuffer bf = new StringBuffer();

           while((line = br.readLine()) != null) {
               bf.append(line);
           }
           br.close();

           System.out.println("response" + bf.toString());

           boolean result = loginjsonParsing(bf.toString());

           //서버에서 response가 true 일때를 분기문에 추가시켜주기.
           if(result){
               Stage primaryStage = (Stage)login_btn.getScene().getWindow();
               Parent parent = FXMLLoader.load(getClass().getResource("/order_list.fxml"));
               Scene scene = new Scene(parent);
               primaryStage.setScene(scene);
               primaryStage.show();
           }
       }
       catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (ProtocolException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    private boolean loginjsonParsing(String result){
       Gson gson = new Gson();
        loginParsing = gson.fromJson(result, LoginParsing.class);
        boolean result1 = loginParsing.isResult();
        //result1 = true;
        if(result1) {
            //store_id, nick, store_name, email, is_open 저장
            preferences.put("store_id", loginParsing.getStore_id());
            preferences.put("nick", loginParsing.getNick());
            preferences.put("store_name", loginParsing.getStore_name());
            preferences.put("email", loginParsing.getEmail());
            preferences.put("is_open", loginParsing.getIs_open());
            //꺼낼때는 get 으로 써주고 두번째 파라미터에는 null 넣어주면 됨. 모르겠으면 물어봐
        }
        return result1;
    }
}
