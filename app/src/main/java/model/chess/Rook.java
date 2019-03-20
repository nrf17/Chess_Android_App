package model.chess;

/**
 * 
 * This class implements a chess piece of the rook piece
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public class Rook extends ChessPiece {

	/**
	 * boolean to see if the king has moved yet or not
	 */
	public boolean firstMove;

	/**
	 * boolean to about a later move
	 */
	private boolean secondMove;

	/**
	 * Initializes an instance with given column, row and team
	 * <p>
	 * set 1st move to true, 2nd to false
	 * @param col column of piece
	 * @param row what row the piece is in
	 * @param team white or black
	 * 
	 */
	Rook(char col, int row, char team){
		super(col, row, team);
		firstMove = true;
		secondMove = false;
	}

	/**
	 * tries moving the desired spot
	 * <p>
	 * after 1st move set to false, 2nd true, then after that continue to set 2nd to false
	 * @param location location what row and column the piece is going to be moved to
	 * @return true if piece can move to spot, false if not
	 * 
	 */
	@Override
	public boolean move(String location) {
		if(!super.move(location))
			return false;
		
		if (secondMove)
			secondMove = false;
		
		if (firstMove) {
			firstMove = false;
			secondMove = true;
		}
		
		return true;
	}

	/**
	 * tries moving to the desired location
	 * <p>
	 * after 1st move set to false, 2nd true, then after that continue to set 2nd to false
	 * @param piece the chess piece to be moved
	 * @return true if piece can move to spot, false if not
	 * 
	 */
	@Override
	public boolean move(ChessPiece piece) {
		if(!super.move(piece))
			return false;
		
		if (secondMove)
			secondMove = false;
		
		if (firstMove) {
			firstMove = false;
			secondMove = true;
		}
		
		return true;
	}

	/**
	 * will revert back to previous move, an undo of the move just performed
	 * <p>
	 * if its second move, set this back to false and 1st back to true
	 * @return false if the piece cant go back to previous move, true if it can
	 * 
	 */
	@Override
	public boolean stepBack() {
		if (!super.stepBack())
			return false;
		
		if (secondMove) {
			firstMove = true;
			secondMove = false;
		}
		
		return true;
	}

	/**
	 * checks to see if the queen is moving properly according to its rules of movement
	 * @param location what row and column the piece is currently at
	 * @return true if the piece can move to the desired spot on the board, false if not
	 * 
	 */
	@Override
	public boolean canMove(String location) {
		char newCol = location.charAt(0);
		int newRow = Character.getNumericValue(location.charAt(1));

		// check if not moving
		if ((newCol == col) && (newRow == row))
			return false;

		// check if moving vertical
		if (newCol == col)
			return true;

		// check if moving horizontal
		if (newRow == row)
			return true;

		return false;
	}

	/**
	 * checks to see if the queen is moving properly according to its rules of movement
	 * @param piece the chess piece to be moved
	 * @return true if the piece can move to the desired spot on the board, false if not
	 * 
	 */
	@Override
	public boolean canMove(ChessPiece piece) {
		char newCol = piece.getCol();
		int newRow = piece.getRow();

		if (piece.getTeam() == team)
			return false;

		// check if not moving
		if ((newCol == col) && (newRow == row))
			return false;

		// check if moving vertical
		if (newCol == col)
			return true;

		// check if moving horizontal
		if (newRow == row)
			return true;

		return false;
	}

	/**
	 * prints out the rook team along with its piece identifier
	 * @return string with team and rook marker
	 */
	@Override
	public String toString() {
		return "" + team + "R";
	}

	@Override
	public ChessPiece clone() {
		Rook clonedPiece = new Rook(col, row, team);
		clonedPiece.previousCol = previousCol;
		clonedPiece.previousRow = previousRow;
		clonedPiece.firstMove = firstMove;
		clonedPiece.secondMove = secondMove;
		return clonedPiece;
	}

}