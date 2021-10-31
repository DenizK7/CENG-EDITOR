
public class MultiLinkedList {
	ParagraphNode head;	
	ParagraphNode tail;
	
//	public void addCategory(int dataToAdd) {
//		ParagraphNode temp =  new ParagraphNode(dataToAdd);
//		if (head == null) {
//			head = temp;
//			tail = temp;
//		}
//		else {		     
//			temp.setUp(tail);
//			tail.setDown(temp);
//			tail = temp;
//		}
//	}

	public void addCategory(int dataToAdd) {
		ParagraphNode temp;
        if (head == null) {
            temp = new ParagraphNode(dataToAdd); 
            head = temp;
        }
        else {
            temp = head;
            while (temp.getDown() != null)
                temp = temp.getDown();
            ParagraphNode newnode = new ParagraphNode(dataToAdd);
            temp.setDown(newnode);
        }
    }	
	
	public void deleteCategory(int categoryName) {
		ParagraphNode previous = null;
		if(head == null) {
			System.out.println("Linked List is Empty");
		}
		else if(head.getCategoryName() == categoryName ){
			head = head.getDown();
		}
		else {
			ParagraphNode temp = head;
			while(temp.getDown() != null && temp.getCategoryName() != categoryName) {
				previous = temp;
				temp = temp.getDown();
			}
			if(temp != null) {
				previous.setDown(temp.getDown());
			}
		}
	}
	
	public void display() {
		if (head == null)
            System.out.println("linked list is empty");
        else {
            ParagraphNode temp = head;
            while (temp != null)
            {
                System.out.print(temp.getCategoryName() + " --> ");
                CharacterNode temp2 = temp.getRight();
                while (temp2 != null)
                {
                    System.out.print(temp2.getItemName());
                    temp2 = temp2.getNext();
                }
                temp = temp.getDown();
                System.out.println();
            }
        }
	}
}


