import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private ArrayList<Card> cards = new ArrayList<>();
	
	public Deck() {
		String[] suits = {"♠","♥","♦","♣"};
		for(String s : suits) {
			for(int i = 2; i <= 14; i++) {
				cards.add(new Card(i, s));
			}
		}
	}
	
	public void shuffle() {
		for (int i = cards.size() - 1; i > 0; i--) {
	        int j = (int)(Math.random() * (i + 1));  
	        Card temp = cards.get(i);
	        cards.set(i, cards.get(j));
	        cards.set(j, temp);
	    }
	}
	
	public ArrayList<Card> deal(int num) {
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            hand.add(cards.remove(0));
        }
        return hand;
    }
}