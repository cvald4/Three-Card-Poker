import java.util.ArrayList;

public class GameLogic {
	public static int handleHand(ArrayList<Card> hand) {
        sortHand(hand);  

        boolean flush = hand.get(0).suit.equals(hand.get(1).suit) && hand.get(1).suit.equals(hand.get(2).suit);

        boolean straight = isStraight(hand);

        boolean threeKind = (hand.get(0).rank == hand.get(1).rank && hand.get(1).rank == hand.get(2).rank);

        boolean pair = (hand.get(0).rank == hand.get(1).rank) || (hand.get(1).rank == hand.get(2).rank) || (hand.get(0).rank == hand.get(2).rank);

        if (straight && flush) {
        	return 6;
        }
        if (threeKind) {
        	return 5;
        }
        if (straight) {
        	return 4;
        }
        if (flush) {
        	return 3;
        }
        if (pair) {
        	return 2;
        }
        return 1;
    }
	
	private static void sortHand(ArrayList<Card> hand) {
		for (int i = 0; i < hand.size() - 1; i++) {
			for (int j = 0; j < hand.size() - i - 1; j++) {
                if (hand.get(j).rank > hand.get(j + 1).rank) {
                    Card temp = hand.get(j);
                    hand.set(j, hand.get(j + 1));
                    hand.set(j + 1, temp);
                }
            }
        }
	}
	
	private static boolean isStraight(ArrayList<Card> hand) {
        int[] cards = {hand.get(0).rank, hand.get(1).rank, hand.get(2).rank};

        for (int i = 0; i < 2; i++) {
            if (cards[i] > cards[i + 1]) {
                int temp = cards[i];
                cards[i] = cards[i + 1];
                cards[i + 1] = temp;
            }
        }
        
        if (cards[0] > cards[1]) {
            int temp = cards[0];
            cards[0] = cards[1];
            cards[1] = temp;
        }

        if (cards[0] == 2 && cards[1] == 3 && cards[2] == 14) {
        	return true;
        }

        if ((cards[2] - cards[1] == 1) && (cards[1] - cards[0] == 1)) {
            return true;
        } 
        else {
            return false;
        }
    }
	
	public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int dealerHand = handleHand(dealer);
        int playerHand = handleHand(player);

        if (playerHand > dealerHand) {
        	return 1;
        }
        if (playerHand < dealerHand) {
        	return -1;
        }

        sortHand(dealer);
        sortHand(player);

        for (int i = 2; i >= 0; i--) {
            int playerRank = player.get(i).rank;
            int dealerRank = dealer.get(i).rank;
            
            if (playerRank > dealerRank) {
            	return 1;
            }
            if (playerRank < dealerRank) {
            	return -1;
            }
        }

        return 0;
    }
	
	public static boolean dealerQualifies(ArrayList<Card> dealer) {
        int high = dealer.get(0).rank;
        
        for (int i = 1; i < dealer.size(); i++) {
            if (dealer.get(i).rank > high) {
                high = dealer.get(i).rank;
            }
        }
        
        if (high >= 12) {
        	return true;
        }
        else {
        	return false;
        }
    }
	
	public static int calcWinnings(ArrayList<Card> hand, int bet) {
		int handRank = handleHand(hand);

	    if (handRank == 6) {
	        return bet * 40;
	    } 
	    else if (handRank == 5) {
	        return bet * 30;
	    }
	    else if (handRank == 4) {
	        return bet * 6;
	    } 
	    else if (handRank == 3) {
	        return bet * 3;
	    } 
	    else if (handRank == 2) {
	        return bet;
	    } 
	    else {
	        return 0;
	    }
	}

}