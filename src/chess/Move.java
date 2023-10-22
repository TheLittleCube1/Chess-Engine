package chess;

import java.awt.Point;

import data.Pieces;
import states.GameState;

public class Move {

	public int fromX, fromY, toX, toY;
	public int promotion;

	public Move(int fromX, int fromY, int toX, int toY, int promotion) {
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
		this.promotion = promotion;
	}

	public Position playMove(Position position) {

		int[][] board = position.getBoard();
		int movingColor = Pieces.color(board[fromY][fromX]);

		if (MoveGenerator.isEnPassant(position, this)) {
			position.replacePiece(toX, fromY, 12);
		}

		int castlingStatus = MoveGenerator.isCastling(position, this);
		if (castlingStatus == 1 && position.isWhiteCanCastleKingSide()) {
			position.replacePiece(5, 0, 3);
			position.replacePiece(7, 0, 12);
		} else if (castlingStatus == 2 && position.isWhiteCanCastleQueenSide()) {
			position.replacePiece(3, 0, 3);
			position.replacePiece(0, 0, 12);
		} else if (castlingStatus == 3 && position.isBlackCanCastleKingSide()) {
			position.replacePiece(5, 7, 9);
			position.replacePiece(7, 7, 12);
		} else if (castlingStatus == 4 && position.isBlackCanCastleQueenSide()) {
			position.replacePiece(3, 7, 9);
			position.replacePiece(0, 7, 12);
		}

		if (board[fromY][fromX] == 5) {
			position.setWhiteCanCastleKingSide(false);
			position.setWhiteCanCastleQueenSide(false);
		} else if (board[fromY][fromX] == 11) {
			position.setBlackCanCastleKingSide(false);
			position.setBlackCanCastleQueenSide(false);
		} else if ((toX == 0 && toY == 0) || (fromX == 0 && fromY == 0)) {
			position.setWhiteCanCastleQueenSide(false);
		} else if ((toX == 7 && toY == 0) || (fromX == 7 && fromY == 0)) {
			position.setWhiteCanCastleKingSide(false);
		} else if ((toX == 0 && toY == 7) || (fromX == 0 && fromY == 7)) {
			position.setBlackCanCastleQueenSide(false);
		} else if ((toX == 7 && toY == 7) || (fromX == 7 && fromY == 7)) {
			position.setBlackCanCastleKingSide(false);
		}

		int[][] locations;
		if (movingColor == 1) {
			locations = position.getWhitePieceLocations();
		} else {
			locations = position.getBlackPieceLocations();
		}

		int[] square = { fromX, fromY };
		int[] newSquare = { toX, toY };
		int index = 0;
		for (int i = 0; i < 16; i++) {
			if (locations[i][0] == square[0] && locations[i][1] == square[1]) {
				index = i;
				break;
			}
		}

		locations[index] = newSquare;

		if (promotion != 0) {
			int piece = promotion;
			if (movingColor == 0) {
				piece += 6;
			}
			position.getBoard()[fromY][fromX] = piece;
		}

		if (board[fromY][fromX] == 5) {
			position.setWhiteKingLocation(new Point(toX, toY));
		} else if (board[fromY][fromX] == 11) {
			position.setBlackKingLocation(new Point(toX, toY));
		}

		position.replacePiece(toX, toY, board[fromY][fromX]);
		position.replacePiece(fromX, fromY, 12);

		position.setTurn(!position.isTurn());

		position.getMoveHistory().add(this);

		return position;

	}

	public String toString() {
		String s = "";
		s += GameState.FILES[fromX];
		s += (fromY + 1);
		s += GameState.FILES[toX];
		s += (toY + 1);
		return s;
	}

}
