package NineSquareSolver;

//This has remained unchanged from how it was in the providedCode folder. The only real difference is that it's in a package now.
    //If it ain't broke, don't fix it!

//code provided by Professor John Gan (but with some comments added by me later on)

import java.util.ArrayList;

/*
The class Node represents nodes.
Its essential features are a GameState and a reference to the node's parent node. The
latter is used to assemble and output the solution path once the goal sate has been reached.
 */

public class Node {
    GameState state;    // The state associated with the node
    Node parent;        // The node from which this node was reached.
    private int cost;   // The cost of reaching this node from the initial node.

    /*
      Constructor used to create new nodes.
     */
    public Node(GameState state, Node parent, int cost) {
        this.state = state;
        this.parent = parent;
        this.cost = cost;
    }

    /*
      Constructor used to create initial node.
     */
    public Node(GameState state) {
        this(state,null,0);
    }

    //returns the 'cost' (number of moves to reach this state) of this node
    public int getCost() {
        return cost;
    }

    //returns the state of this current node as a String
    public String toString() {
        return "Node:" + state + " ";
    } //the 'state' is turned into a string via an implicit call to the GameState.toString() method

    /*
      Given a list of nodes as first argument, findNodeWithState searches the list for a node
       whose state is that specified as the second argument.
       If such a node is in the list, the first one encountered is returned.
       Otherwise null is returned.
     */
    public static Node findNodeWithState(ArrayList<Node> nodeList, GameState gs) {
        for (Node n : nodeList) { //for every node in nodeList
            if (gs.sameBoard(n.state)) return n; //if the state of that existing node is the same as this one, return that existing node
        }
        return null;
    }


}
