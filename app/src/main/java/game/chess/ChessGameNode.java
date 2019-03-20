package game.chess;

import java.io.Serializable;

import model.chess.ChessGame;

public class ChessGameNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChessGame game;
    private ChessGameNode next;
    private ChessGameNode prev;

    ChessGameNode(ChessGame game) {
        this.game = game;
    }

    ChessGameNode(ChessGame game, ChessGameNode prev) {
        this.game = game;
        this.prev = prev;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setNext(ChessGameNode next) {
        this.next = next;
    }

    public ChessGameNode getNext() {
        return next;
    }

    public ChessGameNode getPrev() {
        return prev;
    }
}
