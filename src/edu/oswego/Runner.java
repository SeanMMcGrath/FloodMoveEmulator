package edu.oswego;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

/**
 * FloodMoveEmulator
 *
 * @author - Sean McGrath
 */
public class Runner extends RecursiveTask<Puzzle> {


    final Puzzle board;
    final int depth;


    public Runner(Puzzle board , int depth){
        this.board = board;
        this.depth = depth;
    }

    @Override
    protected Puzzle compute() {
        if(board.isDone() || depth == Controller.MAX_DEPTH){
            //if board is fully flooded or depth limit reached, then return self
            return board;
        }
        final ArrayList<Color> moves = board.possibleMoves();
        //moves to do so make and invoke them
        final ArrayList<Runner> toInvoke = new ArrayList<>();
        for (Color c : moves) {
            Puzzle p = board.clone();
            p.depth++;
            p.memory = board;
            p.flood(c);
            Runner r = new Runner(p, depth + 1);
            toInvoke.add(r);
        }
        invokeAll(toInvoke);
        Runner best = null;
        for (Runner r : toInvoke) {//get the runner with the largest flood size
            if (best == null) {
                best = r;
            } else {
                if (best.board.getMainPoints().size() < r.board.getMainPoints().size()) {
                    best = r;
                }
            }
        }
        return best.board;
    }
}
