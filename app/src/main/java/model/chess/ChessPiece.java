package model.chess;

import java.io.Serializable;

/**
 * 
 * This class implements a chess piece, which can consist of differnt types of pieces
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public abstract class ChessPiece implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * the column of the piece
	 */
	protected char col;
	
	/**
	 * the column of the piece from the previous move
	 */
	protected char previousCol;
	
	/**
	 * the row of the piece
	 */
	protected int row;
	
	/**
	 * the row of the piece from the previous move
	 */
	protected int previousRow;
	
	/**
	 * which team the piece belongs to
	 */
	protected char team;

	ChessPiece(char col, int row, char team) {
		this.row = row;
		this.col = col;
		this.team = team;
	}

	/**
	 * moves the piece to the input location
	 * @param location where you wish to move the piece
	 * @return false if the piece cant be moved here, true if it can and will update the previous and current position
	 */
	public boolean move(String location) {
		if(!canMove(location))
			return false;
		
		previousCol = col;
		previousRow = row;
		
		col = location.charAt(0);
		row = Character.getNumericValue(location.charAt(1));
		
		return true;
	}

	/**
	 * moves the piece to the input location
	 * @param piece the piece to be moved
	 * @return false if the piece cant be moved here, true if it can and will update the previous and current position
	 */
	public boolean move(ChessPiece piece) {
		if(!canMove(piece))
			return false;
		
		previousCol = col;
		previousRow = row;
		
		col = piece.getCol();
		row = piece.getRow();
		
		return true;
	}
	
	/**
	 * Step back to the previous state prior to the last move.
	 * <p>
	 * Sets the column and row to the column and row the piece was at prior to the
	 * last move. This method is most useful to revert back a move when the move
	 * puts the piece's king in check.
	 * 
	 * @return true if successful and false if not
	 */
	public boolean stepBack() {
		if ((previousCol == col) && (previousRow == row))
			return false;

		col = previousCol;
		row = previousRow;

		return true;
	}

	public abstract boolean canMove(String location);

	public abstract boolean canMove(ChessPiece piece);

	public abstract  ChessPiece clone();

	public abstract String toString();

	/**
	 * will give the user the current column of the piece
	 * @return the column of the piece
	 */
	public char getCol() {
		return col;
	}

	/**
	 * will give the user the current row of the piece
	 * @return the row of the piece
	 */
	public int getRow() {
		return row;
	}

	/**
	 * will give the team the piece belongs to
	 * @return the team the piece belongs to
	 */
	public char getTeam() {
		return team;
	}
}
