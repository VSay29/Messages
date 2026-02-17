package org.example.messagesFX;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.messagesFX.model.*;
import org.example.messagesFX.service.*;
import org.example.messagesFX.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MessagesController {

    @FXML
    public Label userLbl;
    @FXML
    public Label titleLbl;
    @FXML
    public Button changeImgBtn;
    @FXML
    public Button refreshBtn;
    @FXML
    public ImageView userImg;
    @FXML
    public TableView<Message> messageTbl;
    @FXML
    public TableColumn<Message, String> messageCol;
    @FXML
    public TableColumn<Message, String> imgCol;
    @FXML
    public TableColumn<Message, String> sentCol;

    @FXML
    public TextField messageTxt;
    @FXML
    public Button sendBtn;
    @FXML
    public Button imgSelectBtn;
    @FXML
    public Button deleteBtn;
    @FXML
    public TableView<User> usersTbl;
    @FXML
    public TableColumn<User, String> AvatarCol;
    @FXML
    public TableColumn<User, String> NickCol;

    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private String imgBase64 = "";

    @FXML
    public void initialize() {

        // CARGAR TABLA DE MENSAJES Y USUARIOS

        // Configuaración columnas de la tabla mensajes

        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        sentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSent()));
        imgCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImage()));

        // Configuración columnas de la tabla usuarios

        AvatarCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImage()));
        NickCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        userLbl.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(e -> {

                            // CARGAR VENTANA Y DATOS DEL USUARIO LOGGEADO

                            //System.out.println("La ventana se ha cargado");
                            userLbl.setText(SharedInfo.currentName);
                            userImg.setImage(base64_img());

                            // CARGAR MENSAJES

                            GetMessages lista = new GetMessages();
                            lista.start();
                            lista.setOnSucceeded(event -> {
                                messages = lista.getValue();
                                rellenarTabla(messageTbl, messages);
                            });

                            // CARGAR USUARIO PARA MENSAJEAR

                            GetUsers listaUsers = new GetUsers();
                            listaUsers.start();
                            listaUsers.setOnSucceeded(event -> {
                                users = listaUsers.getValue();
                                users.removeIf(user -> user.getId().equals(SharedInfo.currentId));
                                //System.out.println(users);
                                //System.out.println("Id de mi usuario: " + SharedInfo.currentId);
                                //users.forEach(u -> System.out.println("Id del usuario: " + u.getId()));
                                rellenarTabla(usersTbl, users);
                            });

                        });
                    }
                });
            }
        });

        // AGREGAR LISTENER AL BOTÓN DELETE

        messageTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteBtn.setDisable(newSelection == null);
        });

        // AGREGAR LISTENER AL BOTÓN SEND MESSAGE

        messageTxt.textProperty().addListener((obs, oldText, newText) -> {
            boolean selected = usersTbl.getSelectionModel().getSelectedIndex() > -1;
            sendBtn.setDisable(newText.isEmpty() || !selected);
        });

    }

    public void changeImage(ActionEvent actionEvent) throws IOException {

        String token = SharedInfo.currentToken;
        if (token != null) {

            // Elegir archivo de imagen

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo de imagen");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagen", "*.jpg"));

            Stage stage = (Stage) changeImgBtn.getScene().getWindow();

            File archivoSeleccionado = fileChooser.showOpenDialog(stage);

            if (archivoSeleccionado != null) {
                Image image = new Image(archivoSeleccionado.toURI().toString());
                userImg.setImage(image);
            }

            // Modificarlo en el servicio

            String imgBase64 = img_base64();

            if (imgBase64.isEmpty()) MessageUtils.showError("Error", "No se ha podido cargar el archivo");
            else {
                MessageUtils.showMessage("Imagen cargada", "Operación realizada con éxito");
                UpdateUser updUser = new UpdateUser(imgBase64);
                changeImgBtn.setDisable(true);
                refreshBtn.setDisable(true);

                updUser.start();
                updUser.setOnSucceeded((e) -> {
                    UpdateUserResponse resp = updUser.getValue();
                    if (resp.isOk()) {
                        MessageUtils.showMessage("Foto de perfil actualizada", "Éxito");
                        SharedInfo.currentImage = imgBase64;
                    } else MessageUtils.showError("La foto de perfil no fue actualizada", "Error");
                });

                changeImgBtn.setDisable(false);
                refreshBtn.setDisable(false);
            }

        }

    }

    public void refresh(ActionEvent actionEvent) {
        messageTbl.getItems().clear();
        usersTbl.getItems().clear();

        GetMessages listaMessages = new GetMessages();
        listaMessages.start();

        listaMessages.setOnSucceeded(event -> {
            messageTbl.setItems(FXCollections.observableList(listaMessages.getValue()));
        });

        GetUsers listaUsers = new GetUsers();
        listaUsers.start();

        listaUsers.setOnSucceeded(event -> {
            listaUsers.getValue().removeIf(user -> user.getId().equals(SharedInfo.currentId));
            usersTbl.setItems(FXCollections.observableList(listaUsers.getValue()));
        });
    }

    private Image base64_img() {
        byte[] bytes = java.util.Base64.getDecoder().decode(SharedInfo.currentImage);
        return new javafx.scene.image.Image(new java.io.ByteArrayInputStream(bytes));
    }

    private String img_base64() throws IOException {
        File f = new File(userImg.getImage().getUrl().substring(6));
        byte[] bytes = Files.readAllBytes(f.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    private <T> void rellenarTabla(TableView<T> tabla, List<T> lista) {
        tabla.setItems(FXCollections.observableList(lista));
    }

    public void deleteMessage(ActionEvent actionEvent) {
        Message m = messageTbl.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar mensaje: " + m.getId());
        alert.setHeaderText("Borrar mensaje '" + m.getMessage());
        alert.setContentText("¿Estás seguro que quieres borrar este mensaje?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            DeleteMessage del = new DeleteMessage(m.getId());
            del.start();

            del.setOnSucceeded(event -> {
                DeleteMessageResponse resp = del.getValue();
                if (resp.isOk()) {
                    MessageUtils.showMessage("El mensaje fue eliminado", "Operación realizada con éxito");
                    messageTbl.getItems().remove(m);
                    messageTbl.getSelectionModel().clearSelection();
                } else MessageUtils.showError("El mensaje no pudo ser borrado con éxito", "Error");
            });
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageTxt.getText();
        String to = usersTbl.getSelectionModel().getSelectedItem().getId();
        Message m = new Message(to, message);
        if(!imgBase64.isEmpty()) m.setImage(imgBase64);
        SendMessage sendMess = new SendMessage(m, m.getTo());
        sendMess.start();
        sendMess.setOnSucceeded(event -> {
            SendMessageResponse resp = sendMess.getValue();
            //System.out.println(resp);
            if (!resp.isOk()) MessageUtils.showError("El mensaje no pudo ser enviado", "Error");
            else MessageUtils.showMessage("El mensaje ha sido enviado con éxito", "Mensaje enviado");
        });
    }

    public void selectImage(ActionEvent actionEvent) throws IOException {

        // Elegir archivo de imagen

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagen", "*.jpg"));

        Stage stage = (Stage) changeImgBtn.getScene().getWindow();

        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if (archivoSeleccionado != null) {
            Image image = new Image(archivoSeleccionado.toURI().toString());
            userImg.setImage(image);
        }

        imgBase64 = img_base64();

        if (imgBase64.isEmpty()) {
            MessageUtils.showError("La imagen no se cargó con éxito", "Error");
        } else {
            MessageUtils.showMessage("La imagen se cargó con éxito", "Imagen cargada");
        }

    }
}
