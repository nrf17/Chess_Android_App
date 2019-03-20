package game.chess;

import java.io.Serializable;
import java.util.Calendar;

public class SavedChessGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChessGameNode lastNode, firstNode;
    private Calendar time;
    private String name;
    private int numOfMoves;

    SavedChessGame(ChessGameNode lastNode, Calendar time) {
        this.lastNode = lastNode;

        firstNode = lastNode;
        numOfMoves = 0;
        while (firstNode.getPrev() != null) {
            firstNode = firstNode.getPrev();
            numOfMoves++;
        }

        this.time = time;
    }

    SavedChessGame(ChessGameNode lastNode, Calendar time, String name) {
        this(lastNode, time);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChessGameNode getLastNode() {
        return lastNode;
    }

    public ChessGameNode getFirstNode() {
        return firstNode;
    }

    public Calendar getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public int getNumOfMoves() {
        return numOfMoves;
    }
}
