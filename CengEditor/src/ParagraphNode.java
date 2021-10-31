
public class ParagraphNode {
	
	private int CategoryName;
	private ParagraphNode down;
	private ParagraphNode up;
	private CharacterNode right;
	
	public ParagraphNode(int dataToAdd) {
		CategoryName = dataToAdd;
		down = null;
		right = null;
	}
	
	public int getCategoryName() { return CategoryName; }
	public void setCategoryName(int data) { this.CategoryName = data;  }
	public ParagraphNode getDown() { return down;  }
	public void setDown(ParagraphNode down) { this.down = down;   }
	public ParagraphNode getUp() {return up;}
	public void setUp(ParagraphNode up) {this.up = up;}
	public CharacterNode getRight() { return right;  }
	public void setRight(CharacterNode right) { this.right = right;   }


}
