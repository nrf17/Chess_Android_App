package model.chess;

/**
 * 
 * This class implements a chess piece of the knight piece
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public class Knight extends ChessPiece {

	/**
	 * Initializes an instance with given column, row and team
	 * 
	 * @param col column of piece
	 * @param row what row the piece is in
	 * @param team white or black
	 */
	Knight(char col, int row, char team){
		super(col, row, team);
	}

	/**
	 * checks if you can move to the desired spot
	 * @param location what row and column the piece is currently at
	 * @return true if the piece can move to the desired spot on the board by rules of knight movement, false if not
	 */
	@Override
	public boolean canMove(String location) {
		char column = location.charAt(0);
		int newRow = Character.getNumericValue(location.charAt(1));
		
		int newCol = 0;
		switch(column){
		case 'a': newCol = 1; break;
		case 'b': newCol = 2; break;
		case 'c': newCol = 3; break;
		case 'd': newCol = 4; break;
		case 'e': newCol = 5; break;
		case 'f': newCol = 6; break;
		case 'g': newCol = 7; break;
		case 'h': newCol = 8; break;
		};
		
		int oldCol = 0;
		switch(col){
		case 'a': oldCol = 1; break;
		case 'b': oldCol = 2; break;
		case 'c': oldCol = 3; break;
		case 'd': oldCol = 4; break;
		case 'e': oldCol = 5; break;
		case 'f': oldCol = 6; break;
		case 'g': oldCol = 7; break;
		case 'h': oldCol = 8; break;
		};
		
		int rowDiff = newRow - row;
		if(rowDiff < 0){ rowDiff = row - newRow; }
		int colDiff = newCol - oldCol;
		if(colDiff < 0){ colDiff = oldCol - newCol; }
		
		if( (colDiff == 2) && (rowDiff == 1) ){ return true; }
		else if( (colDiff == 1) && (rowDiff == 2) ){ return true; }
		else { return false; }
		
	}

	/**
	 * checks if you can move to the desired spot
	 * @param piece the chess piece to be moved
	 * @return true if piece is moving correctly to knights movements, false if not
	 */
	@Override
	public boolean canMove(ChessPiece piece) {
		char column = piece.getCol();
		int newRow = piece.getRow();
		
		if (piece.getTeam() == team)
			return false;

		int newCol = 0;
		switch(column){
		case 'a': newCol = 1; break;
		case 'b': newCol = 2; break;
		case 'c': newCol = 3; break;
		case 'd': newCol = 4; break;
		case 'e': newCol = 5; break;
		case 'f': newCol = 6; break;
		case 'g': newCol = 7; break;
		case 'h': newCol = 8; break;
		};
		
		int oldCol = 0;
		switch(col){
		case 'a': oldCol = 1; break;
		case 'b': oldCol = 2; break;
		case 'c': oldCol = 3; break;
		case 'd': oldCol = 4; break;
		case 'e': oldCol = 5; break;
		case 'f': oldCol = 6; break;
		case 'g': oldCol = 7; break;
		case 'h': oldCol = 8; break;
		};
		
		int rowDiff = newRow - row;
		if(rowDiff < 0){ rowDiff = row - newRow; }
		int colDiff = newCol - oldCol;
		if(colDiff < 0){ colDiff = oldCol - newCol; }
		
		if( (colDiff == 2) && (rowDiff == 1) ){ return true; }
		else if( (colDiff == 1) && (rowDiff == 2) ){ return true; }
		else { return false; }
		
	}

	/**
	 * prints out the knight team along with its piece identifier
	 * @return string with team and knight marker
	 */
	@Override
	public String toString() {
		return "" + team + "N";
	}

	@Override
	public ChessPiece clone() {
		Knight clonedPiece = new Knight(col, row, team);
		clonedPiece.previousCol = previousCol;
		clonedPiece.previousRow = previousRow;
		return clonedPiece;
	}

}