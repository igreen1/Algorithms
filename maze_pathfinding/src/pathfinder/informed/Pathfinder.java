package pathfinder.informed;

import java.util.*;



/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    
	/*
	public static void main(String args[])
	{
		String[] maze = {
	            "XXXXXXX",
	            "XI....X",
	            "X.MMM.X",
	            "X.XKXGX",
	            "XXXXXXX"
	        };
	        MazeProblem prob = new MazeProblem(maze);
	        ArrayList<String> solution = Pathfinder.solve(prob);
	        
	        // result will be a 2-tuple (isSolution, cost) where
	        // - isSolution = 0 if it is not, 1 if it is
	        // - cost = numerical cost of proposed solution
	        int[] result = prob.testSolution(solution);
	        System.out.println(solution);
		
	}*/
	
	
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve (MazeProblem problem) {
    	
    	//first find the key-state
    	Set<MazeState> goals = new HashSet<MazeState>();
    	goals.add(problem.KEY_STATE);
    	
    	
    	//eclipse gets angry if i move these into if-else
    	SearchTreeNode ansKey;
    	ArrayList<String> solnToKey = null;
    	
    	if(problem.KEY_STATE == null) 
    	{
    		
    		return null; //if you comment out this, 
    		
    	}
    	else {
    		ansKey = solveHelper(problem.INITIAL_STATE, goals, problem);
    		if(ansKey == null)
        	{
        		//no way to get to key
    			//so no solution :(
    			return null;
        	}
    		solnToKey = createSolution(ansKey); 
    	}
    	//this could be done WITH the algorithm, but its easier to understand separately and efficiency isn't that close-cut :)
    	
    	//search from the key-state to goal
    	SearchTreeNode ans2GoalKey = solveHelper(ansKey.state, problem.GOAL_STATES, problem);
    	
    	//solve the goal-state, store solution. IF EMPTY, no solution yeet outta here
    	ArrayList<String> solnToGoal = createSolution(ans2GoalKey);
    	
    	//check if goal possible
    	if(ans2GoalKey == null) {
    		return null;
    	}
    	
    	ArrayList<String> fullSoln; //if declared in the if-else, eclipse gets angry
    	
    	if(solnToKey != null)
    	{
	    	fullSoln = solnToKey;
	    	fullSoln.addAll(solnToGoal);
    	}
    	else
    	{
    		fullSoln = solnToGoal;
    	}
    	
    	return fullSoln;
    	
    }
 
   
     //Copied from my past homework
    private static ArrayList<String> createSolution(SearchTreeNode current)
    {
    	ArrayList<String> subSoln = new ArrayList<>();
    	if(current == null) return subSoln;
    	if (current.parent == null) {
    		return subSoln;
    	} else {
    		subSoln.addAll(createSolution(current.parent));
    		subSoln.add(current.action);
    		return subSoln;
    		
    	}
    }

    public static SearchTreeNode solveHelper(MazeState initial, Set<MazeState> goals, MazeProblem problem)
    {
    	
    	//create frontier
    	PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>();
    	
    	//create graveyard
    	HashSet <MazeState> graves = new HashSet<MazeState>();
    	
    	boolean foundSolution = false;
    	
    	frontier.add( new SearchTreeNode(initial, null,null,0,0) );
    	graves.add(initial);
    	
    	SearchTreeNode curr;
    	
    	do
    	{
    		curr = frontier.poll();
    		//check current
    		for(MazeState i : goals)
    		{
	    		if(curr.state.equals(i)) {
	    			foundSolution = true;
	    			break;
	    		}
    		}
    		if(foundSolution)break; //have to double break!
    		
    		graves.add(curr.state); //its been tested now :)
    		
    		//current isn't it :(
    		//expand children
    		
    		Map<String, MazeState> trans = problem.getTransitions(curr.state);
    		
    		for(Map.Entry<String, MazeState> me: trans.entrySet() )
    		{
    			if(!graves.contains(me.getValue()))
    			{
    				//add child to queue
    				//calculate fn
    				// fn = gn + hn, gn = gn, past + cost(curr)
    				int gn = curr.gn + problem.getCost(me.getValue()); //store seperately to keep the manhattan from corrupting the info :)
    				double fn = gn + getHn(me.getValue(), goals);
    				//add em :)
    				frontier.add(new SearchTreeNode(me.getValue(),me.getKey(),curr,gn,fn));
    			}
    			else
    			{
    				continue; //not necessary, implicitly done but I like being explicit
    			}
    		}            
    		
    	}while(!foundSolution && !frontier.isEmpty());
    	
    	if(foundSolution) {
    		return curr;
    	}else
    	{
    		return null;
    	}
    	
    	
    }
    
    //A* heurisitc
    static double getHn(MazeState ms, Set<MazeState> goals)
    {
    	
    	ArrayList<Double> dist = new ArrayList<>();
    	double temp =0;
    	
    	//check if this is goal
    	for(MazeState i : goals)
    	{
    		temp =  Math.sqrt( Math.pow(ms.row - i.row, 2) + Math.pow(ms.col - i.col, 2) );
    		dist.add(temp);
    	}    	
    	
    	//return the shortest distance since this is the best possible
    	//with heuristic always wanna underestimate
    	return Collections.min(dist);
    	
    }
    
}


/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 * [!] NOTE: Feel free to change this however you see fit to adapt your solution 
 *     for A* (including any fields, changes to constructor, additional methods)
 */
class SearchTreeNode implements Comparable<SearchTreeNode>{
    
    MazeState state;
    String action;
    SearchTreeNode parent;
    int gn; //past cost, stored for children :)
    double fn; //priority for queue, defined later!
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     *
     */
    SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int gn, double fn) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.gn = gn;
        this.fn = fn;
        
    }
    
    
    public int compareTo(SearchTreeNode that)
    {
    	return (int) (this.fn - that.fn);
    }
    
    
    
}