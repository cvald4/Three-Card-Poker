import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultController {

    @FXML private Label result;
    @FXML private Label amount;

    public void setResult(String resultText, int amountValue) {
        result.setText(resultText);
        amount.setText("$" + amountValue);
    }

    @FXML
    private void onPlayAgain() {
        try {
            ClientApp.switchScene("game");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExit() {
        try {
            ClientApp.switchScene("welcome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
