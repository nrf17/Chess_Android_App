package model.chess;

/**
 * 
 * This class implements a chess piece of the pawn piece
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public class Pawn extends ChessPiece {

	/**
	 * boolean to see if the king has moved yet or not
	 */
	private boolean firstMove;
	
	/**
	 * boolean to about a later move
	 */
	private boolean secondMove;
	
	/**
	 * boolean to see if the pawn just advanced two spots foward for its first move, for en passant
	 */
	private boolean justAdvancedTwo;
	
	/**
	 * boolean so it is know that the chance for an en passant capture is over
	 */
	private boolean previouslyJustAdvancedTwo;

	/**
	 * Initializes an instance with given column, row and team
	 * <p>
	 * set 1st move to true, 2nd to false, just advanced to false, prev advanced to false
	 * @param col column of piece
	 * @param row what row the piece is in
	 * @param team white or black
	 * 
	 */
	Pawn(char col, int row, char team){
		super(col, row, team);
		firstMove = true;
		secondMove = false;
		justAdvancedTwo = false;
		previouslyJustAdvancedTwo = false;
	}

	/**
	 * tries moving the desired spot
	 * <p>
	 * after 1st move set to false, 2nd true, then after that continue to set 2nd to false, checks for movement of two spots and when
	 * @param location location what row and column the piece is going to be moved to
	 * @return true if piece can move to spot, false if not
	 * 
	 */
	@Override
	public boolean move(String location) {
		int startRow, newRow;
		startRow = row;
		newRow = Character.getNumericValue(location.charAt(1));
		
		if(!super.move(location))
			return false;
		
		if (previouslyJustAdvancedTwo)
			previouslyJustAdvancedTwo = false;
		
		if (justAdvancedTwo) {
			justAdvancedTwo = false;
			previouslyJustAdvancedTwo = true;
		}
		
		if (secondMove)
			secondMove = false;
		
		if (firstMove) {
			if ((newRow == (startRow + 2)) || (newRow == (startRow - 2)))
				justAdvancedTwo = true;
			
			secondMove = true;
			firstMove = false;
		}
		
		return true;
	}

	/**
	 * tries moving the desired spot
	 * <p>
	 * after 1st move set to false, 2nd true, then after that continue to set 2nd to false, checks for movement of two spots and when
	 * @param piece the chess piece to be moved
	 * @return true if piece can move to spot, false if not
	 * 
	 */
	@Override
	public boolean move(ChessPiece piece) {
		int startRow, newRow;
		startRow = row;
		newRow = piece.getRow();
		
		if(!super.move(piece))
			return false;
		
		if (previouslyJustAdvancedTwo)
			previouslyJustAdvancedTwo = false;
		
		if (justAdvancedTwo) {
			justAdvancedTwo = false;
			previouslyJustAdvancedTwo = true;
		}
		
		if (secondMove)
			secondMove = false;
		
		if (firstMove) {
			if ((newRow == (startRow + 2)) || (newRow == (startRow - 2)))
				justAdvancedTwo = true;
			
			secondMove = true;
			firstMove = false;
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
		
		if (previouslyJustAdvancedTwo) {
			justAdvancedTwo = true;
			previouslyJustAdvancedTwo = false;
		}
		
		if (secondMove) {
			firstMove = true;
			secondMove = false;
		}
		
		return true;
	}

	/**
	 * checks to see if the pawn is moving properly according to its rules of movement
	 * @param location what row and column the piece is currently at
	 * @return true if the piece can move to the desired spot on the board, false if not
	 * 
	 */
	@Override
	public boolean canMove(String location) {
		char newCol = location.charAt(0);
		int newRow = Character.getNumericValue(location.charAt(1));
		
		//check if the pawn is not moving
		if((newCol == col) && (newRow == row))
			return false;
		
		if(team == 'w') {
			//check that pawn is moving forward
			if(newRow <= row)
				return false;
			
			if(newCol != col)
				return false;
			
			if(newRow > (row + 2)) {
				return false;
			}
			
			if(newRow == (row + 2)) {
				if(!firstMove)
					return false;
				
				return true;
			}
			
			if(newRow == (row + 1))
				return true;
		}else {
			//check that pawn is moving forward
			if(newRow >= row)
				return false;
			
			if(newCol != col)
				return false;
			
			if(newRow < (row - 2))
				return false;
			
			if(newRow == (row - 2)) {
				if(!firstMove)
					return false;
				
				return true;
			}
			
			if(newRow == (row - 1))
				return true;
		}
		
		return false;
	}

	/**
	 * checks to see if the pawn is moving properly according to its rules of movement
	 * @param piece the chess piece to be moved
	 * @return true if the piece can move to the desired spot on the board, false if not
	 * 
	 */
	@Override
	public boolean canMove(ChessPiece piece) {		
		char newCol = piece.getCol();
		int newRow = piece.getRow();
		
		if(piece.getTeam() == team)
			return false;
		
		if(newCol == col)
			return false;
		
		if(team == 'w') {
			if(newRow != (row + 1))
				return false;
		}else {
			if(newRow != (row - 1))
				return false;
		}
		
		if((newCol == (col + 1)) || (newCol == (col - 1)))
			return true;
		
		return false;
	}

	/**
	 * special case when a pawn can just to an empty space behind an enemy pawn right next to it
	 * @param location the location the piece is to be moved to
	 * @return true if en passant move is attempted
	 */
	public boolean enPassantMove(String location) {
		char newCol = location.charAt(0);
		int newRow = Character.getNumericValue(location.charAt(1));

		if ((newCol != (col + 1)) && (newCol != (col - 1)))
			return false;

		if (team == 'w') {
			if (newRow != (row + 1))
				return false;
		} else {
			if (newRow != (row - 1))
				return false;
		}

		col = newCol;
		row = newRow;

		return true;
	}

	/**
	 * checks if the en passant case is true or not
	 * @return justAdvancedTwo will give whether the pawn just previously made the move to jump 2 spots foward
	 */
	public boolean justAdvancedTwo() {
		return justAdvancedTwo;
	}

	/**
	 * the chance for an en passant capture is over on the pawn, so the boolean is set to false
	 */
	public void setAdvancedTwoFalse() {
		justAdvancedTwo = false;
	}

	/**
	 * compares one piece to another, looking for a pawn to pawn comparison for the en passant case
	 * @param piece the chess piece that will be compared to
	 * @return false if it is not a pawn and they do not have the same row, if its the same team, and incorrect columns, else returns true if all these conditions are met
	 */
	@Override
	public boolean equals(Object piece) {
		Pawn pawnToCompare;

		if (piece == null)
			return false;

		if (!(piece instanceof Pawn))
			return false;

		pawnToCompare = (Pawn) piece;

		if (col != pawnToCompare.getCol())
			return false;

		if (row != pawnToCompare.getRow())
			return false;

		if (team != pawnToCompare.getTeam())
			return false;

		return true;
	}

	/**
	 * prints out the pawn team along with its piece identifier
	 * @return string with team and pawn marker
	 */
	@Override
	public String toString() {
		return "" + team + "p";
	}

	@Override
	public ChessPiece clone() {
		Pawn clonedPiece = new Pawn(col, row, team);
		clonedPiece.previousCol = previousCol;
		clonedPiece.previousRow = previousRow;
		clonedPiece.firstMove = firstMove;
		clonedPiece.secondMove = secondMove;
		clonedPiece.previouslyJustAdvancedTwo = previouslyJustAdvancedTwo;
		return clonedPiece;
	}

}