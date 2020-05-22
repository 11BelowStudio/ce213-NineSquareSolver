package NineSquareSolver;

//GameState.java
//17/10/2019
//edited from the code provided by Professor John Gan

//in short, it has been reverse-engineered to work with the nine-square problem instead of the example problem.

import java.util.ArrayList;

/*
      Instances of the class NineSquareSolver.GameState represent states that can arise in the sliding block puzzle.
      The char array board represents the board configuration; that is, the location of each of the 8 numbered tiles.
      it represents it as so:
            {A,B,C,D,E,F,G,H,I}, where each letter corresponds to its position in this square:
                    A B C
                    D E F
                    G H I
      Each numbered tile is represented by the corresponding number, and the empty location is represented by the space character.
      The int spacePos holds the position of the empty location. (Although this is redundant it saves a
      lot of computation as it is frequently necessary to refer to this location).
      INITIAL_BOARD and FINAL_BOARD are constant arrays holding the initial and final board configurations.
      lastSwap keeps track of what move was used to create the current state
            the value is always an integer, representing the indexes of the two spaces that last swapped their values
            the lower integer comes first in the value, then the higher one (fewer potential values, makes it easier to deal with, basically)
            I chose to make this an integer instead of a string because it's easier to write 'if (lastMove == 03)' than 'if (lastMove.equals("03"))'
                and also because strings are objects, and using those might make the program take even longer
            This is initialised when every new state of the board is created, however, it will be blank for the very first move, as there won't have been a prior move.


 */

public class GameState {
    final char[] board;
    private int spacePos;
    static final char[] INITIAL_BOARD = {'8','7','6','5','4','3','2','1',' '};
    static final char[] FINAL_BOARD = {'1','2','3','4','5','6','7','8',' '};
    private int lastMove;

    /*
        GameState is a constructor that takes a char array holding a board configuration as argument.
     */
    public GameState(char[] board) {
        this.board = board;
        for (int j = 0; j < 9; j++){
            if (board[j] == ' ') {
                this.spacePos = j;
                //initializes the location of spacePos to be the current location of the blank item in this new GameState's board
                break; //stops the loop if it's found
            }
        }
    }

    /*
        clone returns a new NineSquareSolver.GameState with the same board configuration as the current NineSquareSolver.GameState.
     */
    public GameState clone() {
        char[] clonedBoard = new char[9]; //creates clonedBoard, a blank character array holding 9 characters
        System.arraycopy(this.board, 0, clonedBoard, 0, 9);
            //copies contents of this GameState's 'board' array to the clonedBoard array
        return new GameState(clonedBoard/*, this.reverse*/); //returns a call to create a new GameState object with a copy of the 'board' of this GameState
    }

    //I removed 'getSpacePos' from the example code as I had no use for it.

    /*
        toString returns the board configuration of the current GameState as a printable string.

        it returns it in the form:
        A B C
        D E F
        G H I

        where each letter corresponds to the number in the corresponding place in the current state of the board array when represented as {A,B,C,D,E,F,G,H,I}

        basically, it makes it a lot easier to read the output of the program
    */
    public String toString() {
        String s = "";//initialises S as as as empty string
        int i = 1; //initialises i as 1
        for (char c : this.board){ //for each character in the board array
            s = s + c; //it is added to 's'
            if ((i % 3) == 0){ //if the modulo of i over 3 is 0 (every 3rd character)
                s = s + "\n"; //adds a newline character to the string
            } else { //if not
                s = s + " "; //adds a space to the string
            }
            i = i + 1; //i is incremented by 1
        }
        return s; //the s string is returned
    }

    /*
         sameBoard returns true if and only if the GameState supplied as argument has the same board
         configuration as the current GameState.
     */
    public boolean sameBoard(GameState gs) {
        for (int j = 0; j < 9; j++) { //for each index in the boards of each GameState
            if (this.board[j] != gs.board[j]) return false; //return false if there's any difference between the item at that index in each board
        }
        return true; //return true if false wasn't returned
    }

    /*
        possibleMoves returns an arrayList of all GameStates that can be reached in a single move from the GameState.
        For ease of understanding this, each possible space in the tile thing can be seen as numbered from 0 to 8, with the arrangement:
            0 1 2
            3 4 5
            6 7 8

        It also worth remembering that the only valid moves are when a space with a value in it swaps positions with the adjacent empty space.
        Basically, the valid moves change with the location of the blank space, as follows:

            position of blank space -> list of spaces that can swap with it (swap identifier)
            0 -> 1 (01), 3(03)
            1 -> 0(01), 2(12), 4(14)
            2 -> 1(12), 5(25)
            3 -> 0(03), 4(34), 6(36)
            4 -> 1(14), 3(34), 5(45), 7(47)
            5 -> 2(25), 4(45), 8(58)
            6 -> 3(36), 7(67)
            7 -> 4(47), 6(67), 8(78)
            8 -> 5(58), 7(78)

            only one swap will ever be possible for spaces 0, 2, and 6 with the lastSwap limit.
                Both choices are possible for space 8, at least for the first movement of it, due to the empty space starting there.

        The switch-case stuff in this function basically uses the current location of the empty space to work out which new GameStates are legal, and then creates them.

        Additionally, each case has an if statement to ensure that the prior move will not be repeated, for purposes of optimization.
            However,the if statement for space 8 is different to the other corners, to ensure both moves are allowed from the root nodes
                as the blank space starts in space 8 for the initial and goal node,

        the new makeNewState constructor passes the position to swap the blank space with, along with the identifier of the current move.

        I am aware that the numbers such as '01' should ideally be written like '1' instead, however, they are presented like that for
            readability purposes, and the compiler can cope with them.

     */
    public ArrayList<GameState> possibleMoves() {
        ArrayList<GameState> moves = new ArrayList<GameState>();
        switch (this.spacePos) { //use the value of spacePos to work out what to do
            case 0: //if the empty space is in position 0
                if (lastMove == 03) { //if 0 and 3 were swapped last time
                    moves.add(makeNewState(1, 01)); //swap 0 and 1
                } else { //otherwise (if 0 and 1 were last swapped)
                    moves.add(makeNewState(3, 03)); //swap 0 and 3
                }
                //space 0 is connected to spaces 1 and 3
                break;
            case 1: //if the empty space is in position 1
                if (lastMove == 01){ //if 0 and 1 swapped last time
                    moves.add(makeNewState(2, 12)); //swap 1 and 2
                    moves.add(makeNewState(4, 14)); //swap 1 and 4
                } else if (lastMove == 12){ //else if 1 and 2 were swapped last time
                    moves.add(makeNewState(0, 01)); //swap 0 and 1
                    moves.add(makeNewState(4, 14)); //swap 1 and 4
                } else{ //otherwise
                    moves.add(makeNewState(0, 01)); //swap 0 and 1
                    moves.add(makeNewState(2, 12)); //swap 1 and 4
                }
                //space 1 is connected to spaces 0, 2, and 4
                break;
            case 2: //if the empty space is in position 2
                if (lastMove == 25) { //if 2 and 5 swapped last time
                    moves.add(makeNewState(1, 12)); //swap 1 and 2
                } else {
                    moves.add(makeNewState(5, 25)); //swap 2 and 5
                }
                //space 2 is connected to spaces 1 and 5
                break;
                //you probably get the general gist by now
            case 3:
                if (lastMove == 03){
                    moves.add(makeNewState(4, 34)); //3 and 4
                    moves.add(makeNewState(6, 36)); //3 and 6
                } else if (lastMove == 34){
                    moves.add(makeNewState(0, 03)); //0 and 3
                    moves.add(makeNewState(6, 36)); //3 and 6
                } else{
                    moves.add(makeNewState(0, 03)); //0 and 3
                    moves.add(makeNewState(4, 34)); //3 and 4
                }
                //space 3 is connected to spaces 0, 4, and 6
                break;
            case 4:
                if (lastMove == 14){
                    moves.add(makeNewState(3, 34)); //3 and 4
                    moves.add(makeNewState(5, 45)); //4 and 5
                    moves.add(makeNewState(7, 47)); //4 and 7
                } else if (lastMove == 34) {
                    moves.add(makeNewState(1, 14)); //1 and 4
                    moves.add(makeNewState(5, 45)); //4 and 5
                    moves.add(makeNewState(7, 47)); //4 and 7
                } else if (lastMove == 45) {
                    moves.add(makeNewState(1, 14)); //1 and 4
                    moves.add(makeNewState(3, 34)); //3 and 4
                    moves.add(makeNewState(7, 47)); //4 and 7
                } else {
                    moves.add(makeNewState(1, 14)); //1 and 4
                    moves.add(makeNewState(3, 34)); //3 and 4
                    moves.add(makeNewState(5, 45)); //4 and 5
                }
                //space 4 is connected to spaces 1, 3, 5, and 7
                break;
            case 5:
                if (lastMove == 25) {
                    moves.add(makeNewState(4,45)); //4 and 5
                    moves.add(makeNewState(8,58)); //5 and 8
                } else if (lastMove == 45) {
                    moves.add(makeNewState(2, 25)); //2 and 5
                    moves.add(makeNewState(8,58)); //5 and 8
                } else{
                    moves.add(makeNewState(2, 25)); //2 and 5
                    moves.add(makeNewState(4,45)); //4 and 5
                }
                //space 5 is connected to spaces 2, 4, and 8
                break;
            case 6:
                if (lastMove == 67) { //if 6 and 7 were swapped last time
                    moves.add(makeNewState(3, 36)); //swap 3 and 6
                } else { //if 3 and 6 were swapped last time
                    moves.add(makeNewState(7, 67)); //swap 6 and 7
                }
                //space 6 is connected to spaces 3 and 7
                break;
            case 7:
                if (lastMove == 47){
                    moves.add(makeNewState(6, 67)); //6 and 7
                    moves.add(makeNewState(8, 78)); //7 and 8
                } else if (lastMove != 67){
                    moves.add(makeNewState(4, 47)); //4 and 7
                    moves.add(makeNewState(8, 78)); //7 and 8
                }
                else{
                    moves.add(makeNewState(4, 47)); //4 and 7
                    moves.add(makeNewState(6, 67)); //6 and 7
                }
                //space 7 is connected to spaces 6, 4, and 8
                break;
            case 8:
                if (lastMove == 78){
                    moves.add(makeNewState(5, 58)); //5 and 8
                } else if (lastMove == 58){
                    moves.add(makeNewState(7, 78)); //7 and 8
                } else{ //this 'else' will only be used for the first move, as there is no 'lastMove' for the first move
                    moves.add(makeNewState(5, 58)); //5 and 8
                    moves.add(makeNewState(7, 78)); //7 and 8
                }
                //space 8 is connected to spaces 5 and 7, will be the first space to run, so it has to be checked differently
                break;
            default: //a 'default' case should never be reached, but here it is just in case.
                System.out.println("Error! default case accidentally reached!"); //this default case should never appear, but it's here just in case.
        }
        return moves; //the array of legal moves is returned
    }

    public GameState makeNewState(int posSwap, int thisMove){
            //used for generating the new GameStates for each potential move, using the parameter passed to it
                //posSwap is the index of the current space which the blank space is swapping with from the array
                //thisMove is going to be used as 'lastMove' in the new GameState object
        GameState newState = this.clone();
            //creates a new GameState object called 'newState', which is a copy of the current GameState being used
        newState.board[this.spacePos] = this.board[posSwap];
            //puts the item which overwrites the space into the current position of the space in the newState's board array
        newState.board[posSwap] = ' ';
            //puts the space in the index represented by posSwap in the newState's board array
        newState.spacePos = posSwap;
            //updates the spacePos value for newState appropriately (so it is where the new space is)
        newState.lastMove = thisMove;
            //makes the lastMove value for the last swap equal to the thisMove value for this swap
        return newState;
            //newState is returned
    }

}

