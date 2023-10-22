package chess;

import java.awt.Point;
import java.util.ArrayList;

public class Position {

	private int[][] board;
	private boolean turn;
	private boolean whiteCanCastleKingSide, whiteCanCastleQueenSide;
	private boolean blackCanCastleKingSide, blackCanCastleQueenSide;
	private ArrayList<Move> moveHistory;
	private int[][] whitePieceLocations, blackPieceLocations;
	private Point whiteKingLocation, blackKingLocation;

	public Position(int[][] board, boolean turn, boolean whiteCanCastleKingSide, boolean whiteCanCastleQueenSide,
			boolean blackCanCastleKingSide, boolean blackCanCastleQueenSide, ArrayList<Move> moveHistory,
			int[][] whitePieceLocations, int[][] blackPieceLocations, Point whiteKingLocation,
			Point blackKingLocation) {
		this.board = board;
		this.turn = turn;
		this.whiteCanCastleKingSide = whiteCanCastleKingSide;
		this.whiteCanCastleQueenSide = whiteCanCastleQueenSide;
		this.blackCanCastleKingSide = blackCanCastleKingSide;
		this.blackCanCastleQueenSide = blackCanCastleQueenSide;
		this.moveHistory = moveHistory;
		this.whitePieceLocations = whitePieceLocations;
		this.blackPieceLocations = blackPieceLocations;
		this.whiteKingLocation = whiteKingLocation;
		this.blackKingLocation = blackKingLocation;
		setCorrectKingLocations();
	}

	public Position clone() {
		return new Position(MoveGenerator.clone(this.getBoard()), this.isTurn(), this.isWhiteCanCastleKingSide(),
				this.isWhiteCanCastleQueenSide(), this.isBlackCanCastleKingSide(),
				this.isBlackCanCastleQueenSide(), MoveGenerator.clone(this.getMoveHistory()),
				MoveGenerator.clone(this.getWhitePieceLocations()), MoveGenerator.clone(this.getBlackPieceLocations()),
				new Point(this.getWhiteKingLocation().x, this.getWhiteKingLocation().y),
				new Point(this.getWhiteKingLocation().x, this.getWhiteKingLocation().y));
	}

	public void setCorrectKingLocations() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 5) {
					whiteKingLocation = new Point(j, i);
				} else if (board[i][j] == 11) {
					blackKingLocation = new Point(j, i);
				}
			}
		}
	}

	public void setCorrectPieceLocations() {

	}

	public Point getWhiteKingLocation() {
		return whiteKingLocation;
	}

	public void setWhiteKingLocation(Point whiteKingLocation) {
		this.whiteKingLocation = whiteKingLocation;
	}

	public Point getBlackKingLocation() {
		return blackKingLocation;
	}

	public void setBlackKingLocation(Point blackKingLocation) {
		this.blackKingLocation = blackKingLocation;
	}

	public void replacePiece(int x, int y, int newPiece) {
		board[y][x] = newPiece;
	}

	public int[][] getBoard() {
		return board;
	}

	public boolean isWhiteCanCastleKingSide() {
		return whiteCanCastleKingSide;
	}

	public boolean isWhiteCanCastleQueenSide() {
		return whiteCanCastleQueenSide;
	}

	public boolean isBlackCanCastleKingSide() {
		return blackCanCastleKingSide;
	}

	public boolean isBlackCanCastleQueenSide() {
		return blackCanCastleQueenSide;
	}

	public ArrayList<Move> getMoveHistory() {
		return moveHistory;
	}

	public void setWhiteCanCastleKingSide(boolean whiteCanCastleKingSide) {
		this.whiteCanCastleKingSide = whiteCanCastleKingSide;
	}

	public void setWhiteCanCastleQueenSide(boolean whiteCanCastleQueenSide) {
		this.whiteCanCastleQueenSide = whiteCanCastleQueenSide;
	}

	public void setBlackCanCastleKingSide(boolean blackCanCastleKingSide) {
		this.blackCanCastleKingSide = blackCanCastleKingSide;
	}

	public void setBlackCanCastleQueenSide(boolean blackCanCastleQueenSide) {
		this.blackCanCastleQueenSide = blackCanCastleQueenSide;
	}

	public int[][] getWhitePieceLocations() {
		return whitePieceLocations;
	}

	public void setWhitePieceLocations(int[][] whitePieceLocations) {
		this.whitePieceLocations = whitePieceLocations;
	}

	public void setBlackPieceLocations(int[][] blackPieceLocations) {
		this.blackPieceLocations = blackPieceLocations;
	}

	public int[][] getBlackPieceLocations() {
		return blackPieceLocations;
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean b) {
		turn = b;
	}

}
