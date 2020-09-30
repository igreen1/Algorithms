package csp;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.*;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some set of unary and binary 
 * constraints on the dates of each meeting.
 */
public class CSP {

    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        /**
         * Psuedo code :)
         * Make a state variable nmeetings big
         * set each state recursively, exiting once successful 
         * if reach untenable state backtrack until good again
         * states selected domain
         * for NOW domain = range
         */
        
        //Need to store meetings and their respective domains.
        //but most dictionaries are order-less and the meetings are necessarily ordered
        //so instead, I will use two data structures
        List<Set<LocalDate>> domains = createDomains(nMeetings, rangeStart, rangeEnd);
        if(domains == null)
        {
            return null;    //impossible date range (end before start) so no valid domains
        }
        
        if(constraints != null) {
            //now domain pruning! using only unary constraints
            domains = nodeConsistency(domains, constraints);
            //now do some AC3 domain constraining :)
            AC3(domains, constraints);
        }
        
        //Preprocessing could possibly create an impossible solution > Great! return null
        if(domains == null||domains.size() != nMeetings) 
        {
            return null;
        }
        
        
        return backTracking( domains, constraints);
    }
    
    /**
     * Performs a backtracking algorithm on the domains and constraints given. This function, in comparison to 
     *  backTrackingRecursive is just a wrapper than initializes a solution (otherwise, nullpointer errors)
     * @param domains the domains of each variable. Size of this is the number of meetings
     * @param constraints the constraints placed on our meetings
     * @return a LIST of dates, ordered by meeting, for a possible meeting time arrangement
     */
    private static List<LocalDate> backTracking( List<Set<LocalDate>> domains, Set<DateConstraint> constraints)
    {
        //Had to seperate this since I kept getting nullpointer exceptions so f it, do the first more explicitly
        List<LocalDate> solution = new ArrayList<LocalDate>(); //put size in arrayList<>(SIZE) ? depends on forney response
        
        solution = backTrackingRecursive(solution, domains, constraints);
        
        return solution;
    }
    
    /**
     *  The actual engine of backtracking, this bad boy will take a given state, domain, and constraints and 
     *      return a valid solved state based on the input state or NULL if the input state is garbage
     * @param state the state to manipulate to find another solution
     * @param domains the possible values for meetings times
     * @param constraints the constraints placed on the meetings (since Becky can't be in the room with Becca)
     * @return a LIST of localdates, ordered by meeting number, (aka our solution)
     */
    private static List<LocalDate> backTrackingRecursive(List<LocalDate> state, List<Set<LocalDate>> domains, Set<DateConstraint> constraints)
    {
        //base case!
        //NOTE: to get to this point, the state must be valid. So if its a full size, it is valid and full! we're done
        if(state.size() == domains.size())
        {
            return state;
        }
        else
        {
            //find what element we are on
            //Forney told me that I could just use the next index instead of MRV/LCV stuff here
            //it's still very efficient, no worries
            int n = state.size();
            List<LocalDate> temp;
            for(LocalDate possibleDay: domains.get(n))
            {
                state.add(possibleDay);
                if(isValidState(state, constraints))
                {
                    //worth a try :)
                    
                    temp = backTrackingRecursive(state, domains, constraints);
                    if(temp != null)
                    {
                        //yooo, it actually worked
                        return temp;
                    }
                    
                }
                state.remove(n);
            }
            
            return null; //backtrack, this branch is worthless, sigh
        }
        
    }
    
    /**
     * Scalped straight from forney :) tests if solution state is valid for constraints 
     * I made my own but then I thought it was erroneous (it wasn't but I already copied this so whatever)
     * @param soln - possible state to test
     * @param constraints - constraints on the problem
     * @return true is valid state ; false if invalid state
     */
    private static boolean isValidState (List<LocalDate> soln, Set<DateConstraint> constraints) {
        for (DateConstraint d : constraints) {
            
            //but, since may not be full solution, check bounds
            //1. if solution doesn't contain LVAL, get out of here
            //2. for binary constraint, if dones't contain RVAL get out of here
            if( soln.size() <= d.L_VAL || ( d.arity() == 2) && ((BinaryDateConstraint) d).R_VAL >= soln.size() )
            {
                continue;
            }
            
            //Checking these constraints is valid
            //So, here, I just copied forney's code entirely
            // except it returns false instead of failing (duh)
            LocalDate leftDate = soln.get(d.L_VAL),
                      rightDate = (d.arity() == 1) 
                          ? ((UnaryDateConstraint) d).R_VAL 
                          : soln.get(((BinaryDateConstraint) d).R_VAL);
            
            boolean sat = false;
            switch (d.OP) {
            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
            }
            if (!sat) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Creates a list containing the domain for each variable WITHOUT knowing constraints based on start and end
     * Really, I could just do this in solve but trying to really subdivide tasks here. INCLUDES rangeStart and end
     * @param n number of domains to create
     * @param rangeStart start of domain
     * @param rangeEnd end of domain
     * @return list of domains (sets) for each n variable
     */
    private static List<Set<LocalDate>> createDomains(int n, LocalDate rangeStart, LocalDate rangeEnd)
    {
        
        //Check if our range even makes sense 
        //this will stop any weird answers later (like an infinite loop... as a hypothetical example)
        if(rangeStart.compareTo(rangeEnd) > 0)
        {
            return null;
        }
        
        //Create the domain variable
        List<Set<LocalDate>> domains = new ArrayList<Set<LocalDate>>();
        
        //This for loop
        for(int i = 0; i < n; i++)      
        {
            domains.add(new HashSet<LocalDate>());  //first, create the domain for nth varaible
            //now for each value, iterate from rangeStart til end and add it to the hashset
            
            
            //go from rangestart to rangeend, inclusive!!
            for(LocalDate current = rangeStart; current.isBefore( rangeEnd.plusDays(1)); current = current.plusDays(1))
            {
                domains.get(i).add(current);
            }
        }
        
        return domains;
    }
    
    
    /**
     * Performs the AC3 algorithm on the given domain and constraints (preprocessing)
     * @param domains the domains to be pruned like a fine rose
     * @param constraints the constraints placed on the domain
     * @return the domains, with invalid states removed from them
     */
    private static List<Set<LocalDate>> AC3(List<Set<LocalDate>> domains, Set<DateConstraint> constraints)
    {
        //Remove bad values from domain
        
        //Put all my arcs in a queue to start :) 
        Set<BinaryDateConstraint> biC = filterConstraintsToBinary(constraints);
        //Okay so a set might be better but have you TRIED popping an element from a set?
        //  the minor loss in efficiency created by double iterations is probably worth getting rid of my headache
        Queue<Arc> q = createInitialArc(biC);
        
        Arc current;        
        while( (current = q.poll()) != null)
        {
            
           if(arcConsistent(domains, current))
           {
               //we made a change :)
               //I can't tell if it will change (by reference?) so ... let's find out!
               if(domains.get(current.l).isEmpty()) {
                   return null; //this will get caught since no solution!
               }
               else
               {
                   //add all adjacent arcs, requires going through the whole list again
                   q.addAll(addAdjacent(biC, current));
               }
           }
        }
        return domains;
    }
    
    
    /**
     * A helper function for AC3. This will add ARCS adjacent to an input arc (called if current arc is creates
     *  a change in tail domain)
     * @param constraints the constraints that describe neighbors
     * @param current the arc that ruffled some feathers in the domain
     * @return a new set of Arcs to AC3 check
     */
    private static Queue<Arc> addAdjacent(Set<BinaryDateConstraint> constraints, Arc current)
    {
        Queue<Arc> adjacent = new LinkedList<Arc>();
        
        for(BinaryDateConstraint c: constraints)
        {
            if(c.L_VAL == current.l)
            {
                adjacent.add(new Arc(c.R_VAL, c.L_VAL ,invertOp(c.OP)));
            }
            else if(c.R_VAL == current.r)
            {
                adjacent.add(new Arc(c.L_VAL, c.R_VAL, c.OP));
            }
            
        }
        
        return adjacent;
    }
    
    /**
     * Check if the given arc is consistent (checks if tail domain has valid states in head domain given ARC constraint)
     * @param domains the domains in question
     * @param current the arc currently being examined
     * @return true if consistent; false if not consistent
     */
    private static boolean arcConsistent(List<Set<LocalDate>> domains, Arc current)
    {
        
        boolean madeChange = false; //not yet!
        //check if any value of tail 
        //get iterator
        Iterator<LocalDate> iter = domains.get(current.l).iterator();
        
        while(iter.hasNext())
        {
            
            LocalDate leftDay = iter.next();
            boolean possible = false;
            
            for(LocalDate rightDay: domains.get(current.r))
            {
                
                if(doBooleanComparison(leftDay, rightDay, current.op))
                {
                    possible = true;
                    break;
                }
                
            }
            if(!possible)
            {
                //remove left day and enqueue the inverse op
                iter.remove();
                madeChange = true;
                
            }
            
        }
        
        return madeChange;
    }
    
    /**
     * Checks if (left OP right) is a true statement. Honestly pretty straightforward, just does boolean
     *  algebra but we have strings not operands so I need a switch statement 
     * @param left left value of boolean expression
     * @param right right value of boolean expression
     * @param op the boolean operation
     * @return boolean (duh) true if expression is true; false if expression if false
     */
    private static boolean doBooleanComparison(LocalDate left, LocalDate right, String op)
    {
        switch(op)
        {
        case "==":
            if(left.equals(right))
            {return true;}
            break;
        case "!=":
            if(!left.equals(right))
            {return true;}
            break;
        case ">":
            if(left.isAfter(right))
            {return true;}
            break;
        case "<":
            if(left.isBefore(right)) 
            {return true;}
            break;
        case "<=":
            if(left.isBefore(right) || left.isEqual(right)) 
            {return true;}
            break;
        case ">=":
            if(left.isAfter(right) || left.isEqual(right)) 
            {return true;}
            break;
        default:
            throw new UnsupportedOperationException();
        }
        return false;
    }
    
    /**
     * Creates a queue of arcs based on neighboring constraints. For each neighbor, creates  2 arcs
     *   1 for left -> right, one for right -> left
     * @param constraints the constraint graph (but as a set cause graphs are hard haha)
     * @return the set of arcs created by this constraint graph
     */
    private static Queue<Arc> createInitialArc(Set<BinaryDateConstraint> constraints)
    {
        Queue<Arc> q = new LinkedList<>();
        
        for(BinaryDateConstraint c: constraints)
        {
            //Bidirectional constraints
            q.add(new Arc(c.L_VAL, c.R_VAL, c.OP));
            q.add(new Arc(c.R_VAL, c.L_VAL, invertOp(c.OP)));
        }
        
        return q;
    }
    
    /**
     * Returns !(operator) for a simple boolean operator EXCEPT for == and !=
     *  those are symmetric properties (in fact I think I learned that in Proofs) so just return those
     * @param op the operator to flip
     * @return the flipped operator
     */
    private static String invertOp(String op)
    {
        switch(op)
        {
        case "==":
            return "==";
        case "!=":
            return "!=";
        case "<":
            return ">=";
        case ">":
            return "<=";
        case ">=":
            return "<";
        case "<=":
            return ">";
        }
        return null; //idk, sorry yall :) break the program so there's at least a reason
    }
    
    /**
     * Slowly removed from my code this now serves only to make my AC3 easier. it removes unary constraitns
     *  because they are only useful in checkings states and initial preprocessing, not AC3
     * @param constraints the set of ALL constraints
     * @return a set of only BinaryConstraints (sub set of input)
     */
    private static Set<BinaryDateConstraint> filterConstraintsToBinary(Set<DateConstraint> constraints)
    {
        //Just makes my life easier honestly
        Set<BinaryDateConstraint> bdc = new HashSet<BinaryDateConstraint>();
        for(DateConstraint c: constraints)
        {
            if(c.arity() == 2)
            {
                bdc.add((BinaryDateConstraint)c);
            }
        }
        return bdc;
    }
    
    /**
     * Assuming domains is properly setup for n meetings in range, will remove any domains outside of constraints 
     *  This is node consistency in specs so uunary only by definition
     * @param domains the domains to prune
     * @param UNARY!!! (I split them elsewhere) constraints the values which a node cannot store 
     * @return The domains, with improper values removed (this could be a pass by reference style function but it gets funky in Java)
     */
    private static List<Set<LocalDate>> nodeConsistency(List<Set<LocalDate>> domains, Set<DateConstraint> constraints)
    {
                
        //Doing this in the loop was causing my java to bug out so whatever,
        //  might waste a tiny bit of space but shouldn't matter
        Iterator<LocalDate> iter;
       
            //for each domain
            //check it against all constraints
        for(DateConstraint c_uncast: constraints)
        {
            //node consistency only on unary constraints
            if(c_uncast.arity() == 1) {
                UnaryDateConstraint c = (UnaryDateConstraint)c_uncast;
            
                //If i don't put it in my else statement java gets mad
                //EVEN though the logic works cause of the continue smh
                //skip non-unary constraints :)
                switch(c.OP)
                {
                case "==": 
                    //Only valid domain is this value ! (R_VAL)
                    //set domain of L_VAL to R_VAL
                    if(domains.get(c.L_VAL).contains(c.R_VAL)) //If this assignment is valid 
                    {
                        
                        domains.get(c.L_VAL).clear();
                        domains.get(c.L_VAL).add(c.R_VAL);
                    }
                    else {
                        //Improper constraints?? Idk return null and get outta here
                        return null;
                    }
                    break;
                    
                case "!=": 
                    //Remove specifically this number from domain (IF present; otherwise, already good)
                    if(domains.get(c.L_VAL).contains(c.R_VAL))
                    {
                        domains.get(c.L_VAL).remove(c.R_VAL);
                    }
                    break;
                    
                case ">":
                    //Go through each value, remove any value that occurs before R_VAL
                    //I stole this iterator from stack overflow because mine kept throwing errors
                    //  because I was iterating and editing at the same time. Also copied below in spirit
                    iter = domains.get(c.L_VAL).iterator();
                    while(iter.hasNext())
                    {
                        LocalDate ele = iter.next();
                        if(ele.isBefore(c.R_VAL) || ele.equals(c.R_VAL))
                        {
                            iter.remove();
                        }
                    }
                    
                    break;
                    
                case "<":
                    //Copied from above
                    //Go through each value and remove any that occur after R_VAL
                    iter = domains.get(c.L_VAL).iterator();
                    while(iter.hasNext())
                    {
                        LocalDate ele = iter.next();
                        if(ele.isAfter(c.R_VAL) || ele.equals(c.R_VAL))
                        {
                            iter.remove();
                        }
                    }
                    break;
                case "<=":
                    iter = domains.get(c.L_VAL).iterator();
                    while(iter.hasNext())
                    {
                        LocalDate ele = iter.next();
                        //if ! (ele > r_val) <=> ele <= r_val
                        if((ele.isAfter(c.R_VAL)))
                        {
                            iter.remove();
                        }
                    }
                    break;
                case ">=":
                    iter = domains.get(c.L_VAL).iterator();
                    while(iter.hasNext())
                    {
                        LocalDate ele = iter.next();
                        if(ele.isBefore(c.R_VAL))
                        {
                            iter.remove();
                        }
                    }
                    break;
                }
                
            }
        
        }
        
        return domains;
    }

  
    
}
