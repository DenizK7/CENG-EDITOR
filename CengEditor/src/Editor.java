
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import enigma.console.TextAttributes;
import enigma.core.Enigma;

public class Editor {
	public enigma.console.Console cn = Enigma.getConsole("CENG Editor", 120, 30, 30, 1);
	public KeyListener klis;
	public int keypr; // key pressed?
	public int rkey; // key (for press/release)
	public int space = 0;
	public enigma.console.TextWindow cnt = cn.getTextWindow();;
	public int rkeymod;
	public MultiLinkedList mainMLL = new MultiLinkedList();
	CharacterNode cursor = null;
	public int paragraph = 1;
	static int px = 6, py = 3;
	public int enter_py = 3;
	boolean first = true;
	boolean allignleft = false;
	int temp_px = px, temp_py = py;
	public boolean overwrite = false;
	String mode = "Insert";
	public CharacterNode startSelection = null;
	public CharacterNode endSelection = null;
	public int startTempPx, endTempPx;
	public int startTempPy, endTempPy;
	public char[] copy = new char[100];
	public int selectionCounter = 0;
	int cursorCheck = 0;
	boolean find = false;
	public char[] findar = new char[100];
	int find_counter = 0;
	int enter_counter_find = 0;
	CharacterNode search_start = null; 
	public int selected = 0;
	public int cutSelected = 0;
	int howManySelection = 0;
	int counter_for_replace = 0;
	int preParagraph_px;
	int preParagraph_py;
	boolean justified = false;
	CharacterNode replacestart = null;
	CharacterNode replaceend = null;
	boolean replace = false;
	int print_replace = 0;
	CharacterNode temp_replace = null;
	CharacterNode start = null;
	CharacterNode start2 = null;
	public static TextAttributes blue = new TextAttributes(Color.white, Color.blue);
	public static TextAttributes black = new TextAttributes(Color.white, Color.black);
	
	Editor() throws Exception {
		mainMLL.addCategory(paragraph);
		addLetter('*');
				
		ParagraphNode temp = mainMLL.head;
		cursor = temp.getRight();
		
		printExtra(); //for creating screen

		klis = new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (keypr == 0) {
					keypr = 1;
					rkeymod = e.getModifiersEx();
					rkey = e.getKeyCode();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		};
		cn.getTextWindow().addKeyListener(klis);
		
		while (true) {
			
			char rckey = (char) rkey;		
			
			if (keypr == 1) { // if keyboard button pressed
				clearParagraph(64, 21);
							
				if (rkey == KeyEvent.VK_LEFT) { 
					leftArrow();
				}
				if (rkey == KeyEvent.VK_RIGHT) { 
					rightArrow();
				}
				if (rkey == KeyEvent.VK_UP && py > 3) { 
					if (upArrow()) {
						py--;
					}
				}
				if (rkey == KeyEvent.VK_DOWN && py < 21) { 
					if (downArrow()) {
						py++;
					}
				}
				if(replace) {
					cursorCheck =0;

					if(rckey == KeyEvent.VK_ESCAPE)
						replace= false;
					else if(rckey == KeyEvent.VK_ENTER) {
						replace= false;
						replacestart.getPrevious().setNext(start);
						start.setPrevious(replacestart.getPrevious());
						replaceend.getNext().setPrevious(temp_replace);
						temp_replace.setNext(replaceend.getNext());
						cursor = temp_replace;
					}
					else if(((rkey >= 65 && rkey <= 90) || (rkey >= 44 && rkey <= 57))) {
						if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
							if(first) {
								CharacterNode a = new CharacterNode(rckey);
								start = a;
								 temp_replace = start;
								 first = false;
							}
							else if(!first) {
								 start2 = new CharacterNode(rckey); ;
								temp_replace.setNext(start2);
								start2.setPrevious(temp_replace);
								temp_replace = start2;
							}
							cn.getTextWindow().setCursorPosition(80+ print_replace, 22);
							System.out.print(rckey);
						} 
						else {	
							if(first) {
								CharacterNode a = new CharacterNode(Character.toLowerCase(rckey));
								start = a;
								 temp_replace = start;
								 first = false;
							}
							else if(!first) {
								 start2 = new CharacterNode(Character.toLowerCase(rckey)); ;
								temp_replace.setNext(start2);
								start2.setPrevious(temp_replace);
								temp_replace = start2;
							}
							cn.getTextWindow().setCursorPosition(80+ print_replace, 22);
							System.out.print(Character.toLowerCase(rckey));

						}
						print_replace++;
					}
					
					if(!replace) {

						printExtra();
					}
				

				}
				else if (find) {
					if(((rkey >= 65 && rkey <= 90) || (rkey >= 44 && rkey <= 57))) {
						search_start = mainMLL.head.getRight().getNext();
						if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
							findar[find_counter] = rckey;
						} 
						else {
							findar[find_counter] = Character.toLowerCase(rckey);						
						}
						
						cn.getTextWindow().setCursorPosition(78+ find_counter, 22);	
						System.out.print(findar[find_counter]);
						find_counter++;
					}
					if(rkey == KeyEvent.VK_F8) {
						find();
						enter_counter_find++;
					}
					if(rckey == KeyEvent.VK_ESCAPE)
						find= false;
					if(!find) {
						cn.getTextWindow().setCursorPosition(70, 22);
						System.out.print("                                                        ");
						cn.getTextWindow().setCursorPosition(70, 20);
						System.out.print("                                                        ");
						printExtra();
					}

				}
				else if (rkey == KeyEvent.VK_BACK_SPACE) {									
					backspace();					
				}
				else if (rkey == KeyEvent.VK_F1) {
					startSelection = cursor;
					startTempPx = px;
					startTempPy = py;				
				}
				else if (rkey == KeyEvent.VK_F2) {
					endSelection = cursor;
					endTempPx = px;
					endTempPy = py;
					if (startSelection != null) {
						selection();
						howManySelection++;
					}			
				}
				else if (rkey == KeyEvent.VK_F3) {
					cut();
				}
				else if (rkey == KeyEvent.VK_F5) {
					paste();
				}
				else if (rkey == KeyEvent.VK_F12) {
					save();
				}
				else if (rkey == KeyEvent.VK_F10) {
					justified = true;
					cn.getTextWindow().setCursorPosition(81, 18);
					System.out.print("Justified           ");
				}
				
				else if (rkey == KeyEvent.VK_F11) {
					mainMLL = new MultiLinkedList();
					load();			
				}
				else if (((rkey >= 65 && rkey <= 90) || (rkey >= 44 && rkey <= 57))) {// ASCII DE A-Z				
					if ((rkeymod & KeyEvent.SHIFT_DOWN_MASK) > 0) {						
						if (rckey == '.' || rckey == ',') {
							if (rckey == '.') {
								addLetter(':');
							}
							if (rckey == ',') {
								addLetter(';');
							}
						}
						else {
                            addLetter(rckey);
                        }
					} 
					else {						
						if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
							addLetter(rckey);
						} 
						else {
							addLetter(Character.toLowerCase(rckey));						
						}
					}
					plusAdder();
				} 
				else if (rkey == KeyEvent.VK_DELETE) {
                   delete();                  
                }
				else if (rkey == KeyEvent.VK_SPACE && px < 65) {
					addLetter(' ');
				}
				else if (rkey == KeyEvent.VK_ENTER && py < 21) { 
					while (px != 65) {
						addLetter('+');
						px++;
					}
					printParagraph(6, 3);
					preParagraph_px = px;
					preParagraph_py = py;
					py++;
					px = 5;
					paragraph++;
					mainMLL.addCategory(paragraph);
					addLetter('*');
				}
				else if (rkey == KeyEvent.VK_HOME) {
					home();
				}
				else if (rkey == KeyEvent.VK_END) {
					end();
				}
				else if (rkey == KeyEvent.VK_INSERT) { 
					if (overwrite) {
						overwrite = false;
						mode = "Insert";
					}
					else {
						overwrite = true;
						mode = "Overwrite";
					}
					printExtra();
				}
				else if (rkey == KeyEvent.VK_F6) {
					if(find) find = false;
					else find = true;
					printExtra();
					enter_counter_find = 0;
				}
				else if (rkey == KeyEvent.VK_F7) {
					if(replace) replace = false;
					else replace = true;
					printExtra();
				}
				else if (rkey == KeyEvent.VK_F9) { 
					cn.getTextWindow().setCursorPosition(81, 18);
					System.out.print("Align Left           ");
					justified = false;
					allignleft = true;
					allignLeft();
					allignleft = false;
					allignLeftPlus();
					cursorCheck =0;
				}
				plusCheckForWord();	
				if (overwrite && (rkey != KeyEvent.VK_LEFT && rkey != KeyEvent.VK_RIGHT && rkey != KeyEvent.VK_UP && rkey != KeyEvent.VK_DOWN &&
						  rkey != KeyEvent.VK_BACK_SPACE && rkey != KeyEvent.VK_INSERT && rkey != KeyEvent.VK_CAPS_LOCK)) {	
					insert_overwrite();
				}																
				updateCursor(1, 1);
				if(justified) {
					justified();
				}
				printParagraph(6, py);
				cn.getTextWindow().setCursorPosition(10, 24);
				System.out.print("\t\t\t\t\t\t");
				cn.getTextWindow().setCursorPosition(10, 24);
				System.out.print("Cursor position " + (px-6) + " : " +(py-3));
				
				cn.getTextWindow().setCursorPosition(10, 26);
				System.out.print("Cursor is " + cursor.getItemName());

				keypr = 0; // last action
			}
			
			cn.getTextWindow().setCursorPosition(px, py);			
			cnt.setCursorType(keypr);
			
			Thread.sleep(20);
		}
	}

	public void printExtra() {
		for (int i = 0; i < 61; i++) {
			if (i % 5 == 0) {
				cn.getTextWindow().setCursorPosition(i + 5, 2);
				System.out.print("+");
				cn.getTextWindow().setCursorPosition(i + 5, 22);
				System.out.print("+");
			} else {
				cn.getTextWindow().setCursorPosition(i + 5, 2);
				System.out.print("-");
				cn.getTextWindow().setCursorPosition(i + 5, 22);
				System.out.print("-");
			}
		}
		System.out.println();
		for (int i = 1; i < 21; i++) {
			if (i % 5 == 0) {
				cn.getTextWindow().setCursorPosition(5, i + 2);
				System.out.print("+");
				cn.getTextWindow().setCursorPosition(65, i + 2);
				System.out.print("+");
			} else {
				cn.getTextWindow().setCursorPosition(5, i + 2);
				System.out.print("|");
				cn.getTextWindow().setCursorPosition(65, i + 2);
				System.out.print("|");
			}
		}
		
		cn.getTextWindow().setCursorPosition(70, 3);
		System.out.print("F1: Selection start");
		cn.getTextWindow().setCursorPosition(70, 4);
		System.out.print("F2: Selection end");
		cn.getTextWindow().setCursorPosition(70, 5);
		System.out.print("F3: Cut");
		cn.getTextWindow().setCursorPosition(70, 6);
		System.out.print("F4: Copy");
		cn.getTextWindow().setCursorPosition(70, 7);
		System.out.print("F5: Paste");
		cn.getTextWindow().setCursorPosition(70, 8);
		System.out.print("F6: Find");
		cn.getTextWindow().setCursorPosition(70, 9);
		System.out.print("F7: Replace");
		cn.getTextWindow().setCursorPosition(70, 10);
		System.out.print("F8: Next");
		cn.getTextWindow().setCursorPosition(70, 11);
		System.out.print("F9: Align left");
		cn.getTextWindow().setCursorPosition(70, 12);
		System.out.print("F10: Justify");
		cn.getTextWindow().setCursorPosition(70, 13);
		System.out.print("F11: Load");
		cn.getTextWindow().setCursorPosition(70, 14);
		System.out.print("F12: Save");
		cn.getTextWindow().setCursorPosition(70, 16);
		System.out.print("Mode: ");
		cn.getTextWindow().setCursorPosition(76, 16);
		System.out.print("                          ");
		cn.getTextWindow().setCursorPosition(76, 16);
		System.out.print(mode);
		cn.getTextWindow().setCursorPosition(70, 18);
		System.out.print("Alignment: Align Left");
		if(find) {
			cn.getTextWindow().setCursorPosition(70, 20);
			System.out.print("Press ESCAPE(ESC) to exit find mode");
			cn.getTextWindow().setCursorPosition(70, 22);
			System.out.print("SEARCH : ");
		}
		else if(replace) {
			cn.getTextWindow().setCursorPosition(70, 20);
			System.out.print("Press ESCAPE(ESC) to exit find mode");
			cn.getTextWindow().setCursorPosition(70, 22);
			System.out.print("Replace : ");
		}
		else if(!replace) {
			cn.getTextWindow().setCursorPosition(70, 20);
			System.out.print("\t\t\t\t\t\t\t\t\t");
			cn.getTextWindow().setCursorPosition(70, 22);
			System.out.print("\t\t\t\t\t\t\t");
		}
		
		
	}

	public void addLetter(char item) {
		if (mainMLL.head == null)    
			System.out.println("Add a Category before Item");
		else {
			ParagraphNode temp = mainMLL.head;
			
			while (temp != null) {	    	 
				if (paragraph == temp.getCategoryName()) {
					if (temp.getRight() == null) {
						CharacterNode temp2 = temp.getRight();
						temp2 = new CharacterNode(item); 
						temp.setRight(temp2);
						cursor = temp2;
					}
					else {
						cursorCheck = 1;
						CharacterNode temp2 = cursor;
						CharacterNode newnode = new CharacterNode(item);
						
						if (temp2.getNext() != null) {
							CharacterNode temp2_next = temp2.getNext();
							
							temp2.setNext(newnode);
							temp2.getNext().setPrevious(temp2);						
							
							newnode.setNext(temp2_next);
							newnode.getNext().setPrevious(newnode);							
							cursor = newnode;
							
						}
						else {
							temp2.setNext(newnode);
							temp2.getNext().setPrevious(temp2);
							cursor = cursor.getNext();							
							
						}
					}
				}

				temp = temp.getDown();
			}
			
			plusRemover();
		}
	}

	public void updateCursor(int howMuchAdd, int howMuchDelete) {
		if(cursorCheck == 1) {
			for (int i = 0; i < howMuchAdd; i++) {
				 if (px < 64 ) {
	                 px++;
	             } 
	             else if (px == 64 && py < 22) {
	                 px = 6;
	                 py++;
	             }
			}
		}
		else if(cursorCheck == 2) {
			for (int i = 0; i < howMuchDelete; i++) {
				if (px > 6) {
	                px--;
	            } 
	            else if (px == 6 && py > 3) {
	                px = 64;
	                py--;
	            }
			}		    
		}	
		cursorCheck = 0;
	}
	
	public void printParagraph(int pxx, int pyy) {
		pyy = 3;
		ParagraphNode temp = mainMLL.head;

		while (temp != null) {
			CharacterNode tempItem = temp.getRight();
			
			int counter = -1;
			boolean paintingStart = false;
			while (tempItem.getNext() != null) {
				cn.getTextWindow().setCursorPosition(pxx, pyy); 
				
				if (startSelection != null && endSelection != null) {
					if ((selected == 1 && tempItem == startSelection.getPrevious()) || (selected == 2 && tempItem == endSelection.getPrevious())) {
						cn.setTextAttributes(blue);
						paintingStart = true;
					}
					
					if (paintingStart) {
						counter++;
					}
					if (counter == selectionCounter) {
						cn.setTextAttributes(black);
						selected = 0;
					}
				}
				if(tempItem.getNext().getItemName()!= '+'&&tempItem.getNext().getItemName()!= '-')
				System.out.print(tempItem.getNext().getItemName());
				else if(tempItem.getNext().getItemName()== '+'||tempItem.getNext().getItemName()== '-')
					System.out.print(' ');
				if (pxx < 64) {
					pxx++;
				}
				else if (pxx == 64) {
					pxx = 6;
					pyy++;
				}

				tempItem = tempItem.getNext();
			}
			cn.setTextAttributes(black);
			selected = 0;
			
			if (temp.getDown() != null) {
				temp = temp.getDown();
			}
			else {
				break;
			}
		}
	}

	public void clearParagraph(int pxx, int pyy) { 
		while (pyy >= 3) {
			if (pxx > 6) {
				pxx--;
			}
			else if (pxx == 6) {
				pxx = 64;
				pyy--;
			}
			cn.getTextWindow().setCursorPosition(pxx, pyy);
			System.out.print(" ");
		}
	}
	
	public void home() {
		CharacterNode temp = cursor;		
		while (px != 6) {
			temp = temp.getPrevious();
			px--;
		}	
		cursor = temp;
	}
	
	public void end() {
		CharacterNode temp = cursor;
		
		while (temp.getNext() != null && px < 64) {
			temp = temp.getNext();
			px++;
		}
		
		cursor = temp;
	}

	public CharacterNode arrowElement(String arrow) {
		if (mainMLL.head == null) {
            System.out.println("linked list is empty");
			return null;
		}
        else {
            ParagraphNode temp = mainMLL.head;
            CharacterNode temp2 = null;
            while (temp != null)
            {
                temp2 = temp.getRight();
                
                if (arrow == "right") {
                	if (temp.getCategoryName() == paragraph + 1) {
                    	return temp2;
    				}
				}
                else if (arrow == "left") {
                	while (temp2.getNext() != null)
                    {
                		temp2 = temp2.getNext();
                    }
                	 
                	if (temp.getCategoryName() == paragraph - 1) {
                    	return temp2;
    				}
				}
                
                temp = temp.getDown();
                if (temp == null) {
					return null;
				}
            }
            
            return temp2;
        }
	}
	
	public CharacterNode arrowElement2(String arrow) {
		if (mainMLL.head == null) {
            System.out.println("linked list is empty");
			return null;
		}
        else {
            ParagraphNode temp = mainMLL.head;
            CharacterNode temp2 = null;
            while (temp != null)
            {
                temp2 = temp.getRight();
                
                if (arrow == "right") {
                	if (temp.getCategoryName() == paragraph + 1) {
                    	return temp2;
    				}
				}
                
                while (temp2.getNext() != null)
                {
                	if (temp2.getNext().getItemName() != '+') {
                		temp2 = temp2.getNext();
					}
                	else {
						break;
					}
                }
                
                if (arrow == "left") {
                	if (temp.getCategoryName() == paragraph - 1) {
                    	return temp2;
    				}
				}
                
                temp = temp.getDown();
            }
            
            return temp2;
        }
	}
	
	public CharacterNode lineLastElement() {
		CharacterNode temp = cursor;
		while (temp.getNext() != null && temp.getNext().getItemName() != '+') {
			temp = temp.getNext();
		}
		
		if (temp.getNext() == null) {
			return null;
		}
		
		return temp;
	}
	
	public int plusCounter(CharacterNode x) {
		int counter = 0;

        while (x.getNext() != null && x.getNext().getItemName() == '+') {
        	counter++;
            x = x.getNext();
        }
		
		return counter;
	}
	
	public int letterCounter(CharacterNode x) {
		int counter = 0;
		
		while (x.getNext() != null && x.getNext().getItemName() != '+') {
			counter++;
			x = x.getNext();
		}
		
		return counter;
	}
	
	public void plusRemover() {
		CharacterNode temp = lineLastElement();
		
		if (temp != null && temp.getNext() != null && temp.getNext().getNext() != null) {
			temp.getNext().getNext().setPrevious(temp);
			temp.setNext(temp.getNext().getNext());
		}
		else if (temp != null && temp.getNext() != null) {
			temp.setNext(null);
		}
	}
	
	public void leftArrow() {
        if (cursor != null) {
            if (cursor.getPrevious() != null) {
            	cursor = cursor.getPrevious();
            	cursorCheck = 2;
            }
            else if (paragraph > 1) {
				CharacterNode temp = arrowElement2("left");
				cursor = temp;
				cursorCheck = 2;
				paragraph--;
				int plusCounter = plusCounter(temp);
				updateCursor(0, plusCounter);
			}
        }
    }
	
	public void rightArrow() {
        if (cursor != null) {
            if (cursor.getNext() != null && cursor.getNext().getItemName() != '+') {            	
            	cursor = cursor.getNext();
                cursorCheck = 1;                         
            }
            else if (cursor.getNext() != null) {
				CharacterNode temp = arrowElement("right");
				CharacterNode temp2 = lineLastElement();
				cursor = temp;
				cursorCheck = 1;
				paragraph++;
				int plusCounter = plusCounter(temp2);
				updateCursor(plusCounter, 0);
			}
        }
    }
	
	public boolean upArrow() { 
		CharacterNode temp = cursor;
		int counter = 0;
		if (temp != null) {
			for (int i = 0; i < 59; i++) {
				if (temp.getPrevious() != null) {
					temp = temp.getPrevious();
					counter++;
				}
				else {
					break;
				}
			}
			
			if (counter == 59) {
				cursor = temp;
				return true;
			}
			
			temp = arrowElement("left");
			if (counter < 59 && paragraph > 1) {
				paragraph--;
				for (int i = 0; i < 59 - counter; i++) {
					if (temp != null && temp.getPrevious() != null) {
						temp = temp.getPrevious();
					}
				}
				cursor = temp;
				if (temp.getPrevious() != null && temp.getPrevious().getItemName() == '+') {
					int counter2 = 0;
					while (temp.getPrevious().getItemName() == '+') {
						counter2++;
						temp = temp.getPrevious();
					}
					cursor = temp.getPrevious();
					cursorCheck = 2;
					updateCursor(0, counter2 + 1);
				}
				return true;
			}			
		}
		return false;
	}
	
	public boolean downArrow() { 
		CharacterNode temp = cursor;
		int counter = 0;
		if (temp != null) {
			for (int i = 0; i < 59; i++) {
				if (temp.getNext() != null) {
					temp = temp.getNext();
					counter++;
				}
				else {
					break;
				}
			}
			
			if (counter == 59) {
				cursor = temp;
				return true;
			}
			
			temp = arrowElement("right");
			if (counter < 59 && temp != null) {
				paragraph++;
				int counter2 = 0;
				for (int i = 0; i < 59 - counter; i++) {
					if (temp.getNext() != null) {
						temp = temp.getNext();
						counter2++;
					}
				}
				
				cursor = temp;				
					
				if (59 - counter > counter2) {
					cursorCheck = 2;
					updateCursor(0, 59 - counter - counter2);
				}
				
				return true;
			}
		}	
		return false;
	}
	
	public void save() throws IOException {
		FileWriter file = new FileWriter("CengEditor.txt");
		
		if (mainMLL.head == null)    
			System.out.println("linked list is empty");
		else {
			ParagraphNode temp = mainMLL.head;
			while (temp != null)
			{
				
				CharacterNode temp2 = temp.getRight();
				while (temp2 != null)
				{
					file.write(temp2.getItemName());
					temp2 = temp2.getNext();
				}
				temp = temp.getDown();
				file.write("\n");
			}
		}
		
		file.close();
	}

	public void load() throws IOException {
		px = 6; py = 3;
        clearParagraph(64, 21);                     
        File f = new File("CengEditor.txt");
        if (f.exists()) {
            FileReader file = new FileReader("CengEditor.txt");
            BufferedReader b = new BufferedReader(file);           
            String line;
            paragraph = 0;          
            while ((line = b.readLine()) != null) {       	
            	paragraph++;
            	 py++;
                mainMLL.addCategory(paragraph);                                
                char[] array = new char[line.length()];
                array = line.toCharArray();
                px=6;
                for (int i = 0; i < array.length; i++) {              	
                    addLetter(array[i]);  
                    if(array[i]!= '*')
                    px++;
                }
                printParagraph(6, 3);                         
            }  
            py--;
            px--;
            b.close();
        }
    }
	
	public void backspace() {
		cursorCheck = 2;
		CharacterNode temp_cursor = cursor;
		CharacterNode preCursor = temp_cursor.getPrevious();
		
		if(temp_cursor.getNext() != null && temp_cursor.getItemName() != '*') {
			CharacterNode nextCursor = temp_cursor.getNext();
			preCursor.setNext(nextCursor);
			nextCursor.setPrevious(preCursor);
			temp_cursor = preCursor;
			cursor = temp_cursor;
			onePlusAdder();
		}
		else {
			if (temp_cursor.getItemName() != '*') {
				preCursor.setNext(null);
				cursor = preCursor;
			}
			else if (paragraph > 1) {
				CharacterNode temp = arrowElement("left");
				cursor = temp;
				cursorCheck = 2;
				mainMLL.deleteCategory(paragraph);
				paragraph--;
				
				int plusCounter = 0;
				while (temp.getPrevious().getItemName() == '+') {
					plusCounter++;
					temp = temp.getPrevious();
				}
				cursor = temp.getPrevious();
				updateCursor(0, plusCounter + 1);
			}
		}
	}
	
	public void delete() {
        CharacterNode temp_cursor = cursor;
        if(temp_cursor.getNext()!= null && temp_cursor.getNext().getNext()!=null) {//middle delete
            CharacterNode cursornewnext = temp_cursor.getNext().getNext();
            temp_cursor.setNext(cursornewnext);
            cursornewnext.setPrevious(temp_cursor);
        }
        else if((temp_cursor.getNext()!= null) && temp_cursor.getNext().getNext()==null) {

        	temp_cursor.setNext(null);

        }
    }
	
	public void insert_overwrite() {
		CharacterNode temp_cursor = cursor;
		
		if(temp_cursor.getNext() != null && temp_cursor.getPrevious() != null && temp_cursor.getPrevious().getPrevious() != null) {
			CharacterNode preCursor = temp_cursor.getPrevious().getPrevious();
			preCursor.setNext(temp_cursor);
			temp_cursor.setPrevious(preCursor);
			
			cursor = temp_cursor.getNext();
		}
		
		onePlusAdder();
	}

	public void selection() {
		boolean flag = true;
		CharacterNode temp = null;
		CharacterNode temp2 = null;
		if (startTempPy == endTempPy) {
			if (startTempPx < endTempPx) { // left to right
				startSelection = startSelection.getNext();
				temp = startSelection;
				temp2 = endSelection;
				selected = 1;
			}
			else if (startTempPx > endTempPx) {
				endSelection = endSelection.getNext();
				temp = endSelection;
				temp2 = startSelection;
				selected = 2;
			}
			else {
				temp = startSelection;
				temp2 = startSelection; 
				flag = false;
			}
		}
		else if (startTempPy < endTempPy) {
			startSelection = startSelection.getNext();
			temp = startSelection;
			temp2 = endSelection;
			selected = 1;
		}
		else {
			endSelection = endSelection.getNext();
			temp = endSelection;
			temp2 = startSelection;
			selected = 2;
		}
		
		cutSelected = selected;
		
		int i = 0;
		int tempHMS = selectionCounter;
		for (i = 0; temp != temp2; i++) {
			copy[i] = temp.getItemName();
			
			temp = temp.getNext();

			
			selectionCounter++;
		}
		if (flag) {
			copy[i] = temp2.getItemName();
			selectionCounter++;
		}
		
		copy[i + 1] = '!';

		
		if (howManySelection > 0) {
			selectionCounter -= tempHMS;
		}
	}
	
	public void paste() {
		cursorCheck = 1;
		for (int i = 0; copy[i] != '!'; i++) {			
			addLetter(copy[i]);	
			updateCursor(1, 1);
		}
	}
	
	public void find() {
		int cursor_finder = 0;
		int search_counter = 0;
		boolean flag = false;
		find = false;
		
		if(search_start != null) {
			while(search_start.getItemName() != findar[search_counter]) {
				if(search_start != null && search_start.getNext() != null)
				search_start = search_start.getNext();
				else break;
				cursor_finder++;
			}
		}
		if(search_start.getItemName() == findar[search_counter]) {
			flag = true;
			replacestart = search_start;
		}
		search_counter++;
		for (int i = 0; i < find_counter-1; i++) {
			if(search_start != null && search_start.getNext( )!= null)
			search_start = search_start.getNext();
			if (search_start.getItemName() != findar[search_counter]) {
				flag = false;
				break;
			}
			search_counter++;
			cursor_finder++;
		}
		if(flag) {
			replaceend = search_start;
			cursorCheck = 1;
			find = true;
			if(enter_counter_find == 0) {
				px = 6;
				updateCursor(cursor_finder + 1, 0);
			}
			else
				updateCursor(cursor_finder, 0);
			cursor = search_start;
		}
		else {
			find_counter = 0;
			search_start = mainMLL.head.getRight().getNext();
		}
	}
	
	public void cut() {	
		CharacterNode next = null;
		CharacterNode pre = null;
		if (cutSelected == 1) {
			pre = startSelection.getPrevious();
			if(endSelection.getNext() != null) {
				next = endSelection.getNext();
			}
			pre.setNext(next);
			if(endSelection.getNext() != null) {
				next.setPrevious(pre);
			}
			cursorCheck = 2;
			updateCursor(0, selectionCounter);
			for (int i = 0; i < selectionCounter; i++) {
				onePlusAdder();
			}
		}
		else if (cutSelected == 2) {
			pre = endSelection.getPrevious();
			if(startSelection.getNext() != null) {
				next = startSelection.getNext();
			}
			pre.setNext(next);
			if(startSelection.getNext() != null) {
				next.setPrevious(pre);
			}
		}
		
		cursor = pre;
		startSelection = null;
		endSelection = null;
		selectionCounter = 0;
		howManySelection = 0;
	}
	
	public void plusAdder() {

		ParagraphNode temp = mainMLL.head;
		CharacterNode cursorplace = cursor;
		while(temp!=null) {
			int pxx = 6;
			boolean flag = true;
			CharacterNode tempItem2 = null;
			int counter = 0;
			
				CharacterNode tempItem = temp.getRight();
				while (tempItem != null && tempItem.getNext() != null) {			
					if (pxx <= 64) {
						pxx++;					
					}
					else if (pxx == 65) {
						flag = true;
						if(tempItem.getNext() != null)
						tempItem2 = tempItem;
						if(tempItem.getItemName() != '+' &&  tempItem.getNext().getItemName() != ' ') {
							while(tempItem.getItemName() != ' ') {
								tempItem = tempItem.getPrevious();
								counter++;	
								if(tempItem.getItemName() == '*') {
									counter = 0;
									flag = false;
									tempItem = tempItem2;
									break;
								}
							}
							if(flag) {
								cursor = tempItem;
								for (int i = 0; i < counter; i++) {
									addLetter('+');
								}
								if(cursorplace.getNext() == null && flag)
								if(!allignleft)
								updateCursor(counter + 1, 0);
								counter = 0;
								tempItem = cursor;
							}
						
						}					
						pxx = 7;
						
					}

					tempItem = tempItem.getNext();
				}
				
			
				temp = temp.getDown();
		}
		cursor = cursorplace;
	}
	
	public void onePlusAdder() {
		CharacterNode plus = new CharacterNode('+');
		CharacterNode temp = cursor;
		
		while (temp.getNext() != null && temp.getNext().getItemName() != '+') {
			temp = temp.getNext();
		}
		
		if (temp != null && temp.getNext() != null) {
			plus.setNext(temp.getNext());
			temp.getNext().setPrevious(plus);
			temp.setNext(plus);
			plus.setPrevious(temp);
		}
	}
	public void justified() {
		ParagraphNode prgrNode = mainMLL.head;
		CharacterNode lastcursor = cursor;
		

		while(prgrNode!= null) {
			CharacterNode temp2 = null;
			CharacterNode temp3 = null;
			CharacterNode temp4_for_loop = null;
			CharacterNode temp = prgrNode.getRight();
			do {
				if(temp4_for_loop!=null) temp = temp4_for_loop;
				boolean flag = false;
				int pxx = 0;
				int pluscounter = 0;
				int space_counter = 0;
				int plus_counter2 = 0;
			while (temp != null && temp.getNext() != null && pxx<=58) {					
					pxx++;
					temp = temp.getNext();
					if(temp.getItemName()==' ') {
						space_counter++;
						if(space_counter==1) {
							temp2 = temp;
						}
					}
					if(temp.getItemName()=='+') {
						pluscounter++;
						if(pluscounter == 1) {
							temp3 = temp.getPrevious();
							temp4_for_loop = temp3.getNext();
						}
						
						if(temp.getNext()==null || temp.getNext().getItemName()!='+')
							break;
					}
						
				
			}
			plus_counter2 = pluscounter;
			if(pluscounter<=8 && pluscounter>=1 && space_counter>3) {
				temp = temp2;
				cursor = temp;
				addLetter('-');
				pluscounter--;
				temp = temp3;
				while(temp.getItemName()!=' ') temp = temp.getPrevious();
				temp = temp.getPrevious();
				while(temp.getItemName()!=' ') temp = temp.getPrevious();
				cursor = temp;
				addLetter('-');
				pluscounter--;
				temp = temp2.getNext().getNext();
					while(temp.getNext()!=null && pluscounter>0 && temp.getItemName()!= '+' && temp!= temp3.getPrevious()) {// + oldugunda cıkıcak tersten yapılacak
						if(plus_counter2/2 >= pluscounter) break;
						temp = temp.getNext();
						if(temp.getItemName()==' ' && flag) {
							cursor = temp;
							addLetter('-');
							pluscounter--;
							flag = false;
						}
						if (temp.getItemName()==' ' &&!flag) {
							flag = true;
						}
					}	
					flag = false;
					while(temp3.getPrevious()!=null && pluscounter>0 && temp3.getItemName()!= '+' && temp3!= temp2) {// + oldugunda cıkıcak tersten yapılacak
						temp3 = temp3.getPrevious();
						if(temp3.getItemName()==' ' && flag) {
							cursor = temp3;
							addLetter('-');
							pluscounter--;
							flag = false;
						}
						if (temp3.getItemName()==' ' &&!flag) {
							flag = true;
						}
					}

			}
			else if(pluscounter == 0) {
				temp4_for_loop = temp.getNext();
			}
			while(temp4_for_loop!= null&& temp4_for_loop.getNext()!=null&& temp4_for_loop.getItemName() == '+') {
				temp4_for_loop = temp4_for_loop.getNext();
			}
			
			}while (temp4_for_loop!=null&& temp4_for_loop.getNext()!=null);
			prgrNode = prgrNode.getDown();
		}		
		cursor = lastcursor;
	}
	public void plusCheckForWord() {
		ParagraphNode prg = mainMLL.head;
		while(prg != null) {
			int counter = 0;
			int wordcounter = 0;
			CharacterNode cursorplace = prg.getRight();
			CharacterNode plusstart = null;
			CharacterNode plusend = null;
			while(cursorplace != null && cursorplace.getNext() != null) {
				while(cursorplace.getNext() != null && cursorplace.getItemName()!='+') {
					cursorplace = cursorplace.getNext();
				}
				plusstart = cursorplace.getPrevious();
				while(cursorplace != null && cursorplace.getItemName() == '+') {
					counter++;
					cursorplace = cursorplace.getNext();
				}
				plusend = cursorplace;
				while(cursorplace != null && cursorplace.getNext() != null && cursorplace.getItemName() != ' ') {
					wordcounter++;
					cursorplace = cursorplace.getNext();
				}
				wordcounter++;

				if(counter != 0 && counter == wordcounter) {
					plusstart.setNext(plusend);
					plusend.setPrevious(plusstart);
					cursorCheck = 2;
					updateCursor(0, counter + 1);
				}
				wordcounter = 0;
				counter = 0;
			}
			prg = prg.getDown();
		}
				
	}
	public void allignLeftPlus() {
		int pxx = 0;
		CharacterNode tempcurosrCharacterNode = cursor;
		ParagraphNode temp = mainMLL.head;
		while(temp!=null) {
			CharacterNode temp2 = temp.getRight();
			while(temp2.getNext()!=null) {
				pxx++;
				if(pxx==60)
					pxx=0;
				temp2 = temp2.getNext();
			}
			if(temp.getDown()!=null) {
				cursor = temp2;
				while(pxx!=57) {					
					pxx++;
					addLetter('+');
				}
			}
			temp = temp.getDown();
		}
		cursor = tempcurosrCharacterNode;
	}
	public void allignLeft() {
		ParagraphNode temp = mainMLL.head;
		CharacterNode lastcursor = cursor;
		CharacterNode tempItem = null;
		int counter = 0;
		while (temp != null) {
			tempItem = temp.getRight();
			CharacterNode lastLine = tempItem;
			while(tempItem!=null && tempItem.getNext()!= null) {
				counter = 0;
				while(counter<=58 && lastLine.getNext()!=null) {
					lastLine = lastLine.getNext();
					counter++;
				}
				while (tempItem!= lastLine) {
					if(tempItem.getItemName()== '-') {
						tempItem.getPrevious().setNext(tempItem.getNext());
						tempItem.getNext().setPrevious(tempItem.getPrevious());									
					}
						
					tempItem = tempItem.getNext();
				}
				
			}
			plusAdder();
			
			
			if (temp.getDown() != null) {
				temp = temp.getDown();
			}
			else {
				break;
			}
		}
	

		cursor = lastcursor;

	}
}
