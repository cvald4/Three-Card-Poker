import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashMap;

public class ClientApp extends Application {

    private static Stage primaryStage;
    private static HashMap<String, Scene> sceneMap = new HashMap<>();
    private static ClientNetwork clientc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        primaryStage.setOnCloseRequest(e -> {
            if (clientc != null) {
                clientc.closeConnection();
            }
            Platform.exit();
            System.exit(0);
        });

        sceneMap.put("welcome", loadScene("/welcome.fxml"));
        sceneMap.put("game", loadScene("/game.fxml"));
        sceneMap.put("result", loadScene("/result.fxml"));

        stage.setTitle("Three Card Poker - Client");
        stage.setScene(sceneMap.get("welcome"));
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.show();
    }

    private Scene loadScene(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/style.css");
        return scene;
    }

    public static void switchScene(String name) {
        primaryStage.setScene(sceneMap.get(name));
    }


    public static void switchToGameWithClient(ClientNetwork client) throws Exception {
        clientc= client;

        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/game.fxml"));
        Parent root = loader.load();
        GameController gc = loader.getController();

        client.setInfoReceiver(info -> {
            gc.onPokerInfo(info);
        });

        gc.setClient(client);

        Scene gameScene = new Scene(root);
        gameScene.getStylesheets().add("/style.css");

        primaryStage.setScene(gameScene);
        sceneMap.put("game", gameScene);
    }

    public static void switchToResult(String resultText, int amount) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/result.fxml"));
        Parent root = loader.load();
        ResultController rc = loader.getController();
        rc.setResult(resultText, amount);

        Scene resultScene = new Scene(root);
        resultScene.getStylesheets().add("/style.css");

        primaryStage.setScene(resultScene);
        sceneMap.put("result", resultScene);
    }
}