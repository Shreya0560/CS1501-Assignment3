# CS 1501 – Algorithm Implementation – Assignment #3

Due: Monday December 5th @ 11:59pm on Gradescope

Late submission deadline: Wednesday December 7th @11:59pm with 10% penalty per late day

You should submit the Java files `DLBCodeBook.java`, `ArrayCodeBook`, and `LZW.java` to GradeScope (the link is on Canvas). You must also submit a writeup named `a3.md` and an Assignment Information Sheet `InfoSheet.md` as described below.


## OVERVIEW
 
**Purpose:** The purpose of this assignment is to fully understand the LZW compression algorithm, its performance and its implementation. 

_Goal 1_: Allow the codebook size to increase beyond the `4096` entries in the textbook's implementation using adaptive codeword width.

_Goal 2_: Allow LZW to learn new patterns after the codebook size is reached by giving the user the option to reset the codebook. 

## PROCEDURE

1.	Thoroughly read the description/explanation of the LZW compression algorithm as discussed in lecture and in the Sedgewick text.

2.	Read over and make sure you understand the code provided in Lab 7 in the file `LZWmod.java`.

3.	There are two fundamental problems with Lab 7 code as given: 

    - The code uses a fixed length, relatively small codeword size (12 bits).  With this limit, the program will run out of codewords relatively quickly and will not handle large files (especially archives) well.

    - When all code words are used, the program continues to use the existing dictionary for the remainder of the compression.  This may be ok if the rest of the file to be compressed is similar to what has already been compressed, but it may not be.

4. In this assignment we will modify Lab 7's LZW code so as to correct (somewhat) these problems.  

    - First, the code from Lab 7 has been restructured to separate the LZW algorithm from the codebook data structures. Two interfaces have been created, namely `CompressionCodeBookInterface` and `ExpansionCodeBookInterface`, to model the functions provided by the codebook for LZW compression and expansion, respectively. An implementation that uses a DLB Trie is provided for the `CompressionCodeBookInterface` and one that uses an array for `ExpansionCodeBookInterface` have also been created. The DLB implementation is in `DLBCodeBook.java` and the array implementation is in `ArrayCodeBook.java`. The main advantage of that separation is to be able to keep the LZW algorithm unmodified as we correct the problems mentioned above. Although the provided code still has the same problems, but **we will be able to correct them with a minimal change to `LZW.java`**.

    - Examine the provided code very carefully, convincing yourself exactly what is accomplished by each function and by each statement within each function. 
 
    -  In the first modification, the `DLBCodeBook` and `ArrayCodeBook` will be modified so that the LZW algorithm have a varying number of bits for the codewords, as discussed in the adaptive-codeword-size idea in class.  The codeword size should vary from 9 bits to 16 bits and should increment when all codes for the previous size have been used.  This also does not require a lot of modification to the classes, but you must REALLY understand exactly what the program is doing at each step in order to do this successfully (so you can keep the compress and decompress processes **in sync**).  You will need to make the following modifications:
      - Modify the public `add` methods to implement the codeword size incrementing logic
      - Modify `getCodewordWidth()` in `ArrayCodeBook` to return the **effective** codeword size so that the expand is in sync with the compress.
      - Modify `LZW.java` so that the codebooks are initialized with the correct minimum and maximum codeword sizes.
    
    - Once you get the program to work, thoroughly test it to make sure it is correct.  If the algorithm is correct, the expanded file should be identical to the original one.  Some hints about the variable-length code word implementation are given later on in this file.

    - As a partial solution to the issue of the dictionary filling, you will give the user the option to reset the dictionary or not via a command line argument.  See more details on the command line arguments below, but the argument `r` will cause the dictionary to reset once all (16-bit) code words have been used and the argument `n` for "do nothing" will cause the dictionary to stay as is.
      - As discussed in lecture this option will erase and reset the entire dictionary and start rebuilding it from scratch.  As with the variable bits, be careful to sync both the compress and decompress to reset the dictionary at the same point.
      - You will have to add code to reset the dictionary to the public `add` methods of `DLBCodeBook` and  `ArrayCodeBook`. Calling the private `initialize` method should help you in doing so.  
      - You will also need to modify `getCodewordWidth()` in `ArrayCodeBook` to return the effective codeword size so that the expand is in sync with the compress.
      - Since now a file may be compressed with or without resetting the dictionary, in order to decompress correctly your program must be able to discern this fact.  This can be done quite simply by writing a 1-bit flag at the beginning of the output file. Then, before decompression, your program will read this flag and determine whether or not to reset the dictionary when running out of codewords.
      - Note the `flushIfFull` boolean variable inside `LZW.java` and how it is used when calling `add` and `getCodewordWidth`. Consider setting this variable based on the command line argument for compression and based on the bit flag read from the compressed file in expansion.

5. The provided `LZW.java` already has a command-line argument to choose compression or decompression. Modify the program so that for compression a command-line argument also allows the user to choose how to act when all codewords have been used. This extra argument should be an `n` for "do nothing", or `r` for "reset".  Note that these arguments are only used during compression; for decompression the algorithm should be able to automatically detect which technique was used and decompress accordingly.

6. File input and output can be supplied using the standard redirect operators for standard I/O: Use "<" to redirect the input from a file and use ">" to redirect the output to a file. For example, if you wish to compress the file `code.txt` into the file `code.lzw`, resetting the dictionary when you run out of codewords, you would enter at the prompt:

```shell
$ java LZW - r < code.txt > code.lzw
```

To prevent headaches (especially during debugging), you should not replace the original file with the new one (i.e., leave the original file unchanged). Thus, make sure you use a name for the output file that is different from the input file.  If you then want to decompress the `code.lzw` file, you might enter at the prompt

```shell
$ java LZW + < code.lzw > code.rec
```

The file `code.rec` should now be identical to the file `code.txt` (You can confirm that using `diff` in Linux/MacOS and `fc` in Windows).  Note that in the decompression command there is no flag for what to do when the dictionary fills – this should be obtained from the front of the compressed file itself (which, again, requires only a single bit).

6.	Once you have your `LZW.java` program working, you should analyze its performance.  A number of files to use for testing are provided in this repository. Specifically, you will compare the performance of 4 different implementations:

  - The provided implementation that uses 12-bit codewords before any modifications. 
  - Your modified implementation with codeword size going from 9 bits to 16 bits as explained above without dictionary reset.
  - Your modified implementation with codeword size going from 9 bits to 16 bits as explained above with dictionary reset.
  - The predefined Unix `compress` program (which also uses the LZW algorithm).  If you have a Mac or Linux machine you can run this version directly on your computer.   If you have a Windows machine, you can use the version of `compress.exe` in this repository (obtained originally from http://unxutils.sourceforge.net/ ).  To run `compress.exe` you need to open a command-line prompt and run it from there. To decompress with this program use the flag `-d`.

Run all programs on all of the files and for each file record the original size, compressed size, and compression ratio (original size / compressed size).

7.	Write a short (~2-page) paper, named `a3.md` using [Github Markdown syntax](https://guides.github.com/features/mastering-markdown/), that discusses each of the following:
  - How all four of the LZW variation programs compared to each other (via their compression ratios) for each of the different files.  Where there was a difference between them, explain (or speculate) why.  To support your assertions, include a table showing all of the results of your tests (original sizes, compressed sizes and compression ratios for each algorithm).
  - For all algorithms, indicate which of the test files gave the best and worst compression ratios, and speculate as to why this was the case.  If any files did not compress at all or compressed very poorly (or even expanded), speculate as to why.

## Printing debugging messages

Carefully trace what your code is doing as you modify it.  You only have to write a few lines of code for this program, but it could still require a substantial amount of time to get it to work properly.  Clearly, the trickiest parts occur when the codeword size increases and when the dictionary is reset.  It is vital that these changes be made in a consistent way during both compress and decompress.  One idea for debugging is to have an extra output file for each of the `compress()` and `expand()` methods to output any debug messages as explained below.

Since the way we use `LZW.java` is by redirecting its output, it won't be possible to use `System.out.println()` to print debug messages to the console. Instead, you should use `System.err.println()`, which uses the standard error stream, which is still connected to the console. If you want to save the debug messages to a file (for example to have compression and expansion debug messages side by side), you can redirect the standard error stream as in the following commands:

`java LZW - < input.txt > input.lzw 2> debug-compress.txt`

`java LZW + < input.lzw > input.rec 2> debug-expand.txt`

Then, you can open the files `debug-compress.txt` and `debug-expand.txt` side by side for comparison. 

For debugging, I recommend that you print the values of the codeword size, the written/read codeword value, the corresponding string, and the (codeword, string) pair added to the codebook at each step. Printing these out the iterations just before and after a bit change or reset is done can help you a lot to debug your code efficiently.

  
## EXTRA CREDIT

If you want to try some extra credit on this assignment, you can implement the reset in seamless way, so that the user does not have to specify whether or not to reset the dictionary.  This would involve some type of monitoring of the compression ratio once the codewords are all used and a reset would occur only when the compression ratio degrades to some level (you may have to do some trial and error to find a good value for the reset trigger).

## SUBMISSION REQUIREMENTS
You must submit to Gradescope the following files:

1. Your `LZW.java`, `DLBCodeBook.java`, and `ArrayCodeBook.java` 
2. Assignment Information Sheet.
3. The 2-page writeup, named `a3.md`.

The idea from your submission is that your TA (or the autograder or both) can compile and run your programs from the command line WITHOUT ANY additional files or changes, so be sure to test it thoroughly before submitting it. If the TA cannot compile or run your submitted code it will be graded as if the program does not work.
If you cannot get the programs working as given, clearly indicate any changes you made and clearly indicate why on your Assignment Information Sheet.  You will lose some credit for not getting it to work properly, but getting the main programs to work with modifications is better than not getting them to work at all.  A template for the Assignment Information Sheet can be found in this repository. You do not have to use this template but your sheet should contain the same information.  

**Note**: If you use an IDE such as NetBeans, Eclipse, or IntelliJ, to develop your programs, make sure they will compile and run on the command line before submitting – this may require some modifications to your program (such as removing some package information). 

## RUBRICS
__*Please note that if an autograder is available, its score will be used as a guidance for the TA, not as an official final score*__.

Please also note that the autograder rubrics are the definitive rubrics for the assignment. The rubrics below will be used by the TA to assign partial credit in case your code scored less than 40% of the autograder score. If your code is manually graded for partial credit, the maximum you can get for the autograded part is 60%.

Item|Points
----|------
`DLBCodeBook` modified correctly for adaptive codeword size|	20 points
`ArrayCodeBook` modified correctly for adaptive codeword size|	20 points
`LZW.java` modified correctly for adaptive codeword size|	5 points
`LZW.java` modified correctly for reset|	10 points
`DLBCodeBook` modified correctly for reset|	10 points
`ArrayCodeBook` modified correctly for reset|	10 points
Write-up|	15 points
Comments and coding style|	5 points
Assignment Information Sheet|	5 points
Extra Credit|	10 points
