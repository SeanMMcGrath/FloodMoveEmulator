package edu.oswego;

import java.util.ArrayList;
import java.util.List;
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

        //invoke
        List<Runner> results = (List<Runner>) invokeAll(toInvoke);
        //find and return best result
        return getBest(results).board;
    }

    /**
     * takes a list of results and finds the one with the largest flood size, prioritizing smaller depths
     * @param results - list of results
     * @return - best result
     */
    private Runner getBest(List<Runner> results){
        Runner best = null;
        int shallowest = -1;

        for (Runner r : results) {//get the runner with the largest flood size
            if (best == null) {
                best = r;
                shallowest = r.depth;
            } else {
                if(shallowest > r.depth) {
                    //smaller depth so set as best
                    best = r;
                    shallowest = r.depth;
                } else if(shallowest == r.depth) {
                    if (best.board.getMainPoints().size() < r.board.getMainPoints().size()) {//same depth so check if better size
                        best = r;
                    }
                }
                //if shallowest < depth then ignore it since its an inferior result
            }
        }
        return best;
    }
}
