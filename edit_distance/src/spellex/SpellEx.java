package spellex;

import java.util.*;

public class SpellEx {
    
    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map 
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;
    
    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given
     * "dictionary" of words mapped to their frequencies found
     * in some corpus (with the higher counts being the more
     * prevalent, and thus, the more likely to be suggested)
     * @param words The map of words to their frequencies
     */
    SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }
    
    
    /**
     * Returns the edit distance between the two input Strings
     * s0 and s1 based on the minimal number of insertions, deletions,
     * replacements, and transpositions required to transform s0
     * into s1
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance (String s0, String s1) {
        
        //create a 2d array for our memoization structure
        //fill the gutters
        //RECURRENCE:
        // SUBSTRING match (aka, s1[r] = s2[c] && t[r-1][c-1] = 0

        //create structure (+1 bc of null gutters
        int [][] editDistanceStruct = new int[s0.length()+1][s1.length()+1];
        
        //create gutters (using matlab conventions just to mess with you :) )
        //gotta have some fun when corona makes me stay at home 22 hours a day
        for(int ii = 0; ii < s1.length()+1 ; ii++)
        {
            editDistanceStruct[0][ii] = ii;
        }
        for(int ii = 0; ii < s0.length()+1; ii++)
        {
            editDistanceStruct[ii][0] = ii;
        }
        
        //now for the real fun :)
        
        int in, de, re, tr;
        
        //fun quick of the way i've done this
        //when accessing the string, it is not /quite/ the same as the array since the array has gutters
        //could add a null to the string...
        //i elected to just -1 to every .charAt() call (you'll see :) ) 
        for(int r = 1; r < s0.length()+1; r++) 
        {
            for(int c = 1; c < s1.length()+1; c++)
            {
                //since this may or may not be declared, don't want to mess up our min function with junk info
                tr = Integer.MAX_VALUE;
                
                //check if substrings are equal
                if(editDistanceStruct[r-1][c-1] == 0 && s0.charAt(r-1) == s1.charAt(c-1))
                {
                    //yay, no edits done
                    editDistanceStruct[r][c] = 0;
                }
                else  //not equal substrings (t[r][c] != 0)
                {
                    //insert
                    in = editDistanceStruct[r][c-1] + 1;
                    
                    //deletion
                    de = editDistanceStruct[r-1][c] + 1;
                    
                    //replacement
                    if(s0.charAt(r-1) == s1.charAt(c-1))
                    {
                        //replacement has 0 cost
                        re = editDistanceStruct[r-1][c-1] + 0; //for extra clarity
                    }
                    else
                    {
                        re = editDistanceStruct[r-1][c-1] + 1;
                    }
                    
                    if(r >=2 && c >=2 && s0.charAt(r-1) == s1.charAt(c-1-1) && s0.charAt(r-1-1) == s1.charAt(c-1) )
                    {
                        //cool, transposition
                        tr = editDistanceStruct[r-2][c-2] + 1;
                    }
                    
                    editDistanceStruct[r][c] = Math.min(in, Math.min(de, Math.min(re, tr)));//easier than throwing in an object
                }
            }
        }
        //debugPrint(editDistanceStruct);
        return editDistanceStruct[s0.length()][s1.length()];
        
    }
    
    
    //who doesn't use a print statement to debug ;)
    //pretty straightfoward, just prints a 2d array
    private static void debugPrint(int[][] struct)
    {
        for(int i = 0; i < struct.length; i++)
        {
            for(int j = 0; j < struct[0].length; j++)
            {
                System.out.print(struct[i][j]);
                System.out.print(" ");
            }
            System.out.println("");
        }
        
        System.out.println("\n");
    }
    
    /**
     * 
     * @param s0 first string to compare
     * @param s1 second string to compare
     * @return 0 if equal; greater than 0 if s0 is first in alphabet; less than 0 if s0 is later in alphabet
     */
    private static int compare(String s0, String s1)
    {
        for(int i = 0; i < s0.length() && i < s1.length(); i++)
        {
            if(s0.charAt(i) > s1.charAt(i))
            {
                //in ASCII, this means s0 is later in alphabet
                return -1; //arbitrary value, don't care about sum like compareTo()
            }
            else if(s0.charAt(i) < s1.charAt(i))
            {
                return 1; //ditto to above but vice versa
            }
        }
        return 0;
    }
    
    /**
     * Returns the n closest words in the dictionary to the given word,
     * where "closest" is defined by:
     * <ul>
     *   <li>Minimal edit distance (with ties broken by:)
     *   <li>Largest count / frequency in the dictionary (with ties broken by:)
     *   <li>Ascending alphabetic order
     * </ul>
     * @param word The word we are comparing against the closest in the dictionary
     * @param n The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant (String word, int n) {
        //throw new UnsupportedOperationException();
        
        //brute force method (maybe I think of a faster way later? A ver)
        //maybe sort the dictionary by frequency? could decrease wallclock time but would need a map instead of hashmap
        //      (i think)
        
        Set<String> answer = new HashSet<String>();
        
        //for each through the dictionary, we are doing the HARD way
        
        //iterate through dictionary
        //for each of dictionary
        for(Map.Entry<String, Integer> entry : dict.entrySet())
        {
            
            String wordToTest = entry.getKey(); //the current word in the dictionary, might be added...
            
            //any answer is better than no answer
            if(answer.size() < n)
            {
                answer.add(wordToTest);
            }
            //else, must test before adding
            else
            {
                int newDistance = SpellEx.editDistance(wordToTest, word); //edit distance from K word in dictionary and the input word
                
                //now, iterate through answer and find if this new word is better
                //PROBLEM: can't iterate through a set and EDIT it...
                //solution! copying ?
                
                Set<String> answerCopy = new HashSet<String>();
                answerCopy.addAll(answer);
                
                for(String oldWord:answerCopy)
                {
                    
                    int oldDistance = SpellEx.editDistance(oldWord, word);

                    if(newDistance < oldDistance)
                    {
                        //new word is a better solution
                        answer.remove(oldWord);
                        answer.add(wordToTest);
                        //but now! what if old word is still better than the next? here is the issue ....
                        //my solution
                        wordToTest = oldWord;//future loops will now check this one
                    }
                    else if(newDistance == oldDistance)
                    {
                        //tie breaking time
                        
                        //first based on frequency
                        if(dict.get(wordToTest) > dict.get(oldWord))
                        {
                          //new word is a better solution
                            answer.remove(oldWord);
                            answer.add(wordToTest);
                            //but now! what if old word is still better than the next? here is the issue ....
                            //my solution
                            wordToTest = oldWord;//future loops will now check this one
                            
                        }
                        else if(dict.get(wordToTest) == dict.get(oldWord))
                        {
                            //another tie breaking
                            if(SpellEx.compare(wordToTest, oldWord) > 0)
                            {
                                //new word comes first
                                //new word is a better solution
                                answer.remove(oldWord);
                                answer.add(wordToTest);
                                //but now! what if old word is still better than the next? here is the issue ....
                                //my solution
                                wordToTest = oldWord;//future loops will now check this one
                            }
                            else if(SpellEx.compare(wordToTest, oldWord) == 0)
                            {
                                //idk, theyre the same word
                                //nothing happens but uhhh, probably a problem
                                continue;
                            }
                        }
                        //if neither, leave current word
                        
                    }
                    //if neither, leave current word
                    
                }
                
            }
        }
        
        
        
        return answer;
    }
    
    /**
     * Returns the set of n most frequent words in the dictionary to occur with
     * edit distance distMax or less compared to the given word. Ties in
     * max frequency are broken with ascending alphabetic order.
     * @param word The word to compare to those in the dictionary
     * @param n The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested / returned 
     * words from the dictionary can stray from the given word
     * @return The set of n suggested words from the dictionary with edit distance
     * distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance (String word, int n, int distMax) {
        //throw new UnsupportedOperationException();
        
        //create all possible outputs with edit distance distMax from word (and less)
        Set<String> possible = possiblePermutations(word, distMax);

        //remove bad values from possible until it is the right size
        //honestly, I should go back and refactor previous code with this algorithm (idk why I didn't do it before, i'm dumb)
        //way less spaghetti-y but also... i have a circuits quiz tomorrow and i need to study, sorry :/
        // <3
        while(possible.size() > n)
        {
            int smallestFreq = Integer.MAX_VALUE; String lowestWord = new String();
            //instead of adding to another, i will remove lowest values one-by-one
            
            for(String possibleWord : possible)
            {
                if(dict.get(possibleWord) < smallestFreq)
                {
                    smallestFreq = dict.get(possibleWord);
                    lowestWord = possibleWord;
                }
            }
            
            possible.remove(lowestWord);
            
        }
        
        return possible;
        
    }
    
    private Set<String> possiblePermutations(String word, int distMax)
    {
        //recursive time
        //permute words in all the possible ways
        //subtract one and pass each of those permutations into a recursive call
        //return all in one bit set
        
        Set<String> permutations = new HashSet<String>();
        
        
        
        if(distMax == 0)
        {
            //base case
            return permutations;
        }
        else
        {
            //the word now might even be in it :)
            if(dict.containsKey(word)) permutations.add(word);
            
            //do 4 possible permutations (this is harder than it looks, grr)
            
            /*
             * insertion
             * deletion
             * transposition
             * replacement
             */
            
           
            //insertion
            String temp = new String();
            for(char i = 'a'; i <= 'z'; i++)
            {
                for(int j = 0; j <= word.length(); j++)
                {
                    temp = word.substring(0, j) + String.valueOf(i) + word.substring(j, word.length());
                    permutations.addAll(possiblePermutations(temp, distMax-1));
                    if(dict.containsKey(temp))
                    {
                        permutations.add(temp);
                    }
                }
            }
            
            //deletion
            for(int j = 0; j <= word.length(); j++)
            {
                if(j == word.length())
                {
                    temp = word.substring(0, word.length());
                }
                else
                {
                    temp = word.substring(0, j) + word.substring(j+1);
                }
                permutations.addAll(possiblePermutations(temp, distMax-1));
                if(dict.containsKey(temp))
                {
                    permutations.add(temp);
                }
            }
            
            //replacement
            for(char i = 'a'; i <= 'z'; i++)
            {
                for(int j = 0; j <word.length(); j++)
                {
                    temp = word.substring(0, j) + String.valueOf(i) + word.substring(j+1, word.length());
                    permutations.addAll(possiblePermutations(temp, distMax-1));
                    if(dict.containsKey(temp))
                    {
                        permutations.add(temp);
                    }
                }
            }
            
            //transposition
            for(int j = 1; j < word.length(); j++)
            {
                //swap
                char stringAsArray[] = word.toCharArray();
                char tempChar = stringAsArray[j-1];
                stringAsArray[j-1] = stringAsArray[j];
                stringAsArray[j] = tempChar;
                
                permutations.addAll(possiblePermutations(temp, distMax-1));
                
                //check and add
                if(dict.containsKey(temp))
                {
                    permutations.add(temp);
                }
            }
            
            
            return permutations;
            
        }
    }
    
}

