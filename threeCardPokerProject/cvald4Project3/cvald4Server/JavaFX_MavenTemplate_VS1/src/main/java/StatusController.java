import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.function.Consumer;

public class StatusController {

    @FXML private ListView<String> logList;
    @FXML private Label clientCountLabel;
    @FXML private Label gamesPlayedLabel;
    @FXML private Label lastResultLabel;
    @FXML private Label statusPortLabel;
    @FXML private Button stopBtn;

    private Server mainApp;
    private ServerMess server;

    private int clientCount = 0;
    private int gamesPlayed = 0;

    public void setMainApp(Server mainApp) {
        this.mainApp = mainApp;
    }

    public void setServer(ServerMess server, int port) {
        this.server = server;
        statusPortLabel.setText(String.valueOf(port));

        Consumer<String> guiCallback = msg -> {
            Platform.runLater(() -> {
                if (logList != null) {
                    logList.getItems().add(msg);
                    logList.scrollTo(logList.getItems().size() - 1);
                } else {
                    System.out.println("GUI LOG: " + msg);
                }
            });
        };

        server.setLogCallback(guiCallback);

        server.setClientCountCallback(count -> {
            Platform.runLater(() -> {
                if (clientCountLabel != null) {
                    clientCountLabel.setText(String.valueOf(count));
                }
            });
        });

        server.setGameResultCallback((totalGames, lastResult) -> {
            Platform.runLater(() -> {
                if (gamesPlayedLabel != null) {
                    gamesPlayedLabel.setText(String.valueOf(totalGames));
                }
                if (lastResultLabel != null) {
                    lastResultLabel.setText(lastResult);
                }
            });
        });

        if (stopBtn != null) {
            stopBtn.setDisable(false);
        }
    }

    @FXML
    private void onStopServer() {
        if (server != null) server.stopServer();
        if (stopBtn != null) stopBtn.setDisable(true);
    }

    @FXML
    private void onClearLog() {
        if (logList != null) logList.getItems().clear();
    }

    @FXML
    private void showHomeScene() {
        try {
            mainApp.showHomeScene();
        } catch (Exception ignored) {}
    }
}