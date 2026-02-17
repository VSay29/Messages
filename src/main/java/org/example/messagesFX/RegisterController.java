package org.example.messagesFX;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.messagesFX.model.RegisterUserResponse;
import org.example.messagesFX.model.User;
import org.example.messagesFX.service.RegistroUser;
import org.example.messagesFX.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;

public class RegisterController {

    @FXML
    public Label userLbl;
    @FXML
    public Label passwdLbl;
    @FXML
    public Label repeatPasswdLbl;
    @FXML
    public TextField userTxt;
    @FXML
    public PasswordField passwdTxt;
    @FXML
    public PasswordField repeatPasswdTxt;
    @FXML
    public Button cancelBtn;
    @FXML
    public Button registerBtn;
    @FXML
    public Button selectImgBtn;
    @FXML
    public ImageView userImg;

    public void cancel(ActionEvent actionEvent) throws IOException {

        goLogin();

    }

    public void register(ActionEvent actionEvent) throws IOException {

        if (userTxt.getText().isEmpty() || passwdTxt.getText().isEmpty() || repeatPasswdTxt.getText().isEmpty() || userImg.getImage() == null) MessageUtils.showMessage("No se pueden tener campos vacíos", "Campos vacíos");
        else {
            if (!passwdTxt.getText().equals(repeatPasswdTxt.getText())) MessageUtils.showMessage("Las contraseñas deben ser la misma en los dos campos", "Contraseñas distintas");
            else {

                User user = new User(userTxt.getText(), passwdTxt.getText(), img_base64());
                RegistroUser regUs = new RegistroUser(user);

                registerBtn.setDisable(true);
                regUs.start();
                regUs.setOnSucceeded((e) -> {

                    RegisterUserResponse resp = regUs.getValue();

                    if (resp.isOk()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Registro completado");
                        alert.setHeaderText("Te has registrado con éxito");
                        alert.setContentText("Serás redirigido a la página de inicio de sesión");
                        alert.showAndWait();
                        try {
                            goLogin();
                        } catch (IOException ex) {
                            MessageUtils.showError("Ha ocurrido un error", "Error");
                        }
                    } else MessageUtils.showError("Error: El usuario no se pudo registrar", "Error en la operación");

                    clearAll();
                    registerBtn.setDisable(false);
                });
            }
        }

    }

    public void selectImage(ActionEvent actionEvent) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagen", "*.jpg"));

        Stage stage = (Stage) registerBtn.getScene().getWindow();

        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if(archivoSeleccionado != null) {
            Image image = new Image(archivoSeleccionado.toURI().toString());
            userImg.setImage(image);
        }

    }

    private String img_base64() throws IOException {
        File f = new File(userImg.getImage().getUrl().substring(6));
        byte[] bytes = Files.readAllBytes(f.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void clearAll() {
        userTxt.setText("");
        passwdTxt.setText("");
        repeatPasswdTxt.setText("");
        userImg.setImage(null);
    }

    private void goLogin() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/demo/login-view.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();

        Stage old = (Stage) registerBtn.getScene().getWindow();
        old.close();
    }


}
