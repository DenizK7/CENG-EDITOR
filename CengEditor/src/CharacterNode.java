
public class CharacterNode {

    private char ItemName;   
    private CharacterNode next;
    private CharacterNode previous;
    
	public CharacterNode(char dataToAdd) {
	    ItemName = dataToAdd;
	    next = null;
	}
	   
   public char getItemName() { return ItemName; }
   public void setItemName(char data) { this.ItemName = data;  }
   public CharacterNode getNext() { return next;  }
   public void setNext(CharacterNode next) { this.next = next;   }
   public CharacterNode getPrevious() {return previous;}
   public void setPrevious(CharacterNode previous) {this.previous = previous;}


}
