package t3;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Interactive game for T3 that provides UI, IO, and prompts both
 * player and agent for their action choices as the game progresses.
 * You can run this to test your agent, or play against it!
 */
public class T3Game {
    
    public static void main (String[] args) {
        Scanner input = new Scanner(System.in);
        
        // Game State & Agent Setup:
        T3Player ai = new T3Player();
        //to save time, the first move is always '1' so... just do it for them !
        int[][] state_start = {
                {1, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
            };
        T3State state = new T3State(true, state_start);
        T3Action act;
        boolean playersTurn = true;
        
        int[][][] possible_states = {
        		{
        			{0, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{2, 1, 0},
                    {0, 0, 0},
                    {0, 0, 6}
        		},
        		{
        			{0, 0, 0},
                    {0, 5, 0},
                    {0, 0, 0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		{
        			{1, 0, 0},
        			{0, 0, 0},
        			{0, 0 ,0}
        		},
        		
        		
        };
        
        for(int i = 0; i <= 0;i++) {//possible_states.length; i++) {
	        System.out.println("================================");
	        System.out.println("=              T3              =");
	        System.out.println("================================");
	        System.out.println("Test #: " + i);
	        state = new T3State(true, possible_states[i]);
	
	        // The below comment is proof that Dr. Forney originally had a different game in mind ;)
	        /* 
	        	// Continue to pull stones as long as there are some remaining
	         */
	        while (!(state.isTie() || state.isWin())) {
	            System.out.println(state);
	            
	            // Player's turn (but I actually made it the AI to test)
	            if (playersTurn) {
	            	/*
	            	 * I'm too lazy to play this game
	            	 * So I make the computer fight itself :)
	                System.out.println("Enter three space-separated numbers in format: COL ROW NUMBER ");
	                System.out.print("[Player's Turn: Move Options " + Arrays.toString(state.getMoves()) + "] > ");
	                String playerAct = input.nextLine();
	                int[] parsedAct  = Stream.of(playerAct.split(" ")).mapToInt(Integer::parseInt).toArray();
	                act = new T3Action(parsedAct[0], parsedAct[1], parsedAct[2]);
	                if (parsedAct.length != 3 || !state.isValidAction(act)) {
	                    System.out.println("[X] Improper move, l2p. Try again.");
	                    continue;
	                    
	                }
	                */
	            	
	            	act = ai.choose(state);
	            	System.out.println("[Friendly Turn] > " + act);
	            
	            // Agent's turn
	            } else {
	                act = ai.choose(state);
	                System.out.println("[Opponent's Turn] > " + act);
	            }
	            
	            System.out.println("End turn");
	            //String playerAct= input.nextLine();
	            state = state.getNextState(act);
	            playersTurn = !playersTurn;
	        }
	        
	        System.out.println(state);
	        System.out.println(state.isWin() ? (playersTurn ? "[L] You Lose!" : "[W] You Win!") : "[T] Tie Game!");
	        
        }
        input.close();
    }

}
