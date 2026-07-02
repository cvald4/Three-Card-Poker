import java.io.Serializable;

public class Card implements Serializable {
	public final int rank;
	public final String suit;
	
	public Card(int rank, String suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	public String charToString() {
		String ch;
		
		if (rank == 11) {
	        ch = "J";
	    } else if (rank == 12) {
	        ch = "Q";
	    } else if (rank == 13) {
	        ch = "K";
	    } else if (rank == 14) {
	        ch = "A";
	    } else {
	        ch = String.valueOf(rank);
	    }

	    return ch + suit;
	}
}