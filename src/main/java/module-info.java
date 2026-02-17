module org.example.messagesfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.graphics;


    opens org.example.messagesFX to javafx.fxml;
    opens org.example.messagesFX.model to com.google.gson;
    exports org.example.messagesFX;
}