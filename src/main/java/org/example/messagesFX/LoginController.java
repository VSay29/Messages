package org.example.messagesFX;

import java.io.IOException;
import java.lang.classfile.Label;
import java.util.Objects;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.messagesFX.model.LoginUserResponse;
import org.example.messagesFX.model.SharedInfo;
import org.example.messagesFX.service.LoginUser;
import org.example.messagesFX.service.ServiceUtils;
import org.example.messagesFX.utils.MessageUtils;

public class LoginController {


    @FXML
    public Label userLbl;
    @FXML
    public Label passwordLbl;
    @FXML
    public Button loginBtn;
    @FXML
    public Hyperlink notAccountLink;
    @FXML
    public TextField userTxt;
    @FXML
    public PasswordField passwordTxt;

    // FUNCIONES

    /**
     * ActionEvent para el botón login del usuario
     * @param actionEvent
     * @return
     * @throws IOException
     */

    public void loggear(ActionEvent actionEvent) throws IOException {

        //String token = SharedInfo.currentToken;

        if (userTxt.getText().trim().isEmpty() || passwordTxt.getText().trim().isEmpty()) MessageUtils.showMessage("No se pueden tener campos vacíos", "Campos vacíos");
        else {
            LoginUser logU = new LoginUser(userTxt.getText(), passwordTxt.getText());
            loginBtn.setDisable(true);
            logU.start();
            logU.setOnSucceeded((e) -> {
                LoginUserResponse resp = logU.getValue();
                if (resp.isOk()) {
                    MessageUtils.showMessage("Se ha iniciado sesión correctamente", "Login realizado con éxito");
                    SharedInfo.currentToken = resp.getToken();
                    //System.out.println("Id loggeo: " + resp.getId());
                    SharedInfo.currentId = resp.getId();
                    SharedInfo.currentName = resp.getName();
                    SharedInfo.currentImage = resp.getImage();
                    ServiceUtils.setToken(SharedInfo.currentToken);
                    try {
                        goMessages();
                    } catch (IOException ex) {
                        MessageUtils.showError("Error: " + ex.getMessage(), "Error");
                    }

                } else MessageUtils.showError("Usuario o contraseña incorrectos", "Error");
                loginBtn.setDisable(false);
            });
        }
    }

    public void goRegister(ActionEvent actionEvent) throws IOException {
        String path = "/org/example/demo/register-view.fxml";
        String title = "Registro";
        goView(path, title);
    }

    public void goMessages() throws IOException {
        String path = "/org/example/demo/messages-view.fxml";
        String title = "Mensajería";
        goView(path, title);
    }

    private void goView(String path, String title) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();

        Stage old = (Stage) loginBtn.getScene().getWindow();
        old.close();
    }

}
