package huffman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    private TreeMap<Character, String> encodingMap;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
        
        /*
         * Psuedo-Code (left for readability)
         * 1. Find frequency of characters in corpus (see private helper function)
         * 2. Construct priority queue based on frequency
         * 3. Begin tree construction
         * 4. WHILE |QUEUE| > 1
         * 5. Pop (dequeue) two from queue 
         * 6. Connect them to one node
         * 7. Place new parents (with frequency = sum of frequncy of children) onto queue
         * 8. END LOOP
         * 9. Now, create an encoding map by visiting each leaf and finding its character and sequence
         * 9. Return
         */
        
        PriorityQueue<HuffNode> pq = new PriorityQueue<HuffNode>(); //The priority node for the Trie creation
        
        TreeMap<Character, Integer> freqMap = constructFrequencyMap(corpus); //temporary variable holding characters and frequency
        
        
        //copy treemap to priorityqueue by placing character and frequency in HuffNode
        for(Map.Entry entry : freqMap.entrySet()) {
            pq.add(new HuffNode((char)entry.getKey(), (int)entry.getValue()));
        }
        
        
        this.trieRoot = simplifyQueue(pq).poll();
        
        this.encodingMap = createEncodingMap(this.trieRoot, "");
        
    }
    
    /**
     * A recusrive function which will depth-first iterate through the tree until
     *  it reaches a leaf, when it will add
     * @param root
     * @param pathToRoot
     * @return
     */
    private TreeMap<Character, String> createEncodingMap(HuffNode root, String pathToRoot)
    {
        
        if(root.left == null && root.right == null)         //beautiful leaf, add to encoding (base case)
        {
            TreeMap<Character, String> tmp = new TreeMap<Character, String>();
            tmp.put(root.character, pathToRoot);
            return tmp; //java yelled at me for putting this in one line, so temp variable!
        }
        
        else if(root.left == null || root.right == null)    //only one child, error (won't happen in my code, gods be good)
        {
            throw new IllegalArgumentException("One child to parent in Trie");
        }
        
        else                                                //inner node, recurse!
        {
            TreeMap<Character, String> tmp = new TreeMap<Character, String>();
            tmp.putAll(createEncodingMap(root.left, pathToRoot+"0"));   //go left, add 0 to encoding
            tmp.putAll(createEncodingMap(root.right, pathToRoot+"1"));  //go right, add 1 to encoding
            return tmp; //it is recursive after all
        }
    }
    
    /**
     * Takes a priority queue and creates a huffman tree by connecting each node 
     *  until only one remains, then returns that
     * @param pq the priority queue of nodes with no simplification (so just char and freq)
     * @return a priorityqueue holding the root node
     */
    private PriorityQueue<HuffNode> simplifyQueue(PriorityQueue<HuffNode> pq)
    {
      //By assumption (thanks Forney!) this loop will execute ONCE at minimum
        //since |corpus| > 1 in specifications
        HuffNode left, right, parent; //loop variables, could declare inside by I'm a C-Programmer at heart
        while(pq.size() > 1)
        {
            left  = pq.poll();  //dq min val
            right = pq.poll();  //dq min val
            
            parent = new HuffNode('\0', (int)left.count + (int)right.count);
            parent.left = left;
            parent.right = right;
            
            pq.add(parent);
        }
        //pq will not be less than 0 since the smallest size in loop can be is 2
        //  and if size is 2, it will create 1 parent then exit the loop, soooo
        //root is left!
        return pq;
    }
    
    /**
     * Takes corpus and returns a tree map based on frequency of ASCII values 
     *  within corpus
     * @param corpus a given text (see Huffman docs for Forney's better version)
     * @return {@code Treemap} holding the characters present and their frequency
     */
    private TreeMap<Character, Integer> constructFrequencyMap(String corpus)
    {
        /*
         * Psuedo-code (left for readability)
         * 1. Create treemap
         * 2. Go through each part of string
         * 3. For each character, either add it to tree or increment by one
         * 4. Done
         */
        
        //Init variables
        //Forney said to use a TreeMap for consistency :)
        TreeMap<Character, Integer> charFreqMap = new TreeMap<Character, Integer>(); //holds character and frequency
        char[] corpusAsArray = corpus.toCharArray(); //Holds corpus in array so I can use for-each
                                                     // could also do for(i < corpus.length())
        
        for(char i : corpusAsArray)
        {
            if(charFreqMap.containsKey(i))  //Already placed in map, increment freq by 1
            {
                charFreqMap.put(i, charFreqMap.get(i) + 1); //increment by 1
            }
            else
            {
                charFreqMap.put(i, 1); //first time character found, place a 1 there
            }
        }
        
        return charFreqMap;
        
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        
        //Just so you know Dr. Forney, I hate you for making me use spaces instead of tabs
        //but its easier than converting your code :(
        
        //Init variables
        byte[] answer; //holds the resulting byte code from compression
        ByteArrayOutputStream mutableAnswer = new ByteArrayOutputStream(); //answer before becoming an array (more mutable)
        
        
        //add length to beginning (took me an hour to remember this, grrr)
        mutableAnswer.write(message.length());
        
        //iterate through each character, append its string to a 'encoded' string
        
        char[] messageAsArray = message.toCharArray();  //making iterating easier
        String encodedMessage = new String();           //cumulative message
        
        for(char i : messageAsArray)
        {
            
            encodedMessage += this.encodingMap.get(i);
            
        }
        
        //append encoding to end
        
            byte[] temp = stringToByteArray(encodedMessage);
            
            for(byte b : temp)
            {
                mutableAnswer.write(b);
            }
        
        answer = mutableAnswer.toByteArray();
        
        return answer;
       
    }
    
    private byte[] stringToByteArray(String encodedMsg)
    {        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int conversionHelper;
        
        //add left padding since << is annoying as heck
        while(encodedMsg.length()%8 != 0)
        {
            encodedMsg = encodedMsg+"0";
        }
        
        for(int i = 0; i <encodedMsg.length(); i+=8)
        {
            conversionHelper = Integer.parseInt(encodedMsg.substring(i, i+8),2);
            baos.write(conversionHelper);
        }
        
        return baos.toByteArray();
        
    }
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        
        //Loop variables
        HuffNode current; //Holds the current node being analyzed
        String uncompressedMsg = new String(); //resulting string
        
        //first, find length of message
        int length_of_msg = (int)compressedMsg[0];
              
        current = this.trieRoot;
        for(int n = 1*8 ; n < (compressedMsg.length * 8) ; n++)
        {
            
            
            if(uncompressedMsg.length() >= length_of_msg)
            {
                return uncompressedMsg;
            }
            
            if(current.left == null && current.right == null)
            {
                uncompressedMsg += current.character;
                current = this.trieRoot;
            }
            
            if(getBit(compressedMsg, n) == 1)
            {
                current = current.right;
            }
            else
            {
                current = current.left;
            }
            
        }
        
        return uncompressedMsg;
        
    }
    
    
    /**
     * Takes a byte array and returns a single bit as position
     * @param data the byte array contianing all data
     * @param pos the desired bit within the overall binary number
     * @return the bit as an integer 0 or 1
     */
    private int getBit(byte[] data, int n) {
        //I'll be honest, this is some whack frankenstein code 
        // 1/2 is from stack overflow
        // 1/2 is from geeksforgeeks
        // byte[] is just weird :////
        int currentBytePos = n/8;
        int currentBitPos= n%8;
        byte currentByte = data[currentBytePos];
        int asInt = currentByte>>(8-(currentBitPos+1)) & 0x0001;
        return asInt;
     }

    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees, in the case of inner 
     * nodes) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }

}
