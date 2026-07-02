import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class WelcomeController {

    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private ListView<String> serverMessages;

    private ClientNetwork client;

    @FXML
    private void onConnect() {
        String ip = ipField.getText().isEmpty() ? "127.0.0.1" : ipField.getText();

        int port = 5555;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (Exception ignored) {}

        serverMessages.getItems().add("Connecting to " + ip + ":" + port);

        client = new ClientNetwork(ip, port, msg ->
            Platform.runLater(() -> serverMessages.getItems().add(msg))
        );

        client.start();

        try {
            ClientApp.switchToGameWithClient(client);
        } catch (Exception ex) {
            serverMessages.getItems().add("Failed to load" + ex.getMessage());
        }
    }

    @FXML
    private void exitApp() {
        System.exit(0);
    }

    @FXML
    private void freshStart() {
        serverMessages.getItems().add("Fresh start (welcome screen)");
    }

    @FXML
    private void newLook() {
        serverMessages.getItems().add("New look (placeholder)");
    }
}

