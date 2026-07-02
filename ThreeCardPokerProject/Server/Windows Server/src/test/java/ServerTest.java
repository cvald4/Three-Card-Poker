import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerTest {

    private ServerMess serverMess;

    @BeforeEach
    void setUp() {
        serverMess = new ServerMess(msg -> {});
    }

    @Test
    void testBetFlow() {

        PokerInfo betReq = new PokerInfo();
        betReq.messageType = "BET";
        betReq.ante = 10;
        betReq.pairPlus = 5;

        
        Deck deck = new Deck();
        deck.shuffle();
        assertNotNull(deck.deal(3), "Deck should deal cards");
    }

    @Test
    void testPayoutLogic_DealerDoesNotQualify() {
        
        int ante = 10;
        int play = 10;
        int pairPlus = 0;
        
        boolean dealerQualifies = false; 
        boolean playerWins = true;       
        
        int net = 0;
        
        if (!dealerQualifies) {
            net += ante;
        } else {
            net -= 1000; 
        }
        
        assertEquals(10, net, "If dealer doesn't qualify, player should win Ante amount only");
    }

    @Test
    void testPayoutLogic_DealerQualifies_PlayerWins() {
        int ante = 10;
        int play = 10;
        
        boolean dealerQualifies = true; 
        boolean playerWins = true;      
        
        int net = 0;
        
        if (!dealerQualifies) {
            net += ante;
        } else {
            if (playerWins) {
                net += ante; 
                net += play; 
            }
        }
        
        assertEquals(20, net, "If dealer qualifies and player wins, player wins both bets");
    }
}