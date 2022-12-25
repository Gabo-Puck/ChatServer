module com.azureproject.chatserver {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.azureproject.chatserver to javafx.fxml;

    exports com.azureproject.chatserver;
    exports com.azureproject.SharedInterfaces;
    exports com.azureproject.client;
    exports com.azureproject.SharedEnum;

}
