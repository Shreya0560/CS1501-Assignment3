/**
 * An implementation of CompressionCodeBookInterface using a DLB Trie.
 */ 
 
 //Issue of cannot find symbol may be becuase I have to call flushIfFull on an object when referring to it in another file
 public class DLBCodeBook implements CompressionCodeBookInterface {

  private static final int R = 256;        // alphabet size
  private DLBNode root;
  public StringBuilder currentPrefix;
  private DLBNode currentNode;
  private int W;       // current codeword width
  private int minW;    // minimum codeword width
  private int maxW;    // maximum codeword width
  private int L;       // maximum number of codewords with 
                       // current codeword width (L = 2^W)-should it say current codebook width instead??
  private int code;    // next available codeword value

  public DLBCodeBook(int minW, int maxW){
    this.maxW = maxW;
    this.minW = minW;
    currentPrefix = new StringBuilder();
    currentNode = null;
    initialize(false); 
  } 
  
  
//This function adds a word to the codebook, and resets or increases codebook width if needed
  public void add(String str, boolean flushIfFull){
    boolean haveRoom = false;
    if(root == null)
	{
      root = new DLBNode(str.charAt(0));
    }
    if(code < L)
	{
      haveRoom = true;
    }
    if(haveRoom)
	{
      if(str.length() > 0)
	  {
        add(root, code, str, 0);
      }
      code++;
    } 
	//I added these two conditionals
	else if(!haveRoom && W < maxW)  
	{  
		W = W+1;    
		L = 1<<W; 
		if(str.length() > 0)
		{
			add(root, code, str, 0);
		} 
		code++;
		
	} 
  }

  private void add(DLBNode node, int codeword, String word, int index){
    DLBNode current = node;
    char c = word.charAt(index);
    while(current != null){
      if(current.data == c){
        if(index == word.length() - 1){
          current.codeword = codeword;
        } else { //move down
          if(current.child == null){
            current.child = new DLBNode(word.charAt(index+1));
          }
          add(current.child, codeword, word, index+1);
        }
        break;
      } else {
        if(current.sibling == null){
          current.sibling = new DLBNode(c);
        }
        current = current.sibling;
      }
    }
  }

//This method gets the codeword width
  public int getCodewordWidth()
  {
	return W;
  }

//This method is called to initialize the codebook, called at to set the codebook at beginning or when resetting if flushIfFull
  private void initialize(boolean flushIfFull){
    root = null;
    W = minW;
    L = 1<<W;
    code = 0;
    for (int i = 0; i < R; i++) 
	{
      add("" + (char) i, flushIfFull); 
	}
    add("", flushIfFull); //R is codeword for EOF
  }

//advance appends the char onto the currentPrefix and tries to move down the DLB to find the new currentPrefx 
//If the new currentPrefix with appended char isn't found in DLB-not in child or child's sibling, it returns false. 
//When called in compress method, if advance returns false, it means the currentPrefix is not in the dlb codebook 
//and that the longest match was the previous currentPrefix before we appended the last char on 
//So we then output the codeword of that longest match and add the currentPrefix to the dlb codebook(in compress method)
  public boolean advance(char c){
    boolean result = false;
    currentPrefix.append(c);
    if(currentNode == null){
      currentNode = root;
      while(currentNode != null){
        if(currentNode.data == c){
          result = true;
          break;
        }
        currentNode = currentNode.sibling;
      }
    } else {
      DLBNode curr = currentNode.child;
      while(curr != null){
        if(curr.data == c){
          currentNode = curr;
          result = true;
          break;
        }
        curr = curr.sibling;
      }
    }    
    return result;
  }
  
  //This method adds a word to the codebook and resets the codebook or increments W if needed
  public void add(boolean flushIfFull){
    boolean haveRoom = false;

    if(code < L){
      haveRoom = true;
    } 

    if(haveRoom){
      DLBNode newNode = 
        new DLBNode(currentPrefix.charAt(currentPrefix.length()-1));
  
      newNode.codeword = code;
      code++;
      newNode.sibling = currentNode.child;
      currentNode.child = newNode;        
    }
	//I added these two conditionals
	else if(!haveRoom && W < maxW)  
	{  
		W = W+1;    
		L = 1<<W;  
		
		DLBNode newNode = 
        new DLBNode(currentPrefix.charAt(currentPrefix.length()-1));
  
	  newNode.codeword = code; 
	  code++;
      newNode.sibling = currentNode.child;
      currentNode.child = newNode;    
	} 
	
	else if (W == maxW && flushIfFull)
    {
      this.initialize(flushIfFull); 
      DLBNode newNode = 
        new DLBNode(currentPrefix.charAt(currentPrefix.length()-1));
  
      newNode.codeword = code;
      code++;
      currentNode = newNode;
      newNode.sibling = currentNode.child;
      currentNode.child = newNode;  
    } 

    currentNode = null;
    currentPrefix = new StringBuilder();

  }

  public int getCodeWord() {
    return currentNode.codeword;
  }

  //The DLB node class
  private class DLBNode{
    private char data;
    private DLBNode sibling;
    private DLBNode child;
    private Integer codeword;

    private DLBNode(char data){
        this.data = data;
        child = sibling = null;        
    }
  } 
}