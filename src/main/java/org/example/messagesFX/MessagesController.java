package org.example.messagesFX;

import javafx.beans.property.SimpleObjectProperty;
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
import java.nio.file.Path;
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
    public TableColumn<Message, ImageView> imgCol;
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
    public TableColumn<User, ImageView> avatarCol;
    @FXML
    public TableColumn<User, String> NickCol;
    @FXML
    public ImageView imgMessage; // Imagen opcional que se mostrará para enviar el mensaje

    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    MyImage img = null;
    String imgSelected = null; // Imagen opcional que se seleccionará para enviar el mensaje


    /**
     * Inicializador de la ventana: aquí se mostrará al abrir, nombre de usuario, foto de perfil, mensajes y otros usuarios para enviar mensajes
     */

    @FXML
    public void initialize() {

        // CARGAR TABLA DE MENSAJES Y USUARIOS

        // Configuaración columnas de la tabla mensajes

        messageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        sentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSent()));
        imgCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getImageView()));

        // Configuración del formato de las celdas para las imagenes de los mensajes

        imgCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(30);
                    imageView.setFitHeight(30);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });

        // Configuración columnas de la tabla usuarios

        avatarCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getImageView()));

        // Configuración del formato de las celdas para las imagenes de los usuarios

        avatarCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(30);
                    imageView.setFitHeight(30);
                    imageView.setPreserveRatio(true);
                    setGraphic(imageView);
                }
            }
        });

        NickCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        //

        userLbl.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnShown(e -> {

                            // CARGAR VENTANA Y DATOS DEL USUARIO LOGGEADO

                            //System.out.println("La ventana se ha cargado");
                            userLbl.setText(SharedInfo.currentName);
                            userImg.setImage(base64_img(SharedInfo.currentImage));

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

        // LISTENER AL BOTÓN DELETE: Sólo se activará cuando se haya seleccionado un mensaje

        messageTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteBtn.setDisable(newSelection == null);
        });

        // LISTENER AL BOTÓN SEND MESSAGE: Sólo se activará cuando se haya seleccionado un usuario y se haya escrito un mensaje

        messageTxt.textProperty().addListener((obs, oldText, newText) -> {
            boolean selected = usersTbl.getSelectionModel().getSelectedIndex() >= 0;
            sendBtn.setDisable(newText.isEmpty() || !selected);
        });

    }

    /**
     * Botón para cambiar imagen del usuario
     * @param actionEvent
     * @throws IOException
     */

    public void changeImage(ActionEvent actionEvent) throws IOException {

        String token = SharedInfo.currentToken;
        if (token != null) {

            // Elegir archivo de imagen

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo de imagen");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG Image", Arrays.asList("*.jpg", "*.jpeg")));

            Stage stage = (Stage) changeImgBtn.getScene().getWindow();

            Path file = fileChooser.showOpenDialog(stage).toPath();

            img = new MyImage(file);

            userImg.setPreserveRatio(true);
            userImg.setImage(base64_img(img.getData()));

            // Modificarlo en el servicio

            UpdateUser updUser = new UpdateUser(file);
            updUser.start();

            updUser.setOnSucceeded(event -> {
                UpdateUserResponse resp = updUser.getValue();
                if (resp.isOk()) {
                    MessageUtils.showMessage("Foto de perfil actualizada", "Operación realizada con éxito");
                    img = null;
                } else MessageUtils.showError(resp.getError(), "Error");
            });

        }

    }

    /**
     * Botón para refrescar las ventanas
     * @param actionEvent
     */

    public void refresh(ActionEvent actionEvent) {

        messageTbl.getItems().clear();
        usersTbl.getItems().clear();
        messageTbl.getSelectionModel().clearSelection();
        usersTbl.getSelectionModel().clearSelection();
        messageTxt.setText("");
        imgMessage = null;
        imgSelected = "";

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

    /**
     * Función para pasar una imagen a base64
     * @param img: Imagen de la que se hará conversión
     * @return: String formato base64
     * @throws IOException: Lanza excepción si no consigue leer la imagen o viceversa
     */

    private String img_base64(ImageView img) throws IOException {
        File f = new File(img.getImage().getUrl().substring(6));
        byte[] bytes = Files.readAllBytes(f.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Función para convertir de base64 a imagen
     * @param base64: String con el base64 del que se hará conversión
     * @return: Objeto Imagen
     */

    private Image base64_img(String base64) {
        byte[] bytes = java.util.Base64.getDecoder().decode(base64);
        return new javafx.scene.image.Image(new java.io.ByteArrayInputStream(bytes));
    }

    /**
     * Función para rellenar las tablas
     * @param tabla: tabla que será rellenada
     * @param lista: lista con los datos a rellenar
     * @param <T>: Marcador tipo generico
     */

    private <T> void rellenarTabla(TableView<T> tabla, List<T> lista) {
        tabla.setItems(FXCollections.observableList(lista));
    }

    /**
     * Botón para borrar mensajes
     * @param actionEvent
     */

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

    /**
     * Botón para enviar mensaje
     * @param actionEvent
     * @throws IOException
     */

    public void sendMessage(ActionEvent actionEvent) throws IOException {

        String message = messageTxt.getText();
        String to = usersTbl.getSelectionModel().getSelectedItem().getId();
        Message m = new Message(to, message);

        String imgBase64 = img_base64(imgMessage);
        if(imgSelected != null) m.setImage(imgBase64);
        
        SendMessage sendMess = new SendMessage(m, m.getTo());

        sendMess.start();

        sendMess.setOnSucceeded(event -> {

            SendMessageResponse resp = sendMess.getValue();

            if (!resp.isOk()) MessageUtils.showError("El mensaje no pudo ser enviado", "Error");
            else {
                MessageUtils.showMessage("El mensaje ha sido enviado con éxito", "Mensaje enviado");
                usersTbl.getSelectionModel().clearSelection();
                messageTxt.setText("");
                imgMessage = null;
                imgSelected = "";
            }

        });
    }

    /**
     * Botón para seleccionar la imagen opcional que se enviará con el mensaje
     * @param actionEvent
     * @throws IOException
     */

    public void selectImage(ActionEvent actionEvent) throws IOException {

        // Elegir archivo de imagen

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG Image", Arrays.asList("*.jpg", "*.jpeg")));

        Stage stage = (Stage) imgSelectBtn.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imgMessage.setImage(image);
            imgSelected = img_base64(imgMessage);
        } else MessageUtils.showError("No se seleccionó ninguna imagen", "Error");

        imgSelected = img_base64(imgMessage);

        if (imgSelected.isEmpty()) {
            MessageUtils.showError("La imagen no se cargó con éxito", "Error");
        } else {
            MessageUtils.showMessage("La imagen se cargó con éxito", "Imagen cargada");
        }

    }

}
