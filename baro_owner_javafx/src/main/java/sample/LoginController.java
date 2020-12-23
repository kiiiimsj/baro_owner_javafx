package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField phone_textfield;
    @FXML private PasswordField password_textfield;
    @FXML private Button login_btn;

   @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("로그인 컨트롤러 실행!!");
    }

    public void loginAction(ActionEvent actionEvent) {
       System.out.println("로그인 버튼 클릭!!");
    }
}
