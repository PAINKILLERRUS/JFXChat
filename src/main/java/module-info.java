
module com.example.jfxchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    exports com.example.jfxchat.client;
    opens com.example.jfxchat.client to javafx.fxml;
    exports com.example.jfxchat.server;
    opens com.example.jfxchat.server to javafx.fxml;
}