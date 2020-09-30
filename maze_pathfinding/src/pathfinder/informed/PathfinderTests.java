package pathfinder.informed;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness and
 * optimality.
 */
public class PathfinderTests {
	
	 // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite, plus, tests to make sure
    // you're not implementing anything too computationally
    // crazy
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
    // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    
    // =================================================
    // Unit Tests
    // =================================================
    
    
    // Pity tests
    // -------------------------------------------------
    @Test
    public void testInit_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        // No errors, yeah?
    }
    
    @Test
    public void testInit_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        // Not checking if it's correct but... no errors still?
    }

    // Test cases with solutions
    // -------------------------------------------------
    @Test
    public void testPathfinder_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // Making this MazeProblemSolution object simply makes sure
        // any errors in your MazeProblem (especially those
        // potentially introduced by its testSolution method) do not
        // subject you to double jeopardy
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        // result will be a 2-tuple (isSolution, cost) where
        // - isSolution = 0 if it is not, 1 if it is
        // - cost = numerical cost of proposed solution
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]); // Test that result is a solution
        assertEquals(6, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(14, result[1]);
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(10, result[1]);
    }
    
    @Test
    public void testPathfinder_t3() {
        String[] maze = {
            "XXXXX",
            "XGIKX",
            "XXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(3, result[1]);
    }
    
    @Test
    public void testPathfinder_t4() {
        String[] maze = {
            "XXXXXXX",
            "X.GI..X",
            "X...KGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(3, result[1]);
    }
    
    @Test
    public void testPathfinder_t5() {
        String[] maze = {
            "XXXXXXX",
            "X..IX.X",
            "X..KXGX",
            "X...X.X",
            "XG....X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(5, result[1]);
    }
    
    @Test
    public void testPathfinder_t6() {
        String[] maze = {
            "XXXXXXX",
            "X..IX.X",
            "X..KXGX",
            "XMM.X.X",
            "XGM...X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(7, result[1]);
    }
    
    @Test
    public void testPathfinder_t7() {
        String[] maze = {
            "XXXXXXX",
            "X..IXKX",
            "X...XMX",
            "XXX.XGX",
            "XGX...X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(14, result[1]);
    }
    
    @Test
    public void testPathfinder_t8() {
        // Play time's over
        String[] maze = { 
          //           11111111112
          // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "XI.X....M....X..MKX.X", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X.........M...X", // 4
            "XX.......XX.XXMXXMM.X", // 5
            "XGXXX.MM....X...X...X", // 6
            "X...X.MM....M.XX..XXX", // 7
            "XXM.X..MMMMMX..M..X.X", // 8
            "XGX........GX..M...GX", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(36, result[1]);
    }
    
    @Test
    public void testPathfinder_t9() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.X.X", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X....I.XG.M...X", // 4
            "XX.......XXXXXMXXMM.X", // 5
            "X.XXXGMM...KXGX.X...X", // 6
            "X...X.MM....MXXX..XXX", // 7
            "XXM.X..MMMMMX..M..X.X", // 8
            "X.X........GX..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(13, result[1]);
    }
    
    @Test
    public void testPathfinder_t10() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.X.X", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X....I.XG.M...X", // 4
            "XX.......XXXXXMXXMM.X", // 5
            "X.XXXGMM...KXGX.X...X", // 6
            "X...X.MM....MXXX..XXX", // 7
            "XXM.X..MMMMMX..M..X.X", // 8
            "X.X........GX..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(13, result[1]);
        
        // You're not doing anything funky, are you? Global vars?
        // Modifying the maze?
        ArrayList<String> solutionAgain = Pathfinder.solve(prob);
        int[] resultAgain = solnProb.testSolution(solutionAgain);
        assertEquals(1, resultAgain[0]);
        assertEquals(13, resultAgain[1]);
    }
    
    @Test
    public void testPathfinder_t11() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.X.X", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X....I.XG.M...X", // 4
            "XX.......XXXXXMXXMM.X", // 5
            "X.XXXGMM...KXGX.X...X", // 6
            "X...X.MM....MXXX..XXX", // 7
            "XXM.X..MMMMMX..M..X.X", // 8
            "X.X........GX..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        MazeProblemSolution solnProb = new MazeProblemSolution(maze);
        int[] result = solnProb.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(13, result[1]);
        
        // Also, you weren't trying to pull a fast one with your
        // solution test, were you? I will expose your lies!
        int[] yourMazeProblemResult = prob.testSolution(solution);
        assertEquals(result[0], yourMazeProblemResult[0]);
        assertEquals(result[1], yourMazeProblemResult[1]);
    }
    
    
    // Test cases *without* solutions
    // -------------------------------------------------
    @Test
    public void testPathfinder_nosoln_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
    @Test
    public void testPathfinder_nosoln_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.M.MGX",
            "X.X.X.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // Note the spec:
        //   - A valid maze may have *at most* 1 key
        //   - All solutions must have a path to the key
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_nosoln_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.M.MXX",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_nosoln_t3() {
        String[] maze = {
            "XXXXXXX",
            "XI..XKX",
            "X.M.X.X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_nosoln_t5() {
        String[] maze = {
            "XXXXXXX",
            "XIXGXKX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_nosoln_t6() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.X.X", // 1
            "X...X..M.M..MX.MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X....I.XG.M...X", // 4
            "XX.......XXXXXMXXMM.X", // 5
            "X.XXX.MM...KXGX.X...X", // 6
            "X...X.MM....MXXX..XXX", // 7
            "XXM.X..MMMMMX..M..X.X", // 8
            "X.X.........X..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    // BIIIIG Tests
    // -------------------------------------------------
    
    /**
     * Gross method that builds a huge maze for the given initial, key, and goal
     * states
     * @param size The square dimension of the maze
     * @param initial The initial state
     * @param key The key state
     * @param goals A set of goal states
     * @return
     */
    public String[] buildBigMaze (int size, MazeState initial, MazeState key, Set<MazeState> goals) {
        String[] result = new String[size];
        for (int r = 0; r < size; r++) {
            char[] row = new char[size];
            for (int c = 0; c < size; c++) {
                if (c == 0 || c == size-1 || r == 0 || r == size-1) {
                    row[c] = 'X';
                } else {
                    row[c] = '.';
                }
                MazeState m = new MazeState(r, c);
                if (m.equals(initial)) {
                    row[c] = 'I';
                }
                if (m.equals(key)) {
                    row[c] = 'K';
                }
                if (goals.contains(m)) {
                    row[c] = 'G';
                }
            }
            
            result[r] = new String(row);
        }
        return result;
    }
    
    @Test
    public void testPathfinder_bigboi_t0() {
        int size = 1000;
        MazeState init = new MazeState(1,1),
                  key  = new MazeState(999, 1);
        HashSet<MazeState> goals = new HashSet<>();
        goals.add(new MazeState(1, 999));
        
        MazeProblem prob = new MazeProblem(buildBigMaze(size, init, key, goals));
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(2994, result[1]);
    }
    
    @Test
    public void testPathfinder_bigboi_t1() {
        int size = 1000;
        MazeState init = new MazeState(1,1),
                  key  = null;
        HashSet<MazeState> goals = new HashSet<>();
        goals.add(new MazeState(1, 999));
        
        MazeProblem prob = new MazeProblem(buildBigMaze(size, init, key, goals));
        ArrayList<String> solution = Pathfinder.solve(prob);
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_bigboi_t2() {
        int size = 1000;
        MazeState init = new MazeState(500,500),
                  key  = new MazeState(500, 1);
        HashSet<MazeState> goals = new HashSet<>();
        goals.add(new MazeState(500, 999));
        goals.add(new MazeState(1, 999));
        goals.add(new MazeState(999, 999));
        
        MazeProblem prob = new MazeProblem(buildBigMaze(size, init, key, goals));
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(1497, result[1]);
    }
    
    @Test
    public void testPathfinder_bigboi_t3() {
        int size = 1000;
        MazeState init = new MazeState(500,500),
                  key  = new MazeState(500, 1);
        HashSet<MazeState> goals = new HashSet<>();
        goals.add(new MazeState(500, 501));
        
        MazeProblem prob = new MazeProblem(buildBigMaze(size, init, key, goals));
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(999, result[1]);
    }
    
    @Test
    public void testPathfinder_bigboi_t4() {
        int size = 1000;
        MazeState init = new MazeState(500,500),
                  key  = new MazeState(500, 501);
        HashSet<MazeState> goals = new HashSet<>();
        goals.add(new MazeState(500, 499));
        
        MazeProblem prob = new MazeProblem(buildBigMaze(size, init, key, goals));
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);
        assertEquals(3, result[1]);
    }
    
   
    @Test
    public void testPathfinder_t12() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // result will be a 2-tuple (isSolution, cost) where
        // - isSolution = 0 if it is not, 1 if it is
        // - cost = numerical cost of proposed solution
        int[] result = prob.testSolution(solution);
        
        assertEquals(1, result[0]); // Test that result is a solution
        assertEquals(6, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t13() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(14, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t14() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t15() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
    @Test
    public void testPathfinder_t16() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XXX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] results = prob.testSolution(solution);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t17() {
        String[] maze = {
            "XXXXXXX",
            "XI.X..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(0, result[0]);  // Test that result is a solution
        assertEquals(-1, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t18() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "XXXXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XXXX.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "X.....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
    //heuristic not most efficient path, lots of unnecessary exploration
    @Test
    public void testPathfinder_t19() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "XXXXXXXXXXXX.XXXXX",
                "XG......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XXXX.XXXXX",
                "X.......X......G.X",
                "X.....X.XXXXXXX..X",
                "X....GX..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(30, result[1]); // Ensure that the solution is optimal
    }
    
    //hopefully memoization works ?? 
    @Test
    public void testPathfinder_t20() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "XGX.K....I.........................................X",
                "XXXXXXXXXXXX.XXXXXXXXX.............................X",
                "XG......X.........XXXX.............................X",
                "X.XXXXX.X..XXXXX...................................X",
                "X.....X.X..X.......................................X",
                "XXXXXXX.XXXXXXXXXX.................................X",
                "X.......X......G.X.................................X",
                "X.....X.XXXXXXX..X.................................X",
                "X....GX..........X................................GX",
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(59, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t21() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "X.XXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XX.X.XXXXX",
                "XM......X........X",
                "XMM...X.XXXXXXX..X",
                "XGMMMMX..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(50, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t22() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "X.XXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XX.X.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "XG...MX..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(46, result[1]); // Ensure that the solution is optimal
    }
    
    
    @Test
    public void testPathfinder_t23() {
    	String[] maze = {
                "XXXXXXXXXXXXXXXXXX",
                "XGX.K....I.......X",
                "X.XXXXXXXXXX.XXXXX",
                "X.......X........X",
                "X.XXXXX.X..XXXXX.X",
                "X.....X.X..X.....X",
                "XXXXXXX.XXXX.XXXXX",
                "X.......X........X",
                "X.....X.XXXXXXX..X",
                "XG....X..........X",
                "XXXXXXXXXXXXXXXXXX"
            };
    	MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(50, result[1]); // Ensure that the solution is optimal
    }
    
}
