
module com.example.jfxchat {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.jfxchat.client;
    opens com.example.jfxchat.client to javafx.fxml;
}