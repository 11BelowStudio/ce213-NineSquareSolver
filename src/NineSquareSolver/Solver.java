package NineSquareSolver;

//Solver.java
//17/10/2019
//edited from the code provided by Professor John Gan

//in short, it has been edited to use bidirectional breadth-first search, instead of breadth-first search (and is much faster than it was!)

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;


/*
   Solver is a class that contains the methods used to search for and print solutions
   plus the data structures needed for the search.
 */

public class Solver {

    ArrayList<Node> unexpanded = new ArrayList<Node>(); // Holds unexpanded node list
    ArrayList<Node> expanded = new ArrayList<Node>();   // Holds expanded node list
    ArrayList<Node> revUnexpanded = new ArrayList<Node>(); //Holds unexpanded reversed node list
    ArrayList<Node> revExpanded = new ArrayList<Node>(); //Holds expanded reversed node list
    Node rootNode;                                 // Node object representing initial state
    Node reverseRoot;       //node object representing reverse initial state

    /*
       Solver is a constructor that sets up an instance of the class with
            a node corresponding to the initial state as the root node.
            a node corresponding to the final state as the reverse root node
     */
    public Solver(char[] initialBoard, char[] finalBoard) {
        GameState initialState = new GameState(initialBoard);
        rootNode = new Node(initialState);
        GameState initialReverse = new GameState(finalBoard);
        reverseRoot = new Node(initialReverse);
    }

    /*
        The method 'solve' searches for a solution. It implements a bidirectional breadth first search.
            Basically, it performs a breadth first search from the start state to the solution
                whilst performing another breadth first search from the solution to the start state
            When a node is generated with a state that matches that of a node in the other list, that countas as a solution
                The path needed to get to those nodes in each direction are put together to make a full solution
        The problem asks for a solution with the minimum number of moves.
        The Printwriter argument is used to specify where the output should be directed.

        Bidirectional breadth-first search was implemented here for several reasons
            * Finds a solution
            * The solution will be optimal
            * Better time+space complexity than normal breadth-first search
                > O(2(b^(d/2)))
                > 2 breadth-first searches which only go to depth d/2
            * I was planning on implementing bidirectional iterative deepening depth-first search, however, from some tests
                I carried out when I was attempting to implement it, it took considerably longer than the bidirectional
                breadth-first search I had implemented, so, as the payoff looked like it would be very minimal, I decided to stick to this search


        There is no value to differentiate whether a node is being used for the main search or for the reverse search,
            as, due to the way I have implemented the bidirectional search, it isn't needed, and would just slow things down.
            The non-reversed nodes will remain in the 'unexpanded' and 'expanded' ArrayLists, whilst the reversed nodes
            remain in the 'revUnexpanded' and 'revExpanded' ArrayLists. They are never sent into the other direction's ArrayLists.
            Efficiency yay
     */


    public void solve(PrintWriter output) {
        unexpanded.add(rootNode);          // Initialise the unexpanded node list with the root node.
        revUnexpanded.add(reverseRoot); //initialise the reverse unexpanded node list with reverse root node.

        while ((unexpanded.size() > 0) && (revUnexpanded.size() > 0)) {    // While there are nodes waiting to be expanded:

            //A non-reverse node will be expanded first

            Node n = unexpanded.get(0);    // Get the first item off the unexpanded node list
            expanded.add(n);               // Add it to the expanded node list
            unexpanded.remove(0);    // Remove it from the unexpanded node list

            ArrayList<GameState> moveList = n.state.possibleMoves();      // Get list of permitted moves for the non-reversed node
            for (GameState gs : moveList) {                               // For each such move:
                if ((Node.findNodeWithState(unexpanded, gs) == null) &&   // If it is not already on either
                    (Node.findNodeWithState(expanded, gs) == null)) { // expanded or unexpanded node list then
                        int newCost = n.getCost()+ 1;                         // add it to the unexpanded node list.
                        Node newNode = new Node(gs, n, newCost);              // The parent is the current node.

                        if (Node.findNodeWithState(revUnexpanded, gs) != null) { //if a reversed node with the same gamestate as this one is in the reversed unexpanded list
                                reportSolution(newNode,Node.findNodeWithState(revUnexpanded, gs), output); //a solution has been found, and is reported
                                return; //ends this function
                            /*only the reverse unexpanded list will be checked, as the expanded nodes will all have at least one child unexpanded node
                                so the unexpanded nodes will all be encountered before the expanded nodes, meaning that checking the expanded
                                nodes would just be a massive waste of time, as, if no unexpanded nodes match the current gamestate,
                                none of the expanded nodes would match it either*/
                        } else{ //if this isn't the same as a reversed node
                                unexpanded.add(newNode); //adds newNode to the unexpanded node list
                        }
                }
            }

            //and now, same stuff as above, but for the reverse nodes
                //this is where the 'bidirectional' part of the 'bidirectional breadth-first search' comes into play

            Node r = revUnexpanded.get(0);    // Get the first item off the reversed unexpanded node list
            revExpanded.add(r);               // Add it to the reverse expanded node list
            revUnexpanded.remove(0);    // Remove it from the reverse unexpanded node list

            ArrayList<GameState> revMoveList = r.state.possibleMoves();      // Get list of permitted moves
            for (GameState gs : revMoveList) {                               // For each such move:
                if ((Node.findNodeWithState(revUnexpanded, gs) == null) &&   // If it is not already on either
                        (Node.findNodeWithState(revExpanded, gs) == null)) { // reverse expanded or reverse  unexpanded node list then
                    int newCost = r.getCost()+ 1;                         // add it to the reverse unexpanded node list.
                    Node newNode = new Node(gs, r, newCost);              // The parent is the current node.

                    if (Node.findNodeWithState(unexpanded, gs) != null) { //if a node with the same gamestate is in the normal unexpanded list
                        reportSolution(Node.findNodeWithState(unexpanded, gs), newNode, output); //report the solution
                        return; //end this function
                    } else{ //if no match was found
                        revUnexpanded.add(newNode); //adds newNode to the unexpanded reverse node list
                    }
                }
            }
        }
        output.println("No solution found"); //inform the user if no solution could be found
    }

    /*
       printSolution and printRevSolution are recursive methods which collectively print all the states in a solution.
       printSolution outputs everything leading to the state of node n/node r
            (Basically, it recursively outputs the first half of the solution)
       printRevSolution prints everything between the state of node n/node r to the final node
            (basically, it recursively outputs the second half of the solution)
       The PrintWriter argument is used to specify where the output should be directed.
     */
    public void printSolution(Node n, PrintWriter output) {
        if (n.parent != null){
            printSolution(n.parent, output); //if this node has a parent node, it will call this again for that parent node
        }
        output.println(n.state); //prints the state of node n, uses the GameState.toString() method
    }

    //does the same thing as above, but in reverse (printing from child to parent), as the reverse nodes will have all the same information but in reverse instead.
    public void printRevSolution(Node r, PrintWriter output){
        output.println(r.state); //ouputs the state of node r
        if (r.parent != null) printRevSolution(r.parent, output); //calls this again for the parent of r, if it exists
    }

    /*
       reportSolution prints the solution together with statistics on the number of moves
            and the number of expanded and unexpanded nodes.
       The Node arguments 'n' and 'r' are the non-reversed and reversed nodes which have the same gamestate
       The PrintWriter argument is used to specify where the output should be directed.
     */
    public void reportSolution(Node n, Node r, PrintWriter output) {
        System.out.println("Solution found! Now printing to output.txt"); //putting this here so it's easier to see when it is done
        output.println("Solution found!\n"); //added the newline character here
        printSolution(n, output); //prints the first half of the solution to get the nodes which lead to node n
        printRevSolution(r.parent, output); //prints the second half of the solution (the reverse half), to get the nodes which lead to node r.
            //this starts from the parent of node r, as node r has the same state as node n, so starting from r would lead to the same state being output twice
            //however, by starting the parent of r instead, the state directly following the state of n will be output immediately after n.
        output.println((n.getCost() + r.getCost()) + " Moves"); //outputs the total number of moves by adding the cost of n and r
        output.println("Nodes expanded: " + (this.expanded.size() + this.revExpanded.size())); //returns combined size of the 'expanded' lists
        output.println("Nodes unexpanded: " + (this.unexpanded.size() + this.revUnexpanded.size())); //returns combined size of 'unexpanded' lists
        output.println();
    }



    public static void main(String[] args) throws Exception {
        Solver problem = new Solver(GameState.INITIAL_BOARD, GameState.FINAL_BOARD);
            // Set up the problem to be solved, using the GameState class's INITIAL_BOARD and FINAL_BOARD info
        File outFile = new File("output.txt");       // Create a file as the destination for output (as output.txt)
        PrintWriter output = new PrintWriter(outFile);         // Create a PrintWriter for that file
        problem.solve(output);                                 // Search for and print the solution
        output.close();                                        // Close the PrintWriter (to ensure output is produced).
    }
}
