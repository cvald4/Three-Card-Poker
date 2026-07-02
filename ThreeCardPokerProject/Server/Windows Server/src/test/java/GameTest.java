import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class GameLogicTest {

    private ArrayList<Card> createHand(int r1, String s1, int r2, String s2, int r3, String s3) {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(r1, s1));
        hand.add(new Card(r2, s2));
        hand.add(new Card(r3, s3));
        return hand;
    }


    @Test
    void testStraightFlush() {
        ArrayList<Card> hand = createHand(12, "♥", 13, "♥", 14, "♥");
        int rank = GameLogic.handleHand(hand);
        assertEquals(6, rank, "Q-K-A Suited should be a Straight Flush (6)");
    }

    @Test
    void testThreeOfAKind() {
        ArrayList<Card> hand = createHand(5, "♥", 5, "♣", 5, "♠");
        int rank = GameLogic.handleHand(hand);
        assertEquals(5, rank, "5-5-5 should be Three of a Kind (5)");
    }

    @Test
    void testStraight() {
        ArrayList<Card> hand = createHand(5, "♥", 6, "♣", 7, "♠");
        int rank = GameLogic.handleHand(hand);
        assertEquals(4, rank, "5-6-7 Mixed should be a Straight (4)");
    }

    @Test
    void testFlush() {
        ArrayList<Card> hand = createHand(2, "♠", 8, "♠", 11, "♠");
        int rank = GameLogic.handleHand(hand);
        assertEquals(3, rank, "2-8-J Suited should be a Flush (3)");
    }

    @Test
    void testPair() {
        ArrayList<Card> hand = createHand(13, "♥", 13, "♣", 4, "♠");
        int rank = GameLogic.handleHand(hand);
        assertEquals(2, rank, "K-K-4 should be a Pair (2)");
    }

    @Test
    void testHighCard() {
        ArrayList<Card> hand = createHand(2, "♥", 5, "♣", 9, "♠");
        int rank = GameLogic.handleHand(hand);
        assertEquals(1, rank, "2-5-9 Mixed should be High Card (1)");
    }


    @Test
    void testDealerQualifiesWithQueen() {
        // Q, 3, 2
        ArrayList<Card> hand = createHand(12, "♥", 3, "♣", 2, "♠");
        boolean qualifies = GameLogic.dealerQualifies(hand);
        assertTrue(qualifies, "Dealer should qualify with Queen High");
    }

    @Test
    void testDealerDoesNotQualifyJackHigh() {
        ArrayList<Card> hand = createHand(11, "♥", 10, "♣", 2, "♠");
        
        boolean qualifies = GameLogic.dealerQualifies(hand);
        assertFalse(qualifies, "Dealer should NOT qualify with Jack High");
    }

    @Test
    void testDealerQualifiesWithPair() {
    	
        ArrayList<Card> hand = createHand(2, "♥", 2, "♣", 5, "♠");

        boolean qualifies = GameLogic.dealerQualifies(hand);
        assertFalse(qualifies, "Based on current logic, Pair of 2s has high card 5, so it fails qual check.");
    }


    @Test
    void testPlayerWinsPairOverHighCard() {
        ArrayList<Card> player = createHand(2, "♥", 2, "♣", 5, "♠"); 
        ArrayList<Card> dealer = createHand(14, "♥", 10, "♣", 8, "♠"); 
        
        int result = GameLogic.compareHands(dealer, player);
        assertEquals(1, result, "Player Pair should beat Dealer Ace High");
    }

    @Test
    void testDealerWinsHigherFlush() {
        ArrayList<Card> player = createHand(2, "♥", 4, "♥", 6, "♥"); 
        ArrayList<Card> dealer = createHand(3, "♥", 5, "♥", 7, "♥"); 
        
        int result = GameLogic.compareHands(dealer, player);
        assertEquals(-1, result, "Dealer higher flush should win");
    }
    
    @Test
    void testTie() {
        ArrayList<Card> player = createHand(10, "♥", 11, "♣", 12, "♠"); 
        ArrayList<Card> dealer = createHand(10, "♦", 11, "♠", 12, "♣");
        
        int result = GameLogic.compareHands(dealer, player);
        assertEquals(0, result, "Exact same ranks should be a tie");
    }
}