/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt > output.lzw  (compress input.txt 
 *                                                         into output.lzw)
 *  Execution:    java LZWmod + < output.lzw > input.rec  (expand output.lzw 
 *                                                         into input.rec)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZW {
    private static final int R = 256;        // alphabet size 
	private static boolean flushIfFull = false;

    //write it in
	public static void compress() {  
        CompressionCodeBookInterface codebook = 
            new DLBCodeBook(9, 16); 
		
		if (flushIfFull)
        {
            BinaryStdOut.write(true); 
        } 
        else
        {
            BinaryStdOut.write(false);
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            if(!codebook.advance(c)){ //found longest match
                int codeword = codebook.getCodeWord();
                BinaryStdOut.write(codeword, codebook.getCodewordWidth()); 
                codebook.add(flushIfFull);
                codebook.advance(c);
            }
        }
        int codeword = codebook.getCodeWord();
        BinaryStdOut.write(codeword, codebook.getCodewordWidth()); 

        BinaryStdOut.write(R, codebook.getCodewordWidth()); 
        BinaryStdOut.close();
    }

	//This method expands the file
    public static void expand() { 
		//initialize();
        ExpansionCodeBookInterface codebook = new ArrayCodeBook(9, 16); 
		if(BinaryStdIn.readInt(1) == 1) 
		{ 
			flushIfFull = true;;
		} 
		else 
		{  
			flushIfFull = false;;
		}

        int codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));
        String val = codebook.getString(codeword);

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));

            if (codeword == R) break;
            String s = codebook.getString(codeword); 
			//System.out.println(s);  
			//If codeword we read is equal to next available codeword
            if (codebook.size() == codeword) s = val + val.charAt(0); // special case hack 
			//if ((1<<codebook.getCodewordWidth(false)) -1 == codeword) s = val + val.charAt(0); // special case hack

            codebook.add(val + s.charAt(0), flushIfFull);
            val = s;

        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) 
	{
        if(args[0].equals("-"))  
		{  

			//If args equals r, we do reset the codebook if W is at max and codebook is filled
			if(args[1].equals("r")) 
			{  
				flushIfFull = true; 
				compress();
			} 
			
			//If args equals n, and we need to reset the codebook, we do nothing
			else if(args[1].equals("n"))
			{  
				flushIfFull = false; 
				compress();
			} 
			
			else throw new RuntimeException("Illegal command line argument"); 
			
			//compress();
		}
        else if (args[0].equals("+")) 
		{  
			expand();
		}
        else throw new RuntimeException("Illegal command line argument");
    }

}
