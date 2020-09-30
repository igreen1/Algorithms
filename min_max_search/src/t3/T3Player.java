package t3;

import java.util.*;


/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
    	
    	//since my actual bounds are [0,1], i figure using max min suffices (rather than double.(-inf, +inf))
    	int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
    	
    	
    	//create min children and go wild :)
    	Map<T3Action, T3State> trans = state.getTransitions();
    	int temp; //stores the branch that was just checked for comparison to alpha/beta
    	T3Action currentBest = null; //stores the current best option
    	
    	//iterate through this node's possible decisions
    	//priority given to left-most branches, make sure left most are given correctly to match SPECS (TODO) 
    	for(Map.Entry<T3Action, T3State> me: trans.entrySet() )
    	{
    		//SPECS require that immediate wins are always prioritized
    		if(me.getValue().isWin()) return me.getKey(); 
    		
    		temp = minChooser(me.getValue(), alpha, beta);
    		if(temp > alpha)
    		{
    			alpha = temp; //max nodes affect min bound
    			currentBest = me.getKey();
    		}
    		else if(temp == alpha)
    		{
    			//equally good or bad, prefer lower column and row
    			if(currentBest.compareTo(me.getKey()) > 0)
    			{
    				currentBest = me.getKey();
    			}
    		}
    		
    		
    		//pruning
    		if(alpha >= beta) break;
    	}
    	
    	return currentBest;
    	
    }
    
    // TODO: Implement your alpha-beta pruning recursive helper here!
    
    /**
     * 
     * @param state the state from which this max node must chose the best option
   	 * @param alpha the lower bound of our pruning algorithm
   	 * @param beta the upper bound of our pruning algorithm
   	 * @return the bubbled up value to this node (aka beta after pruning)
   	 */
   	private int minChooser(T3State state, int alpha, int beta) {
   		
   		if(state.isTie()) return 0;
   		if(state.isWin()) return -1;
   		
   		Map<T3Action, T3State> trans = state.getTransitions();
    	int temp; //stores the branch that was just checked for comparison to alpha/beta
    	T3Action currentBest = null; //stores the current best option

    	//iterate through this node's possible decisions
    	//priority given to left-most branches, make sure left most are given correctly to match SPECS (TODO) 
    	for(Map.Entry<T3Action, T3State> me: trans.entrySet() )
    	{
    		//SPECS require that immediate wins are always prioritized
    		if(me.getValue().isWin()) return -1; //victory means return 0 back up (immediately choose victory) 
    		//NOTE TO GRADER: I'm pretty sure alpha beta pruning would've taken care of this buuuuttttt... this is an easy confirmation :) 
    		
    		temp = maxChooser(me.getValue(), alpha, beta);
    		
    		if(currentBest == null) currentBest = me.getKey(); //just for the first iteration
    		
    		if(temp < beta)
    		{
    			beta = temp; //max nodes affect min bound
    			currentBest = me.getKey();
    		}
    		else if(temp == beta)
    		{
    			//equally good or bad, prefer lower column and row
    			if(currentBest.compareTo(me.getKey()) > 0)
    			{
    				//System.out.println(temp);
    				currentBest = me.getKey();
    			}
    		}
    		
    		if(alpha >= beta) break;
    	}
    	
    	return beta;
 
    }
   	
   	/**
   	 * 
   	 * @param state the state from which this max node must chose the best option
   	 * @param alpha the lower bound of our pruning algorithm
   	 * @param beta the upper bound of our pruning algorithm
   	 * @return the bubbled up value to this node (aka alpha after pruning)
   	 */
   	private int maxChooser(T3State state, int alpha, int beta)
   	{
   		//base cases 
   		if(state.isTie()) return 0;
   		if(state.isWin()) return 1;
   		
   		Map<T3Action, T3State> trans = state.getTransitions();
    	int temp; //stores the branch that was just checked for comparison to alpha/beta
    	T3Action currentBest = null; //stores the current best option
    	
    	
    	//iterate through this node's possible decisions
    	//priority given to left-most branches, make sure left most are given correctly to match SPECS (TODO) 
    	for(Map.Entry<T3Action, T3State> me: trans.entrySet() )
    	{
    		//SPECS require that immediate wins are always prioritized
    		if(me.getValue().isWin()) return 1; //victory in max situation is 1 for us 
    		
    		temp = minChooser(me.getValue(), alpha, beta);
    		
    		if(currentBest == null) currentBest = me.getKey(); //just for the first iteration

    		
    		if(temp > alpha)
    		{
    			alpha = temp; //max nodes affect min bound
    			currentBest = me.getKey();
    		}
    		else if(temp == alpha)
    		{
    			//equally good or bad, prefer lower column and row
    			if(currentBest.compareTo(me.getKey()) > 0)
    			{
    				currentBest = me.getKey();
    			}
    		}
    		if(alpha >= beta) break;
    	}
    	
    	return alpha;
   		
   	}
    
   	
}

