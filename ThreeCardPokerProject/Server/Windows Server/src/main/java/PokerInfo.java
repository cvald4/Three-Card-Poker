import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    public String messageType; 
    public int ante;
    public int pairPlus;
    public int playWager;
    public ArrayList<Card> playerCards;
    public ArrayList<Card> dealerCards;
    public boolean playerWon;
    public boolean dealerQualified;
    public int amountWonOrLost;
    public int clientID;
    public String statusMessage;
}
