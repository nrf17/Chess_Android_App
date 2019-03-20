package model.chess;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * 
 * This class implements a game of chess
 * 
 * @author Nick Fasullo (nrf17)
 *
 */

public class ChessGame implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * the structure which holds all the chess pieces
	 */
	private HashMap<String, ChessPiece> pieces;

	/**
	 * the king chess piece of the white team
	 */
	private King whiteKing;

	/**
	 * the king chess piece of the black team
	 */
	private King blackKing;

	private char currentTeam;

	private boolean drawProposed;

	private boolean pawnPromoFlag;

	private Pawn pawnToPromote;

	/**
	 * an instance of a chess game which will create the board(hashmap) and begin the game
	 */
	public ChessGame() {
		pieces = new HashMap<String, ChessPiece>();

		currentTeam = 'w';
		drawProposed = false;
		pawnPromoFlag = false;

		populateBoard();
	}

	public ChessGame clone() {
		HashMap<String, ChessPiece> clonedPieces = (HashMap<String, ChessPiece>) pieces.clone();

		ChessGame clonedGame = new ChessGame();
		clonedGame.pieces = clonedPieces;

		// still need to clone the individual pieces
		ArrayList<String> listOfKeys = new ArrayList<String>(clonedPieces.keySet());

		for (String key : listOfKeys) {
			ChessPiece oldPiece = clonedPieces.get(key);
			ChessPiece newClonedPiece = oldPiece.clone();
			clonedPieces.replace(key, oldPiece, newClonedPiece);
		}

		// change reference to new Kings
		String whiteKingKey = "" + whiteKing.getCol() + whiteKing.getRow();
		String blackKingKey = "" + blackKing.getCol() + blackKing.getRow();

		clonedGame.whiteKing = (King) clonedPieces.get(whiteKingKey);
		clonedGame.blackKing = (King) clonedPieces.get(blackKingKey);

		// now copy all the remaining variables
		clonedGame.currentTeam = currentTeam;
		clonedGame.drawProposed = drawProposed;
		clonedGame.pawnPromoFlag = pawnPromoFlag;
		clonedGame.pawnToPromote = pawnToPromote;

		return clonedGame;
	}

	public ChessPiece getPiece(int index) {
		return pieces.get(gridIndexToKey(index));
	}

	public char getCurrentTeam() {
		return currentTeam;
	}

	public boolean stalemate() {
		if (currentTeam == 'w')
			return stalemate(whiteKing);
		else
			return stalemate(blackKing);
	}

	public boolean check() {
		if (currentTeam == 'w')
			return check(whiteKing);
		else
			return check(blackKing);
	}

	public boolean checkmate() {
		if (currentTeam == 'w')
			return checkmate(whiteKing);
		else
			return checkmate(blackKing);
	}

	private String gridIndexToKey(int index) {
		String key = "";
		int column;

		column = index % 8;

		switch (column) {
			case 0:
				key += 'a';
				break;
			case 1:
				key += 'b';
				break;
			case 2:
				key += 'c';
				break;
			case 3:
				key += 'd';
				break;
			case 4:
				key += 'e';
				break;
			case 5:
				key += 'f';
				break;
			case 6:
				key += 'g';
				break;
			case 7:
				key += 'h';
				break;
		}

		if (index > 7) {
			if (index > 15) {
				if (index > 23) {
					if (index > 31) {
						if (index > 39) {
							if (index > 47) {
								if (index > 55) {
									key += 1;
								} else {
									key += 2;
								}
							} else {
								key += 3;
							}
						} else {
							key += 4;
						}
					} else {
						key += 5;
					}
				} else {
					key += 6;
				}
			} else {
				key += 7;
			}
		} else {
			key += 8;
		}

		return key;
	}

	private int keyToGridIndex(String key) {
		char col;
		int index, row;

		index = 0;

		col = key.charAt(0);
		row = Character.getNumericValue(key.charAt(1));

		for (int i = row; i < 8; i++) {
			index += 8;
		}

		for (char c = col; c > 'a'; c--) {
			index += 1;
		}

		return index;
	}

	public boolean proposeDraw() {
		if (drawProposed)
			return false;

		drawProposed = true;
		return true;
	}

	public boolean drawProposed() {
		return drawProposed;
	}

	/**
	 * Return true if the last move was a pawn promotion move
	 * <p>
	 * This method should be called following every move. If this method returns true, the {@link #pawnPromo(Class)} method should be called with the desired ChessPiece class.
	 *
	 * @return true if eligible for pawn promotion, false otherwise
	 */
	public boolean pawnPromo() {
		return pawnPromoFlag;
	}

	/**
	 * Method to promote the pawn that is eligible for pawn promotion. This method should be called following the {@link #pawnPromo()} method returning true.
	 *
	 * @param pieceClass the desired class for pawn promotion
	 * @return true if pawn promotion was successful, false otherwise
	 */
	public boolean pawnPromo(Class<? extends ChessPiece> pieceClass) {
		char col, team;
		int row;

		if(!pawnPromoFlag)
			return false;

		col = pawnToPromote.getCol();
		row = pawnToPromote.getRow();
		team = pawnToPromote.getTeam();

		String pawnPromoKey = "" + col + row;

		try {
			Constructor ctor = pieceClass.getDeclaredConstructor(char.class, int.class, char.class);
			ctor.setAccessible(true);
			ChessPiece newPiece = (ChessPiece) ctor.newInstance(col, row, team);
			pieces.replace(pawnPromoKey, newPiece);

			pawnPromoFlag = false;
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean move(int firstIndex, int secondIndex) {
		if (!userEntry(firstIndex, secondIndex))
			return false;

		if (currentTeam == 'w')
			currentTeam = 'b';
		else
			currentTeam = 'w';

		return true;
	}

	private boolean move(String startKey, String endKey) {
		return move(keyToGridIndex(startKey), keyToGridIndex(endKey));
	}

	public boolean randomMove() {
		// check if in stalemate
		if(currentTeam == 'w'){
			if(stalemate(whiteKing))
				return false;
		}else{
			if(stalemate(blackKing))
				return false;
		}

		Random random = new Random();
		String startKey, endKey;
		ChessPiece pieceToMove;
		ArrayList<String> listOfKeys;
		ArrayList<String> keysToMoveTo;
		boolean moved = false;

		// populate existing keys list
		listOfKeys = new ArrayList<String>(pieces.keySet());

		while(!listOfKeys.isEmpty()) {
			startKey = listOfKeys.get(random.nextInt(listOfKeys.size()));
			pieceToMove = pieces.get(startKey);
			listOfKeys.remove(startKey);

			while ((pieceToMove.getTeam() != currentTeam) && !listOfKeys.isEmpty()) {
				startKey = listOfKeys.get(random.nextInt(listOfKeys.size()));
				pieceToMove = pieces.get(startKey);
				listOfKeys.remove(startKey);
			}

			// populate column and row lists
			keysToMoveTo = new ArrayList<String>(64);
			for(char col = 'a'; col <= 'h'; col++)
				for(int row = 1; row <= 8; row++)
					keysToMoveTo.add("" + col + row);

			endKey = keysToMoveTo.get(random.nextInt(keysToMoveTo.size()));
			keysToMoveTo.remove(endKey);

			if (move(startKey, endKey))
				moved = true;

			while (!moved && !keysToMoveTo.isEmpty()) {
				endKey = keysToMoveTo.get(random.nextInt(keysToMoveTo.size()));
				keysToMoveTo.remove(endKey);

				if (move(startKey, endKey))
					moved = true;
			}

			if(moved)
				break;
		}

		if(moved)
			return true;
		else
			return false;
	}

	/**
	 * Method that takes the movement of the piece that user wishes to perform
	 * <p>
	 * determines if a piece is in the selected spot and belongs to your team
	 * <p>
	 * special cases will be consider for the resulting move, then see if the move was legal for that type of chess piece, if not it will report so and ask the user to try another move, then will report check or check mate if applicable
	 * @param firstIndex index of the piece the user would like to move
	 * @param secondIndex index where the user wants to move the piece
	 * @return true if the move was legal and complete, false if not
	 */
	private boolean userEntry(int firstIndex, int secondIndex) {
		String firstKey, secondKey;
		ChessPiece pieceToMove, pieceToCapture;
		
		firstKey = gridIndexToKey(firstIndex);
		secondKey = gridIndexToKey(secondIndex);
		
		if (!pieces.containsKey(firstKey))
			return false;

		pieceToMove = pieces.get(firstKey);

		if (currentTeam != pieceToMove.getTeam())
			return false;

		//CASTLING
		if(pieceToMove.getTeam() == 'w'){
			
			if(! (check(whiteKing)) ){
				boolean castle = castling(firstKey, secondKey, pieceToMove);
				if(castle == true){ return true; }
			}
		
		}
		
		
		if(pieceToMove.getTeam() == 'b'){
			
			if(! (check(blackKing)) ){
				boolean castle = castling(firstKey, secondKey, pieceToMove);
				if(castle == true){ return true; }
			}
			
		}

		// check that there are no pieces in the way of movement
		// all pieces, except a knight, move in a straight line and can not jump others
		if (!(pieceToMove instanceof Knight)) {
			if (!moveInStraightLine(firstKey, secondKey)) {
				System.out.print("Illegal move, try again");
				return false;
			}

			if (pieceInTheWay(firstKey, secondKey)) {
				System.out.print("Illegal move, try again");
				return false;
			}
		}
		
		if (enPassant(pieceToMove, secondKey)) {
			setAllAdvancedTwoFalse(null);
			return true;
		}

		if (pieces.containsKey(secondKey)) {
			pieceToCapture = pieces.get(secondKey);

			if (pieceToMove.move(pieceToCapture)) {
				pieces.remove(firstKey);
				pieces.remove(secondKey);
				pieces.put(secondKey, pieceToMove);
				
				// check that user didn't put their own king in check
				// if they did, it is an illegal move
				if (currentTeam == 'w') {
					if (check(whiteKing)) {
						pieceToMove.stepBack();
						
						pieces.remove(secondKey);
						pieces.put(firstKey, pieceToMove);
						pieces.put(secondKey, pieceToCapture);

						return false;
					}
				} else {
					if (check(blackKing)) {
						pieceToMove.stepBack();
						
						pieces.remove(secondKey);
						pieces.put(firstKey, pieceToMove);
						pieces.put(secondKey, pieceToCapture);

						return false;
					}
				}

				setAllAdvancedTwoFalse(pieceToMove);

				// check for pawn promo
				if (pieceToMove instanceof Pawn) {
					pawnToPromote = (Pawn) pieceToMove;

					if (pawnPromo(pawnToPromote))
						pawnPromoFlag = true;
				}

				return true;
			}
		} else {
			if (pieceToMove.move(secondKey)) {
				pieces.remove(firstKey);
				pieces.put(secondKey, pieceToMove);
				
				// check that user didn't put their own king in check
				// if they did, it is an illegal move
				if (currentTeam == 'w') {
					if (check(whiteKing)) {
						pieceToMove.stepBack();
						
						pieces.remove(secondKey);
						pieces.put(firstKey, pieceToMove);

						return false;
					}
				} else {
					if (check(blackKing)) {
						pieceToMove.stepBack();
						
						pieces.remove(secondKey);
						pieces.put(firstKey, pieceToMove);

						return false;
					}
				}
				
				setAllAdvancedTwoFalse(pieceToMove);

				// check for pawn promo
				if (pieceToMove instanceof Pawn) {
					pawnToPromote = (Pawn) pieceToMove;

					if (pawnPromo(pawnToPromote))
						pawnPromoFlag = true;
				}

				return true;
			}
		}

		return false;
	}

	private boolean pawnPromo(Pawn movedPawn) {
		if(movedPawn.getTeam() == 'w') {
			if (movedPawn.getRow() == 8)
				return true;
		}else {
			if (movedPawn.getRow() == 1)
				return true;
		}

		return false;
	}

	/**
	 * Method to determine if the current move is the special pawn capture move
	 * <i>en passant</i>.
	 * <p>
	 * If the move is an <i>en passant</i> move the method will return true and the
	 * board will be adjusted accordingly, including: the captured pawn will be
	 * removed from the board and the capturing pawn will be moved to it's new
	 * location.
	 * 
	 * @param pieceToMove
	 *            capturing ChessPiece to move
	 * @param moveToKey
	 *            key that the capturing ChessPiece is moving to
	 * @return true if the provided move is an <i>en passant</i> move and false if
	 *         it is not
	 */
	private boolean enPassant(ChessPiece pieceToMove, String moveToKey) {
		Pawn pawnToMove, pawnToCapture;
		ChessPiece pieceToCapture;
		String keyToCapture, startKey;
		char colStart, colToMoveTo, colToCapture;
		int rowStart, rowToMoveTo, rowToCapture;

		if (!(pieceToMove instanceof Pawn))
			return false;

		pawnToMove = (Pawn) pieceToMove;

		if (pieces.containsKey(moveToKey))
			return false;

		colStart = pawnToMove.getCol();
		rowStart = pawnToMove.getRow();

		colToMoveTo = moveToKey.charAt(0);
		rowToMoveTo = Character.getNumericValue(moveToKey.charAt(1));

		colToCapture = colToMoveTo;

		if (pawnToMove.getTeam() == 'w') {
			rowToCapture = rowToMoveTo - 1;

			if (rowToCapture != 5)
				return false;
		} else {
			rowToCapture = rowToMoveTo + 1;

			if (rowToCapture != 4)
				return false;
		}

		if ((colToMoveTo != (colStart + 1)) && (colToMoveTo != (colStart - 1)))
			return false;

		keyToCapture = "" + colToCapture + rowToCapture;
		pieceToCapture = pieces.get(keyToCapture);

		if (!(pieceToCapture instanceof Pawn))
			return false;

		pawnToCapture = (Pawn) pieceToCapture;

		if (!pawnToCapture.justAdvancedTwo())
			return false;

		if (!pawnToMove.enPassantMove(moveToKey))
			return false;

		startKey = "" + colStart + rowStart;

		pieces.remove(startKey);
		pieces.remove(keyToCapture);
		pieces.put(moveToKey, pawnToMove);

		return true;
	}

	/**
	 * Sets all {@link Pawn#justAdvancedTwo justAdvancedTwo} flags to
	 * <code>false</code> on all of the pawns on the board using the
	 * {@link Pawn#setAdvancedTwoFalse() setAdvancedTwoFalse} method in each
	 * {@link Pawn} object.
	 * 
	 * @param lastPieceThatMoved
	 *            to be added
	 */
	private void setAllAdvancedTwoFalse(ChessPiece lastPieceThatMoved) {
		String tempKey;
		ChessPiece tempPiece;
		Pawn tempPawn;

		// loop through and call the method on all Pawns
		// if the pawn is the pawn to capture then save the object for later
		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				tempKey = "" + col + row;

				if (pieces.containsKey(tempKey)) {
					tempPiece = pieces.get(tempKey);

					if (tempPiece instanceof Pawn) {
						tempPawn = (Pawn) tempPiece;

						if (!tempPawn.equals(lastPieceThatMoved))
							tempPawn.setAdvancedTwoFalse();
					}
				}
			}
		}
	}

	/**
	 * Checks for chess pieces in between two locations on the board.
	 * 
	 * @param start
	 *            key for the start location
	 * @param end
	 *            key for the end location
	 * @return true if there's a piece in between the two locations and false if
	 *         there is no piece in between.
	 */
	private boolean pieceInTheWay(String start, String end) {
		char startCol, endCol;
		int startRow, endRow, colDiff, rowDiff;
		String tempKey;

		tempKey = start;

		startCol = start.charAt(0);
		startRow = Character.getNumericValue(start.charAt(1));

		endCol = end.charAt(0);
		endRow = Character.getNumericValue(end.charAt(1));

		if (startRow == endRow) {
			char c = startCol;
			if (startCol < endCol) {
				c++;
				while (c < endCol) {
					tempKey = c + tempKey.substring(1);
					if (pieces.containsKey(tempKey))
						return true;
					c++;
				}
			} else {
				c--;
				while (c > endCol) {
					tempKey = c + tempKey.substring(1);
					if (pieces.containsKey(tempKey))
						return true;
					c--;
				}
			}
		}

		if (startCol == endCol) {
			int r = startRow;
			if (startRow < endRow) {
				r++;
				while (r < endRow) {
					tempKey = tempKey.substring(0, 1) + r;
					if (pieces.containsKey(tempKey))
						return true;
					r++;
				}
			} else {
				r--;
				while (r > endRow) {
					tempKey = tempKey.substring(0, 1) + r;
					if (pieces.containsKey(tempKey))
						return true;
					r--;
				}
			}
		}

		colDiff = endCol - startCol;
		rowDiff = endRow - startRow;

		if ((Math.abs(colDiff)) == (Math.abs(rowDiff))) {
			char c = startCol;
			int r = startRow;
			if (colDiff > 0) {
				c++;
				if (rowDiff > 0) {
					r++;
					while ((c < endCol) && (r < endRow)) {
						tempKey = "" + c + r;
						if (pieces.containsKey(tempKey))
							return true;
						c++;
						r++;
					}
				} else {
					r--;
					while ((c < endCol) && (r > endRow)) {
						tempKey = "" + c + r;
						if (pieces.containsKey(tempKey))
							return true;
						c++;
						r--;
					}
				}
			} else {
				c--;
				if (rowDiff > 0) {
					r++;
					while ((c > endCol) && (r < endRow)) {
						tempKey = "" + c + r;
						if (pieces.containsKey(tempKey))
							return true;
						c--;
						r++;
					}
				} else {
					r--;
					while ((c > endCol) && (r > endRow)) {
						tempKey = "" + c + r;
						if (pieces.containsKey(tempKey))
							return true;
						c--;
						r--;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Check to see if move from a start location to an end location is in a
	 * straight line. Can be vertical, horizontal, or diagonal.
	 * 
	 * @param start
	 *            key for the start location
	 * @param end
	 *            key for the end location
	 * @return true if the move is in a straight line and false if the move is not
	 *         in a straight line.
	 */
	private boolean moveInStraightLine(String start, String end) {
		char startCol, endCol;
		int startRow, endRow, colDiff, rowDiff;

		startCol = start.charAt(0);
		endCol = end.charAt(0);

		if (startCol == endCol)
			return true;

		startRow = Character.getNumericValue(start.charAt(1));
		endRow = Character.getNumericValue(end.charAt(1));

		if (startRow == endRow)
			return true;

		colDiff = Math.abs(endCol - startCol);
		rowDiff = Math.abs(endRow - startRow);

		if (colDiff == rowDiff)
			return true;

		return false;
	}

	/**
	 * Determine if given King is checked.
	 * <p>
	 * This method determines if it is possible for any of the oppenent's pieces to
	 * capture the King. This method searches for every piece on the board, checks
	 * if the piece is the King's opponent, and determines if it is in a location
	 * that can capture the given King.
	 * 
	 * @param king
	 *            the king to determine if it is checked or not
	 * @return true if the given King is in check and false if it is not
	 */
	private boolean check(King king) {
		String key, kingKey;
		ChessPiece piece;

		kingKey = "" + king.getCol() + king.getRow();

		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				key = "" + col + row;

				if (pieces.containsKey(key)) {
					piece = pieces.get(key);

					if (piece.getTeam() != king.getTeam())
						if (piece instanceof Knight) {
							if (piece.canMove(king))
								return true;
						} else if ((moveInStraightLine(kingKey, key)) && (!pieceInTheWay(kingKey, key))) {
							if (piece.canMove(king))
								return true;
						}
				}
			}
		}

		return false;
	}

	/**
	 * Determine if given King is in checkmate.
	 * <p>
	 * This method determines if it is possible for the given King to get out of
	 * check. This method first determines if the king can move to another position
	 * where it is not in check. The method then checks if other pieces can be moved
	 * to remove the check.
	 * 
	 * @param king
	 *            the king to determine if it is in checkmate
	 * @return true if the given King is in checkmate and false if it is not
	 */
	private boolean checkmate(King king) {
		ChessPiece piece, movingPiece;
		String startKey, endKey;
		char startCol;
		int startRow;

		if (!check(king))
			return false;

		// see if king can move
		startCol = king.getCol();
		startRow = king.getRow();
		startKey = "" + startCol + startRow;

		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				endKey = "" + col + row;

				if (pieces.containsKey(endKey)) {
					piece = pieces.get(endKey);

					if (king.move(piece)) {
						pieces.remove(endKey);
						pieces.remove(startKey);
						pieces.put(endKey, king);

						if (!check(king)) {
							king.stepBack();

							pieces.put(startKey, king);
							pieces.put(endKey, piece);

							return false;
						} else {
							king.stepBack();

							pieces.put(startKey, king);
							pieces.put(endKey, piece);
						}
					}
				} else if (king.move(endKey)) {
					pieces.remove(startKey);
					pieces.put(endKey, king);

					if (!check(king)) {
						king.stepBack();

						pieces.remove(endKey);
						pieces.put(startKey, king);

						return false;
					} else {
						king.stepBack();

						pieces.remove(endKey);
						pieces.put(startKey, king);
					}
				}
			}
		}

		// see if any movement by the king's teammates will remove the check
		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				startKey = "" + col + row;
				startCol = col;
				startRow = row;

				if (pieces.containsKey(startKey)) {
					movingPiece = pieces.get(startKey);

					if (movingPiece.getTeam() == king.getTeam()) {
						// loop through again to see if the piece can move to take king out of check
						for (char c = 'a'; c <= 'h'; c++) {
							for (int r = 1; r <= 8; r++) {
								endKey = "" + c + r;

								boolean validMove = true;

								if (!(movingPiece instanceof Knight)) {
									if (!moveInStraightLine(startKey, endKey))
										validMove = false;

									if (pieceInTheWay(startKey, endKey))
										validMove = false;
								}

								if (validMove) {
									if (pieces.containsKey(endKey)) {
										piece = pieces.get(endKey);

										if (movingPiece.move(piece)) {
											pieces.remove(endKey);
											pieces.remove(startKey);
											pieces.put(endKey, movingPiece);

											if (!check(king)) {
												movingPiece.stepBack();

												pieces.put(startKey, movingPiece);
												pieces.put(endKey, piece);

												return false;
											} else {
												movingPiece.stepBack();

												pieces.put(startKey, movingPiece);
												pieces.put(endKey, piece);
											}
										}
									} else if (movingPiece.move(endKey)) {
										pieces.remove(startKey);
										pieces.put(endKey, movingPiece);

										if (!check(king)) {
											movingPiece.stepBack();

											pieces.remove(endKey);
											pieces.put(startKey, movingPiece);

											return false;
										} else {
											movingPiece.stepBack();

											pieces.remove(endKey);
											pieces.put(startKey, movingPiece);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Determine if the game is in a stalemate.
	 * <p>
	 * Searches for available moves for the king and the king's teammates. If an
	 * available move is found, the game is not in a stalemate and the method
	 * returns true.
	 * 
	 * @param king
	 *            the king to determine if it's team is in a stalemate
	 * @return true if the game is in a stalemate and false if it is not
	 */
	private boolean stalemate(King king) {
		ChessPiece piece, movingPiece;
		String startKey, endKey;

		// see if any pieces on the king's team can move
		for (char col = 'a'; col <= 'h'; col++) {
			for (int row = 1; row <= 8; row++) {
				startKey = "" + col + row;

				if (pieces.containsKey(startKey)) {
					movingPiece = pieces.get(startKey);

					if (movingPiece.getTeam() == king.getTeam()) {
						// loop through again to see if the piece can move without going into check
						for (char c = 'a'; c <= 'h'; c++) {
							for (int r = 1; r <= 8; r++) {
								endKey = "" + c + r;

								boolean validMove = true;

								if (!(movingPiece instanceof Knight)) {
									if (!moveInStraightLine(startKey, endKey))
										validMove = false;

									if (pieceInTheWay(startKey, endKey))
										validMove = false;
								}

								if (validMove) {
									if (pieces.containsKey(endKey)) {
										piece = pieces.get(endKey);

										if (movingPiece.move(piece)) {
											pieces.remove(endKey);
											pieces.remove(startKey);
											pieces.put(endKey, movingPiece);

											if (!check(king)) {
												movingPiece.stepBack();

												pieces.put(startKey, movingPiece);
												pieces.put(endKey, piece);

												return false;
											} else {
												movingPiece.stepBack();

												pieces.put(startKey, movingPiece);
												pieces.put(endKey, piece);
											}
										}
									} else if (movingPiece.move(endKey)) {
										pieces.remove(startKey);
										pieces.put(endKey, movingPiece);

										if (!check(king)) {
											movingPiece.stepBack();

											pieces.remove(endKey);
											pieces.put(startKey, movingPiece);

											return false;
										} else {
											movingPiece.stepBack();

											pieces.remove(endKey);
											pieces.put(startKey, movingPiece);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * method which fills up the board with the chess pieces for each team, so they can be displayed
	 */
	private void populateBoard() {
		ChessPiece piece;
		char col;
		int row;
		String key;
		
		//make and put white pawns
		col = 'a';
		row = 2;
		
		while(col <= 'h') {
			key = "" + col + row;
			piece = new Pawn(col, row, 'w');
			
			pieces.put(key, piece);
			
			col++;
		}
		
		//make and put black pawns
		col = 'a';
		row = 7;
		
		while(col <= 'h') {
			key = "" + col + row;
			piece = new Pawn(col, row, 'b');
			
			pieces.put(key, piece);
			
			col++;
		}
		
		//make and put white knights
		col = 'b';
		row = 1;
		key = "" + col + row;
		piece = new Knight(col, row, 'w');
		pieces.put(key, piece);
		
		col = 'g';
		row = 1;
		key = "" + col + row;
		piece = new Knight(col, row, 'w');
		pieces.put(key, piece);
		
		//make and put black knights
		col = 'b';
		row = 8;
		key = "" + col + row;
		piece = new Knight(col, row, 'b');
		pieces.put(key, piece);
		
		col = 'g';
		row = 8;
		key = "" + col + row;
		piece = new Knight(col, row, 'b');
		pieces.put(key, piece);
		
		//make and put white bishops
		col = 'c';
		row = 1;
		key = "" + col + row;
		piece = new Bishop(col, row, 'w');
		pieces.put(key, piece);
		
		col = 'f';
		row = 1;
		key = "" + col + row;
		piece = new Bishop(col, row, 'w');
		pieces.put(key, piece);
		
		//make and put black bishops
		col = 'c';
		row = 8;
		key = "" + col + row;
		piece = new Bishop(col, row, 'b');
		pieces.put(key, piece);
		
		col = 'f';
		row = 8;
		key = "" + col + row;
		piece = new Bishop(col, row, 'b');
		pieces.put(key, piece);
		
		//make and put white rooks
		col = 'a';
		row = 1;
		key = "" + col + row;
		piece = new Rook(col, row, 'w');
		pieces.put(key, piece);
		
		col = 'h';
		row = 1;
		key = "" + col + row;
		piece = new Rook(col, row, 'w');
		pieces.put(key, piece);
		
		//make and put black rooks
		col = 'a';
		row = 8;
		key = "" + col + row;
		piece = new Rook(col, row, 'b');
		pieces.put(key, piece);
		
		col = 'h';
		row = 8;
		key = "" + col + row;
		piece = new Rook(col, row, 'b');
		pieces.put(key, piece);
		
		//make and put white queen
		col = 'd';
		row = 1;
		key = "" + col + row;
		piece = new Queen(col, row, 'w');
		pieces.put(key, piece);
		
		//make and put black queen
		col = 'd';
		row = 8;
		key = "" + col + row;
		piece = new Queen(col, row, 'b');
		pieces.put(key, piece);
		
		//make and put white king
		col = 'e';
		row = 1;
		key = "" + col + row;
		piece = new King(col, row, 'w');
		pieces.put(key, piece);
		whiteKing = (King) piece;
		
		//make and put black king
		col = 'e';
		row = 8;
		key = "" + col + row;
		piece = new King(col, row, 'b');
		pieces.put(key, piece);
		blackKing = (King) piece;
	}
	
	/**
	 * will check to see if you are wishing to perform the special case of castling with your king and rook, move the king 2 spots in that direction and then placing the rook on the other side of the king
	 * <p>
	 * Conditions for Castling:
	 * <p>
	 * 1.) The king and rook (on the side you wish to castle to) have not been moved before (both pieces 1st move)
	 * <p>
	 * 2.) There are no other pieces in between your king and this rook
	 * <p>
	 * 3.) The king may not castle through check in 1st of its 2 move to the desired side, and the king may not land in check when moving to the 2nd spot (same is also true for check mate)
	 * <p>
	 * 4.) You are not allowed to castle as a move to escape a check from your opponent
	 *
	 * @param firstKey spot from you wish to move the piece
	 * @param secondKey spot in which you wish to move the piece
	 * @param pieceToMove the piece in which you wish to move
	 * @return true if the conditions of castling are met and the actions were performed, false if not
	 */
	private boolean castling(String firstKey, String secondKey, ChessPiece pieceToMove){
		
		//check if you are moving the king
		if(!(pieceToMove instanceof King)){ return false; }
		
		//check if the king has moved before
		King tempK = (King) pieceToMove;
		if(tempK.firstMove == false){ return false; }
		
		//check if you are trying to move king 2 spots
		if(!secondKey.equals("c1") && !secondKey.equals("g1") && !secondKey.equals("c8") && !secondKey.equals("g8") ){ return false; }
		
		//white left side castle
		if(secondKey.equals("c1")){
			
			//check if rook is still there
			if(pieces.get("a1") == null){ return false; }
			
			//check to see if piece there is a rook and the correct team
			ChessPiece temp = pieces.get("a1");
			if(!(temp instanceof Rook) || temp.team != 'w'){ return false; }
			
			//check to see if the rook has moved before
			Rook tempR = (Rook)pieces.get("a1");
			if(tempR.firstMove == false){ return false; }
			
			//check to see if any pieces are in between the rook and king
			if (pieceInTheWay("e1", "a1") ){ return false; }
			
			//check to make sure you are not castling through a check
			//move piece 1st of 2 moves, e1 -> d1
			if(pieceToMove.move("d1")) {
				pieces.remove("e1");
				pieces.put("d1", pieceToMove);
					
				//seeing if check thru occurs, if it does move piece back, set 1st move to true since the move never actually happened
				if (check(whiteKing)) {
					//move back from d1 -> e1
					if(pieceToMove.move("e1")) { pieces.remove("d1"); pieces.put("e1", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
				
			}
				
			//check to make sure you are not castling into a check
			//move piece 2nd of 2 moves, d1 -> c1 | (e1 -> d1 -> c1)
			if(pieceToMove.move("c1")) {
				pieces.remove("d1");
				pieces.put("c1", pieceToMove);
					
				//see if king moved into a check, if so undo the moves, and set 1st move back to true
				if (check(whiteKing)) {
					//move back c1 -> d1
					if(pieceToMove.move("d1")) { pieces.remove("c1"); pieces.put("d1", pieceToMove); }
					//move back d1 -> e1
					if(pieceToMove.move("e1")) { pieces.remove("d1"); pieces.put("e1", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
			}
				
			//move rook to right side of king, set movement to false, a1 -> d1
			tempR.firstMove = false;
			temp.move("d1");
			pieces.remove("a1");
			pieces.put("d1", temp);
		}
		
		//white right side castle
		if(secondKey.equals("g1")){
			
			//check if rook is still there
			if(pieces.get("h1") == null){ return false; }
				
			//check to see if piece there is a rook and the correct team
			ChessPiece temp = pieces.get("h1");
			if(!(temp instanceof Rook) || temp.team != 'w'){ return false; }
				
			//check to see if the rook has moved before
			Rook tempR = (Rook)pieces.get("h1");
			if(tempR.firstMove == false){ return false; }
				
			//check to see if any pieces are in between the rook and king
			if (pieceInTheWay("e1", "h1") ){ return false; }
					
			//check to make sure you are not castling through a check
			//move piece 1st of 2 moves, e1 -> f1
			if(pieceToMove.move("f1")) {
				pieces.remove("e1");
				pieces.put("f1", pieceToMove);
						
				//seeing if check thru occurs, if it does move piece back, set 1st move to true since the move never actually happened
				if (check(whiteKing)) {
					//move back from f1 -> e1
					if(pieceToMove.move("e1")) { pieces.remove("f1"); pieces.put("e1", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}						
			}
						
			//check to make sure you are not castling into a check
			//move piece 2nd of 2 moves, f1 -> g1 | (e1 -> f1 -> g1)
			if(pieceToMove.move("g1")) {
				pieces.remove("f1");
				pieces.put("g1", pieceToMove);
				//see if king moved into a check, if so undo the moves, and set 1st move back to true
				if (check(whiteKing)) {
					//move back g1 -> f1
					if(pieceToMove.move("f1")) { pieces.remove("g1"); pieces.put("f1", pieceToMove); }
					//move back f1 -> e1
					if(pieceToMove.move("e1")) { pieces.remove("f1"); pieces.put("e1", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
			}
						
			//move rook to left side of king, set movement to false, h1 -> f1
			tempR.firstMove = false;
			temp.move("f1");
			pieces.remove("h1");
			pieces.put("f1", temp);
		}
		
		//black left side castle, towards A column
		if(secondKey.equals("c8")){
					
			//check if rook is still there
			if(pieces.get("a8") == null){ return false; }
					
			//check to see if piece there is a rook and the correct team
			ChessPiece temp = pieces.get("a8");
			if(!(temp instanceof Rook) || temp.team != 'b'){ return false; }
					
			//check to see if the rook has moved before
			Rook tempR = (Rook)pieces.get("a8");
			if(tempR.firstMove == false){ return false; }
					
			//check to see if any pieces are in between the rook and king
			if (pieceInTheWay("e8", "a8") ){ return false; }
					
			//check to make sure you are not castling through a check
			//move piece 1st of 2 moves, e8 -> d8
			if(pieceToMove.move("d8")) {
				pieces.remove("e8");
				pieces.put("d8", pieceToMove);
							
				//seeing if check thru occurs, if it does move piece back, set 1st move to true since the move never actually happened
				if (check(blackKing)) {
					//move back from d8 -> e8
					if(pieceToMove.move("e8")) { pieces.remove("d8"); pieces.put("e8", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
			}
						
			//check to make sure you are not castling into a check
			//move piece 2nd of 2 moves, d8 -> c8 | (e8 -> d8 -> c8)
			if(pieceToMove.move("c8")) {
				pieces.remove("d8");
				pieces.put("c8", pieceToMove);
							
				//see if king moved into a check, if so undo the moves, and set 1st move back to true
				if (check(blackKing)) {
					//move back c8 -> d8
					if(pieceToMove.move("d8")) { pieces.remove("c8"); pieces.put("d8", pieceToMove); }
					//move back d8 -> e8
					if(pieceToMove.move("e8")) { pieces.remove("d8"); pieces.put("e8", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
			}
						
			//move rook to right side of king, set movement to false, a8 -> d8
			tempR.firstMove = false;
			temp.move("d8");
			pieces.remove("a8");
			pieces.put("d8", temp);
		}		
		
		//black right side castle, towards H column
		if(secondKey.equals("g8")){
					
			//check if rook is still there
			if(pieces.get("h8") == null){ return false; }
						
			//check to see if piece there is a rook and the correct team
			ChessPiece temp = pieces.get("h8");
			if(!(temp instanceof Rook) || temp.team != 'b'){ return false; }
						
			//check to see if the rook has moved before
			Rook tempR = (Rook)pieces.get("h8");
			if(tempR.firstMove == false){ return false; }
						
			//check to see if any pieces are in between the rook and king
			if (pieceInTheWay("e8", "h8") ){ return false; }
							
			//check to make sure you are not castling through a check
			//move piece 1st of 2 moves, e8 -> f8
			if(pieceToMove.move("f8")) {
				pieces.remove("e8");
				pieces.put("f8", pieceToMove);
								
				//seeing if check thru occurs, if it does move piece back, set 1st move to true since the move never actually happened
				if (check(blackKing)) {
					//move back from f8 -> e8
					if(pieceToMove.move("e8")) { pieces.remove("f8"); pieces.put("e8", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}						
			}
								
			//check to make sure you are not castling into a check
			//move piece 2nd of 2 moves, f8 -> g8 | (e8 -> f8 -> g8)
			if(pieceToMove.move("g8")) {
				pieces.remove("f8");
				pieces.put("g8", pieceToMove);
				//see if king moved into a check, if so undo the moves, and set 1st move back to true
				if (check(blackKing)) {
					//move back g8 -> f8
					if(pieceToMove.move("f8")) { pieces.remove("g8"); pieces.put("f8", pieceToMove); }
					//move back f8 -> e8
					if(pieceToMove.move("e8")) { pieces.remove("f8"); pieces.put("e8", pieceToMove); }
					tempK.firstMove = true;
					return false;
				}
			}
								
			//move rook to left side of king, set movement to false, h8 -> f8
			tempR.firstMove = false;
			temp.move("f8");
			pieces.remove("h8");
			pieces.put("f8", temp);
		}		
		
		//all conditions were properly met and the pieces were properly moved, return castling move complete
		return true;
	}

}
