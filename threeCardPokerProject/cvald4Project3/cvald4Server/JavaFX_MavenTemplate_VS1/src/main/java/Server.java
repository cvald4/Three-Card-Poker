import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Server extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_intro.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Three Card Poker - Server");
        stage.setScene(scene);
        stage.show();

        IntroController ctrl = loader.getController();
        ctrl.setMainApp(this);
    }

    public void showServerLogsScene(ServerMess server, int port) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_status.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        StatusController ctrl = loader.getController();
        ctrl.setMainApp(this);
        ctrl.setServer(server, port);
    }

    public void showHomeScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_intro.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        IntroController ctrl = loader.getController();
        ctrl.setMainApp(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}