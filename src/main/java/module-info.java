module com.azureproject.chatserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.azureproject.chatserver to javafx.fxml;
    exports com.azureproject.chatserver;
}
