import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameController {

    @FXML private BorderPane rootPane;

    @FXML private HBox playerCards;
    @FXML private HBox dealerCards;

    @FXML private Label p1, p2, p3;
    @FXML private Label d1, d2, d3;

    @FXML private TextField playField;
    @FXML private Label winnings;
    @FXML private ListView<String> serverMessages;

    @FXML private Button dealBtn, playBtn, foldBtn, clearBtn;

    @FXML private ToggleButton ante5Btn, ante10Btn, ante20Btn, ante25Btn;
    @FXML private ToggleButton pair0Btn, pair5Btn, pair10Btn, pair15Btn, pair20Btn;

    private ClientNetwork client;
    private int totalWinnings = 0;
    private int currentAnte = 5;
    private int currentPair = 0;

    public void setClient(ClientNetwork client) {
        this.client = client;
    }

    @FXML
    private void initialize() {
        playField.setText("0");
        winnings.setText("$0");

        showBacksideDealer();
        showBacksidePlayer();
        
        playBtn.setDisable(true);
        foldBtn.setDisable(true);
        dealBtn.setDisable(false);
    }


    @FXML private void onAnte5()  { setAnte(5); }
    @FXML private void onAnte10() { setAnte(10); }
    @FXML private void onAnte20() { setAnte(20); }
    @FXML private void onAnte25() { setAnte(25); }

    @FXML private void onPair0()  { setPair(0); }
    @FXML private void onPair5()  { setPair(5); }
    @FXML private void onPair10() { setPair(10); }
    @FXML private void onPair15() { setPair(15); }
    @FXML private void onPair20() { setPair(20); }

    private void setAnte(int value) {
        currentAnte = value;
        serverMessages.getItems().add("Ante set to $" + value);
    }

    private void setPair(int value) {
        currentPair = value;
        serverMessages.getItems().add("Pair Plus set to $" + value);
    }

    @FXML
    private void deal() {
        if (client == null) return;

        PokerInfo info = new PokerInfo();
        info.messageType = "BET";
        info.ante = currentAnte;
        info.pairPlus = currentPair;

        serverMessages.getItems().add("BET");
        client.sendPokerInfo(info);
        
        dealBtn.setDisable(true);
        clearBtn.setDisable(true);
        disableBettingButtons(true);
    }

    @FXML
    private void play() {
        if (client == null) return;

        playField.setText("$" + currentAnte);

        PokerInfo info = new PokerInfo();
        info.messageType = "PLAY";
        info.playWager = currentAnte;
        info.ante = currentAnte;
        info.pairPlus = currentPair;

        serverMessages.getItems().add("PLAY");
        client.sendPokerInfo(info);
        
        resetButtonsForNextRound();
    }

    @FXML
    private void fold() {
        if (client == null) return;

        PokerInfo info = new PokerInfo();
        info.messageType = "FOLD";
        info.ante = currentAnte;
        info.pairPlus = currentPair;

        serverMessages.getItems().add("FOLD");
        client.sendPokerInfo(info);
        
        resetButtonsForNextRound();
    }
    
    private void resetButtonsForNextRound() {
        dealBtn.setDisable(false);
        clearBtn.setDisable(false);
        playBtn.setDisable(true);
        foldBtn.setDisable(true);
        disableBettingButtons(false);
    }
    
    private void disableBettingButtons(boolean disable) {
        List<ToggleButton> allBets = Arrays.asList(
            ante5Btn, ante10Btn, ante20Btn, ante25Btn,
            pair0Btn, pair5Btn, pair10Btn, pair15Btn, pair20Btn
        );
        for(ToggleButton b : allBets) b.setDisable(disable);
    }

    @FXML
    private void clearBets() {
        ante5Btn.setSelected(true);
        pair0Btn.setSelected(true);
        setAnte(5);
        setPair(0);
        serverMessages.getItems().add("Bets reset to default.");
    }

    @FXML
    private void exit() {
        serverMessages.getItems().add("Exiting game");
        if (client != null) client.closeConnection();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void freshStart() {
        totalWinnings = 0;
        winnings.setText("$0");
        
        showBacksideDealer();
        showBacksidePlayer();
        playField.setText("0");
        
        clearBets();
        
        resetButtonsForNextRound();
        
        serverMessages.getItems().clear();
        serverMessages.getItems().add("Fresh start initialized. Balance reset.");
    }

    @FXML
    private void newLook() {
        if (rootPane.getStyleClass().contains("blue-theme")) {
            rootPane.getStyleClass().remove("blue-theme");
            serverMessages.getItems().add("Theme switched to: Classic Green");
        } else {
            rootPane.getStyleClass().add("blue-theme");
            serverMessages.getItems().add("Theme switched to: Ocean Blue");
        }
    }

    public void onPokerInfo(PokerInfo info) {
        if (info == null) return;

        Platform.runLater(() -> {
            switch (info.messageType) {
                case "DEAL":
                    showCards(playerCards, new Label[]{p1,p2,p3}, info.playerCards);
                    showBacksideDealer();
                    playField.setText("0");
                    playBtn.setDisable(false);
                    foldBtn.setDisable(false);
                    serverMessages.getItems().add("Server dealt cards.");
                    break;

                case "RESULT":
                    showCards(playerCards, new Label[]{p1,p2,p3}, info.playerCards);
                    showCards(dealerCards, new Label[]{d1,d2,d3}, info.dealerCards);
                    
                    totalWinnings += info.amountWonOrLost;
                    winnings.setText("$" + totalWinnings);

                    if (info.statusMessage != null) {
                        serverMessages.getItems().add(info.statusMessage);
                    }
                    
                    PauseTransition pauseResult = new PauseTransition(Duration.seconds(4));
                    pauseResult.setOnFinished(e -> {
                        try {
                            ClientApp.switchToResult(info.statusMessage, totalWinnings);
                        } catch(Exception ex) { ex.printStackTrace(); }
                    });
                    pauseResult.play();
                    break;

                case "FOLD":
                    showCards(playerCards, new Label[]{p1,p2,p3}, info.playerCards);
                    totalWinnings += info.amountWonOrLost;
                    winnings.setText("$" + totalWinnings);
                    serverMessages.getItems().add(info.statusMessage);
                    
                    PauseTransition pauseFold = new PauseTransition(Duration.seconds(2));
                    pauseFold.setOnFinished(e -> {
                        try {
                            ClientApp.switchToResult("You Folded.", totalWinnings);
                        } catch(Exception ex) { ex.printStackTrace(); }
                    });
                    pauseFold.play();
                    break;
            }
        });
    }

    private void showBacksideDealer() { d1.setText("🂠"); d2.setText("🂠"); d3.setText("🂠"); }
    private void showBacksidePlayer() { p1.setText("?"); p2.setText("?"); p3.setText("?"); }

    private void showCards(HBox box, Label[] slots, ArrayList<Card> cards) {
        if (cards == null || cards.size() != 3) {
            showBacksidePlayer();
            return;
        }
        for (int i = 0; i < 3; i++) {
            slots[i].setText(cards.get(i).charToString());
        }
    }
}
