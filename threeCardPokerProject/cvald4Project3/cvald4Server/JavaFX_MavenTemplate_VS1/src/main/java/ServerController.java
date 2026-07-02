import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class ServerController {

    @FXML private TextField portField;
    @FXML private TextField ipField;
    @FXML private Button startBtn;

    @FXML private ListView<String> logList;
    @FXML private Label clientCountLabel;
    @FXML private Label gamesPlayedLabel;
    @FXML private Label lastResultLabel;
    @FXML private Label statusPortLabel;
    @FXML private Button stopBtn;

    private Stage primaryStage;

    private ServerMess server;

    private int clientCount = 0;
    private int gamesPlayed = 0;

    public ServerController() {}

    @FXML
    private void initialize() {
        if (portField != null) portField.setText("5555");
        if (ipField != null) ipField.setText("0.0.0.0");
    }

    @FXML
    private void onStartServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            return;
        }

        Consumer<String> uiCallback = (msg) -> Platform.runLater(() -> handleServerMessage(msg));

        server = new ServerMess(uiCallback);
        server.startServer(port);

        if (startBtn != null) startBtn.setDisable(true);
        if (stopBtn != null) stopBtn.setDisable(false);
        if (statusPortLabel != null) statusPortLabel.setText(String.valueOf(port));

        showStatusScene();
    }

    @FXML
    private void onStopServer() {
        if (server != null) {
            server.stopServer();
            server = null;
            if (startBtn != null) startBtn.setDisable(false);
            if (stopBtn != null) stopBtn.setDisable(true);
            if (statusPortLabel != null) statusPortLabel.setText("(not running)");
            if (logList != null) logList.getItems().add("Server stopped.");
        }
    }

    @FXML
    private void onClearLog() {
        if (logList != null) logList.getItems().clear();
    }

    @FXML
    private void showStatusScene() {
        try {
            Stage st = getStage();
            if (st == null) return;
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server_status.fxml"));
            Scene scene = new Scene(loader.load());
            st.setScene(scene);
            
            bindStatusNodes(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showIntroScene() {
        try {
            Stage st = getStage();
            if (st == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server_intro.fxml"));
            Scene scene = new Scene(loader.load());
            st.setScene(scene);
            bindIntroNodes(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String msg) {
        if (logList != null) logList.getItems().add(msg);

        if (msg.startsWith("Client connected")) {
            clientCount++;
            if (clientCountLabel != null) clientCountLabel.setText(String.valueOf(clientCount));
        } 
        else if (msg.startsWith("Client disconnected") || msg.startsWith("Client removed")) {
            if (clientCount > 0) clientCount--;
            if (clientCountLabel != null) clientCountLabel.setText(String.valueOf(clientCount));
        } 
        else if (msg.startsWith("DEAL") || msg.contains("Cards dealt") || msg.startsWith("Player BET")) {
            gamesPlayed++;
            if (gamesPlayedLabel != null) gamesPlayedLabel.setText(String.valueOf(gamesPlayed));
        } 
        else if (msg.startsWith("RESULT") || msg.contains("Win") || msg.contains("Lost")) {
            if (lastResultLabel != null) lastResultLabel.setText(msg);
        }
    }

    private Stage getStage() {
        if (primaryStage != null) return primaryStage;
        if (portField != null && portField.getScene() != null) {
            return (Stage) portField.getScene().getWindow();
        }
        if (logList != null && logList.getScene() != null) {
            return (Stage) logList.getScene().getWindow();
        }
        return null;
    }

    private void bindStatusNodes(Scene scene) {
        logList = (ListView<String>) scene.lookup("#logList");
        clientCountLabel = (Label) scene.lookup("#clientCountLabel");
        gamesPlayedLabel = (Label) scene.lookup("#gamesPlayedLabel");
        lastResultLabel = (Label) scene.lookup("#lastResultLabel");
        statusPortLabel = (Label) scene.lookup("#statusPortLabel");
        stopBtn = (Button) scene.lookup("#stopBtn");
        
        if (clientCountLabel != null) clientCountLabel.setText(String.valueOf(clientCount));
        if (gamesPlayedLabel != null) gamesPlayedLabel.setText(String.valueOf(gamesPlayed));
    }

    private void bindIntroNodes(Scene scene) {
        portField = (TextField) scene.lookup("#portField");
        ipField = (TextField) scene.lookup("#ipField");
        startBtn = (Button) scene.lookup("#startBtn");
    }
}