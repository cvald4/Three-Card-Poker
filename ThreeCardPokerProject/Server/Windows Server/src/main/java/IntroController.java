import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class IntroController {

    @FXML private TextField portField;
    @FXML private TextField ipField;
    @FXML private Button startBtn;

    private Server mainApp;
    private ServerMess server;

    public void setMainApp(Server mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void onStartServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());

            server = new ServerMess(msg -> {
                System.out.println(msg); 
            });

            server.startServer(port);
            startBtn.setDisable(true);

        } catch (Exception e) {
            e.printStackTrace(); 
            portField.setText("INVALID");
        }
    }

    @FXML
    private void showServerLogsScene() {
        try {
            int port = 5555;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException nfe) {
                portField.setText("INVALID PORT");
                return;
            }

            if (server == null) {
                portField.setText("START SERVER FIRST");
                return;
            }

            mainApp.showServerLogsScene(server, port);

        } catch (Exception e) {
            System.err.println("Error switching scene:");
            e.printStackTrace(); 
            portField.setText("SCENE ERROR");
        }
    }
}