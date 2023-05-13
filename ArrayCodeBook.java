/**
 * An implementation of ExpansionCodeBookInterface using an array.
 */

public class ArrayCodeBook implements ExpansionCodeBookInterface {
    private static final int R = 256;        // alphabet size
    private String[] codebook;
    private int W;       // current codeword width
    private int minW;    // minimum codeword width
    private int maxW;    // maximum codeword width
    private int L;       // maximum number of codewords with 
                         // current codeword width (L = 2^W)
    private int code;    // next available codeword value
  
    public ArrayCodeBook(int minW, int maxW){
        this.maxW = maxW;
        this.minW = minW;
        initialize(false);  
    }
    public int size(){
        return code;
    }


	//this function gets the codeword width, and resets the codeword width back to the min size if flushIfFull
    public int getCodewordWidth(boolean flushIfFull)
	{    
		if(code < L) 
		{  
			return W;
		}  
		
		else if(W == maxW && flushIfFull && code == L) 
		{  
			return minW;
		}  
		
		else if(W < maxW) 
		{ 
			return W +1;
		}
		
		//In the case that code > L(we have gone over codeword width), but ifFlushNotFull, we can just return W-width doesn't change 
		//Remember, if w < maxW and code > L, w does increment but we do not need to do this in this function, the add function takes care of that
		else //if(W == maxW && !flushIfFull)
		{  
			
			return W;
		} 
    }

	//This function is meant to initalize the codebook, called at the beginning or when resetting the codebook
	private void initialize(boolean flushIfFull){
        codebook = new String[1 << maxW]; 
		//codebook = new String[1 << minW];
        W = minW;
        L = 1<<W;
        code = 0;
        // initialize symbol table with all 1-character strings
        for (int i = 0; i < R; i++) 
		{ 
            add("" + (char) i, flushIfFull); 
		}
        add("", flushIfFull); //R is codeword for EOF
    }

    public void add(String str, boolean flushIfFull) 
	{
        boolean haveRoom = false;
        if(code < L){
            haveRoom = true;
        }

        if(haveRoom){
            //code++;
			codebook[code] = str;
            code++;
        }
		
		//I added these two conditionals 
		
		//If we don't have room and W < maxW, we can increment w in order to increase codebook width
		else if(!haveRoom && W < maxW) 
		{  
			W = W+1;    
			L = 1<<W;  
			
			
			codebook[code] = str; 
            code++;
			
		}   
		
		//Else we need to reset the codebook
		else if(W == maxW && flushIfFull) 
		{  
            this.initialize(flushIfFull);
            codebook[code] = str;
            code++; 
		} 
		
		//In the case that we don't have room, W==maxW(cannot increase codebook size), and !flushIfFull(cannot reset), then we don't do anything-cannot add to codebook
    }

    public String getString(int codeword) {
        //System.err.println(codebook[codeword]);
		return codebook[codeword];
    }
    
}
