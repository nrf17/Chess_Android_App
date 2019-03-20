package model.chess;

/**
 * 
 * This class implements a chess piece of the bishop piece
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public class Bishop extends ChessPiece {

	/**
	 * Initializes an instance with given column, row and team
	 * 
	 * @param col column of piece
	 * @param row what row the piece is in
	 * @param team white or black
	 */
	Bishop(char col, int row, char team){
		super(col, row, team);
	}

	/**
	 * checks if you can move to the desired spot
	 * @param location what row and column the piece is currently at
	 * @return true if the piece can move to the desired spot on the board by rules of bishop movement, false if not
	 */
	@Override
	public boolean canMove(String location) {
		int colDiff, rowDiff, newRow;
		char newCol;

		newCol = location.charAt(0);
		newRow = Character.getNumericValue(location.charAt(1));

		// check if moving diagonally
		colDiff = Math.abs(newCol - col);
		rowDiff = Math.abs(newRow - row);

		if (colDiff == rowDiff)
			return true;

		return false;
	}

	/**
	 * checks to see if you can move to the desired spot
	 * @param piece the chess piece to be moved
	 * @return true if piece is moving correctly to bishops movements, false if not
	 */
	
	@Override
	public boolean canMove(ChessPiece piece) {
		int colDiff, rowDiff, newRow;
		char newCol;

		newCol = piece.getCol();
		newRow = piece.getRow();
		
		if (piece.getTeam() == team)
			return false;

		// check if moving diagonally
		colDiff = Math.abs(newCol - col);
		rowDiff = Math.abs(newRow - row);

		if (colDiff == rowDiff)
			return true;

		return false;
	}

	/**
	 * prints out the bishop team along with its piece identifier
	 * @return string with team and bishop marker
	 */
	@Override
	public String toString() {
		return "" + team + "B";
	}

	@Override
	public ChessPiece clone() {
		Bishop clonedPiece = new Bishop(col, row, team);
		clonedPiece.previousCol = previousCol;
		clonedPiece.previousRow = previousRow;
		return clonedPiece;
	}

}
