package chess;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import data.Pieces;

public class MoveGenerator {

	public static long pseudoLegal = 0;
	public static long check = 0;
	public static DecimalFormat to2DecimalPlaces = new DecimalFormat("0.00");
	
	public static long pawn = 0, knight = 0, bishop = 0, rook = 0, queen = 0, king = 0;
	public static float total = 0;
	
	public static long perft(Position position, int depth) {
		if (depth == 0) {
			return 1;
		}
		long total = 0;
		ArrayList<Move> legals = generateLegalMoves(position, position.isTurn());
		Position testPosition = position.clone();
		for (Move move : legals) {
			move.playMove(testPosition);
			total += perft(testPosition, depth - 1);
			testPosition = position.clone();
		}
		return total;
	}

	public static ArrayList<Move> generateLegalMoves(Position position, boolean movingColor) {
		ArrayList<Move> moves = new ArrayList<Move>();
		long start = System.nanoTime();
		int[][] board = position.getBoard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (Pieces.nonEmptyColor(board[i][j]) != movingColor) {
					continue;
				}
				long startOfPseudoLegals = System.nanoTime();
				moves.addAll(generateLegalMovesFromSquare(position, j, i));
//				System.out.println(System.nanoTime() - startOfPseudoLegals);
			}
		}
		total += System.nanoTime() - start;
//		System.out.println("Pawn: " + to2DecimalPlaces.format(pawn / total * 100) + "%");
//		System.out.println("Knight: " + to2DecimalPlaces.format(knight / total * 100) + "%");
//		System.out.println("Bishop: " + to2DecimalPlaces.format(bishop / total * 100) + "%");
//		System.out.println("Rook: " + to2DecimalPlaces.format(rook / total * 100) + "%");
//		System.out.println("Queen: " + to2DecimalPlaces.format(queen / total * 100) + "%");
//		System.out.println("King: " + to2DecimalPlaces.format(king / total * 100) + "%");
//		System.out.println();
		return moves;
	}

	public static ArrayList<Move> generateLegalMovesFromSquare(Position position, int fromX, int fromY) {
		int[][] board = position.getBoard();
		long start = System.nanoTime();
		ArrayList<Move> moves = generatePseudoLegalMovesFromSquare(position, fromX, fromY);
//		System.out.println("generatePseudoLegalMovesFromSquare: " + (System.nanoTime() - start));
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		int movingColor = Pieces.color(board[fromY][fromX]);
		
		long startOfCheck = System.nanoTime();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			Position positionClone = clone(position);
			move.playMove(positionClone);
			if (movingColor == 1) {
				if (!whiteInCheck(positionClone)) {
					legalMoves.add(move);
				}
			} else if (movingColor == 0) {
				if (!blackInCheck(positionClone)) {
					legalMoves.add(move);
				}
			}
		}
//		System.out.println("Check: " + (System.nanoTime() - startOfCheck));
//		System.out.println();

		return legalMoves;
	}

	public static ArrayList<Move> generatePseudoLegalMoves(Position position, int movingColor) {
		ArrayList<Move> moves = new ArrayList<Move>();
		int[][] board = position.getBoard();
		for (int fromX = 0; fromX < 8; fromX++) {
			for (int fromY = 0; fromY < 8; fromY++) {

				int piece = board[fromY][fromX];
				int squareColor = Pieces.color(piece);

				// Moving opponent piece
				if (squareColor != movingColor) {
					continue;
				}

				moves.addAll(generatePseudoLegalMovesFromSquare(position, fromX, fromY));

			}
		}
		return moves;
	}

	public static ArrayList<Move> generatePseudoLegalMovesFromSquare(Position position, int fromX, int fromY) {
		ArrayList<Move> moves = new ArrayList<Move>();
		int[][] board = position.getBoard();
		int piece = board[fromY][fromX];
		
		long start = System.nanoTime();
		
		// White Pawn
		if (piece == 0) {
			moves.addAll(whitePawnMoves(position, fromX, fromY));
			pawn += System.nanoTime() - start;
		}

		// Black Pawn
		else if (piece == 6) {
			moves.addAll(blackPawnMoves(position, fromX, fromY));
			pawn += System.nanoTime() - start;
		}

		// Knight
		else if (piece == 1 || piece == 7) {
			moves.addAll(knightMoves(position, fromX, fromY));
			knight += System.nanoTime() - start;
		}

		// Bishop, Rook, or Queen
		else if (piece == 2 || piece == 3 || piece == 4 || piece == 8 || piece == 9 || piece == 10) {
			moves.addAll(slidingMoves(position, fromX, fromY, piece));
			if (piece == 2 || piece == 8) {
				bishop += System.nanoTime() - start;
			} else if (piece == 3 || piece == 9) {
				rook += System.nanoTime() - start;
			} else if (piece == 4 || piece == 10) {
				queen += System.nanoTime() - start;
			}
		}

		// King
		else if (piece == 5 || piece == 11) {
			moves.addAll(kingMoves(position, fromX, fromY));
			king += System.nanoTime() - start;
		}

		// Capturing own piece
		ArrayList<Move> modifiedMoves = new ArrayList<Move>();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			boolean withinBoard = withinBoard(move.toX, move.toY);
			if (!withinBoard) {
				continue;
			}
			int startPiece = board[move.fromY][move.fromX];
			int startColor = Pieces.color(startPiece);
			int targetPiece = board[move.toY][move.toX];
			int targetColor = Pieces.color(targetPiece);
			if (startColor != targetColor) {
				modifiedMoves.add(move);
			}
		}

		return modifiedMoves;
	}

	public static boolean someoneInCheck(Position position) {
		if (whiteInCheck(position))
			return true;
		if (blackInCheck(position))
			return true;
		return false;
	}

	public static boolean whiteInCheck(Position position) {

		Point kingSquare = position.getWhiteKingLocation();
		int kingX = kingSquare.x, kingY = kingSquare.y;
		int[][] board = position.getBoard();

		// Check for pawns
		int x = kingX - 1;
		int y = kingY + 1;
		if (withinBoard(x, y)) {
			if (board[y][x] == 6) {
				return true;
			}
		}
		x = kingX + 1;
		if (withinBoard(x, y)) {
			if (board[y][x] == 6) {
				return true;
			}
		}

		// Check for knights
		int[][] jumps = { { -1, -2 }, { 1, -2 }, { -2, -1 }, { 2, -1 }, { -2, 1 }, { 2, 1 }, { -1, 2 }, { 1, 2 } };
		for (int[] jump : jumps) {
			x = kingX + jump[0];
			y = kingY + jump[1];
			if (!withinBoard(x, y))
				continue;
			if (board[y][x] == 7)
				return true;
		}

		// Check for bishops
		int[][] directions = Pieces.BISHOP_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 8) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for rooks
		directions = Pieces.ROOK_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 9) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for queens
		directions = Pieces.QUEEN_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 10) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for kings
		directions = Pieces.QUEEN_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			if (!withinBoard(x, y)) {
				continue;
			}
			if (board[y][x] == 11) {
				return true;
			}
		}

		return false;

	}

	public static boolean blackInCheck(Position position) {

		Point kingSquare = position.getBlackKingLocation();
		int kingX = kingSquare.x, kingY = kingSquare.y;
		int[][] board = position.getBoard();

		// Check for pawns
		int x = kingX - 1;
		int y = kingY - 1;
		if (withinBoard(x, y)) {
			if (board[y][x] == 0) {
				return true;
			}
		}
		x = kingX + 1;
		if (withinBoard(x, y)) {
			if (board[y][x] == 0) {
				return true;
			}
		}

		// Check for knights
		int[][] jumps = { { -1, -2 }, { 1, -2 }, { -2, -1 }, { 2, -1 }, { -2, 1 }, { 2, 1 }, { -1, 2 }, { 1, 2 } };
		for (int[] jump : jumps) {
			x = kingX + jump[0];
			y = kingY + jump[1];
			if (!withinBoard(x, y))
				continue;
			if (board[y][x] == 1)
				return true;
		}

		// Check for bishops
		int[][] directions = Pieces.BISHOP_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 2) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for rooks
		directions = Pieces.ROOK_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 3) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for queens
		directions = Pieces.QUEEN_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			while (withinBoard(x, y)) {
				if (board[y][x] == 4) {
					return true;
				} else if (board[y][x] != 12) {
					break;
				}
				x += direction[0];
				y += direction[1];
			}
		}

		// Check for kings
		directions = Pieces.QUEEN_DIRECTIONS;
		for (int[] direction : directions) {
			x = kingX + direction[0];
			y = kingY + direction[1];
			if (!withinBoard(x, y)) {
				continue;
			}
			if (board[y][x] == 5) {
				return true;
			}
		}

		return false;

	}

	public static Set<Point> generateAttackedSquares(Position position, int movingColor) {
		Set<Point> squares = new HashSet<Point>();
		int[][] board = position.getBoard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int piece = board[i][j];
				if (Pieces.color(piece) == movingColor) {
					ArrayList<Point> squaresFromPiece = generateAttackedSquaresFromSquare(position, j, i);
					squares.addAll(squaresFromPiece);
				}
			}
		}
		return squares;
	}

	public static ArrayList<Point> generateAttackedSquaresFromSquare(Position position, int fromX, int fromY) {

		int[][] board = position.getBoard();
		int piece = board[fromY][fromX];

		// White Pawn
		if (piece == 0) {
			return whitePawnSquares(position, fromX, fromY);
		}

		// Black Pawn
		else if (piece == 6) {
			return blackPawnSquares(position, fromX, fromY);
		}

		// Knight
		else if (piece == 1 || piece == 7) {
			return knightSquares(position, fromX, fromY);
		}

		// Bishop, Rook, or Queen
		else if (piece == 2 || piece == 3 || piece == 4 || piece == 8 || piece == 9 || piece == 10) {
			return slidingSquares(position, fromX, fromY, piece);
		}

		// King
		else {
			return kingSquares(position, fromX, fromY);
		}
	}

	public static ArrayList<Move> whitePawnMoves(Position position, int fromX, int fromY) {
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> moveHistory = position.getMoveHistory();
		int[][] board = position.getBoard();

		// Vertical Movement
		if (fromY == 1) {
			Move move = new Move(fromX, 1, fromX, 2, 0);
			if (board[2][fromX] == 12) {
				moves.add(clone(move));
			}
			move.toY = 3;
			if (board[2][fromX] == 12 && board[3][fromX] == 12) {
				moves.add(clone(move));
			}
		} else {
			if (board[fromY + 1][fromX] == 12) {
				if (fromY == 6) {
					Move move = new Move(fromX, fromY, fromX, fromY + 1, 1);
					moves.add(clone(move));
					move.promotion = 2;
					moves.add(clone(move));
					move.promotion = 3;
					moves.add(clone(move));
					move.promotion = 4;
					moves.add(clone(move));
				} else {
					Move move = new Move(fromX, fromY, fromX, fromY + 1, 0);
					moves.add(clone(move));
				}
			}
		}

		// Normal Capturing
		if (fromY < 7) {
			if (fromX > 0) {
				if (Pieces.color(board[fromY + 1][fromX - 1]) == 0) {
					Move move = new Move(fromX, fromY, fromX - 1, fromY + 1, 0);
					if (fromY == 6) {
						move.promotion = 1;
						moves.add(clone(move));
						move.promotion = 2;
						moves.add(clone(move));
						move.promotion = 3;
						moves.add(clone(move));
						move.promotion = 4;
						moves.add(clone(move));
					} else {
						moves.add(move);
					}
				}
			}

			if (fromX < 7) {
				if (Pieces.color(board[fromY + 1][fromX + 1]) == 0) {
					Move move = new Move(fromX, fromY, fromX + 1, fromY + 1, 0);
					if (fromY == 6) {
						move.promotion = 1;
						moves.add(clone(move));
						move.promotion = 2;
						moves.add(clone(move));
						move.promotion = 3;
						moves.add(clone(move));
						move.promotion = 4;
						moves.add(clone(move));
					} else {
						moves.add(move);
					}
				}
			}
		}

		// En Passant
		if (moveHistory.size() == 0) {
			return moves;
		}
		Move lastMove = moveHistory.get(moveHistory.size() - 1);
		if (lastMove.toY != 4) {
			return moves;
		}
		if (board[lastMove.toY][lastMove.toX] != 6) {
			return moves;
		}

		if (fromY == 4) {
			if (lastMove.fromY == 6) {
				if (lastMove.toX == fromX - 1) {
					Move move = new Move(fromX, fromY, fromX - 1, fromY + 1, 0);
					moves.add(move);
				}
				if (lastMove.toX == fromX + 1) {
					Move move = new Move(fromX, fromY, fromX + 1, fromY + 1, 0);
					moves.add(move);
				}
			}
		}

		return moves;
	}

	public static ArrayList<Point> whitePawnSquares(Position position, int fromX, int fromY) {

		ArrayList<Point> squares = new ArrayList<Point>();

		// Normal Capturing
		if (fromY < 7) {
			if (fromX > 0) {
				Point move = new Point(fromX - 1, fromY + 1);
				squares.add(move);
			}

			if (fromX < 7) {
				Point move = new Point(fromX + 1, fromY + 1);
				squares.add(move);
			}
		}

		return squares;
	}

	public static ArrayList<Move> blackPawnMoves(Position position, int fromX, int fromY) {

		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> moveHistory = position.getMoveHistory();
		int[][] board = position.getBoard();

		// Vertical Movement
		if (fromY == 6) {
			Move move = new Move(fromX, 6, fromX, 5, 0);
			if (board[5][fromX] == 12) {
				moves.add(clone(move));
			}
			move.toY = 4;
			if (board[5][fromX] == 12 && board[4][fromX] == 12) {
				moves.add(clone(move));
			}
		} else {
			Move move = new Move(fromX, fromY, fromX, fromY - 1, 0);
			if (board[fromY - 1][fromX] == 12) {
				if (fromY == 1) {
					move.promotion = 1;
					moves.add(clone(move));
					move.promotion = 2;
					moves.add(clone(move));
					move.promotion = 3;
					moves.add(clone(move));
					move.promotion = 4;
					moves.add(clone(move));
				} else {
					moves.add(move);
				}
			}
		}

		// Normal Capturing
		if (fromY > 0) {
			if (fromX > 0) {
				if (Pieces.color(board[fromY - 1][fromX - 1]) == 1) {
					Move move = new Move(fromX, fromY, fromX - 1, fromY - 1, 0);
					if (fromY == 1) {
						move.promotion = 1;
						moves.add(clone(move));
						move.promotion = 2;
						moves.add(clone(move));
						move.promotion = 3;
						moves.add(clone(move));
						move.promotion = 4;
						moves.add(clone(move));
					} else {
						moves.add(move);
					}
				}
			}

			if (fromX < 7) {
				if (Pieces.color(board[fromY - 1][fromX + 1]) == 1) {
					Move move = new Move(fromX, fromY, fromX + 1, fromY - 1, 0);
					if (fromY == 1) {
						move.promotion = 1;
						moves.add(clone(move));
						move.promotion = 2;
						moves.add(clone(move));
						move.promotion = 3;
						moves.add(clone(move));
						move.promotion = 4;
						moves.add(clone(move));
					} else {
						moves.add(move);
					}
				}
			}
		}
		
		// En Passant
		if (moveHistory.size() == 0) {
			return moves;
		}
		Move lastMove = moveHistory.get(moveHistory.size() - 1);
		if (board[lastMove.toY][lastMove.toX] != 0) {
			return moves;
		}
		if (lastMove.toY != 3) {
			return moves;
		}

		if (fromY == 4) {
			if (lastMove.fromY == 6) {
				if (lastMove.toX == fromX - 1) {
					Move move = new Move(fromX, fromY, fromX - 1, fromY + 1, 0);
					moves.add(move);
				}
				if (lastMove.toX == fromX + 1) {
					Move move = new Move(fromX, fromY, fromX + 1, fromY + 1, 0);
					moves.add(move);
				}
			}
		}

		return moves;
	}

	public static ArrayList<Point> blackPawnSquares(Position position, int fromX, int fromY) {
		ArrayList<Point> squares = new ArrayList<Point>();

		// Normal Capturing
		if (fromY > 0) {
			if (fromX > 0) {
				Point move = new Point(fromX - 1, fromY - 1);
				squares.add(move);
			}

			if (fromX < 7) {
				Point move = new Point(fromX + 1, fromY - 1);
				squares.add(move);
			}
		}

		return squares;
	}

	public static ArrayList<Move> knightMoves(Position position, int fromX, int fromY) {
		ArrayList<Move> moves = new ArrayList<Move>();
		int[][] jumps = { { -1, -2 }, { 1, -2 }, { -2, -1 }, { 2, -1 }, { -2, 1 }, { 2, 1 }, { -1, 2 }, { 1, 2 } };
		for (int i = 0; i < 8; i++) {
			Move move = new Move(fromX, fromY, fromX + jumps[i][0], fromY + jumps[i][1], 0);
			if (!withinBoard(move.toX, move.toY)) {
				continue;
			}
			moves.add(move);
		}
		return moves;
	}

	public static ArrayList<Point> knightSquares(Position position, int fromX, int fromY) {
		ArrayList<Point> squares = new ArrayList<Point>();
		int[][] jumps = { { -1, -2 }, { 1, -2 }, { -2, -1 }, { 2, -1 }, { -2, 1 }, { 2, 1 }, { -1, 2 }, { 1, 2 } };
		for (int i = 0; i < 8; i++) {
			Point move = new Point((fromX + jumps[i][0]), (fromY + jumps[i][1]));
			if (!withinBoard(move.x, move.y)) {
				continue;
			}
			squares.add(move);
		}
		return squares;
	}

	public static ArrayList<Move> slidingMoves(Position position, int fromX, int fromY, int piece) {
		long start = System.nanoTime();
		int[][] board = position.getBoard();
		ArrayList<Move> moves = new ArrayList<Move>();
		int[][] directions;
		if (piece == 2 || piece == 8) {
			directions = Pieces.BISHOP_DIRECTIONS;
		} else if (piece == 3 || piece == 9) {
			directions = Pieces.ROOK_DIRECTIONS;
		} else {
			directions = Pieces.QUEEN_DIRECTIONS;
		}
		int startPiece = board[fromY][fromX];
		int startColor = Pieces.color(startPiece);
		for (int i = 0; i < directions.length; i++) {
			int[] direction = directions[i];
			int magnitude = 1;
			boolean withinBoard = withinBoard(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
			if (!withinBoard) {
				continue;
			}
			int targetPiece = board[fromY + direction[1]][fromX + direction[0]];
			while (withinBoard && targetPiece == 12) {
				Move move = new Move(fromX, fromY, fromX + magnitude * direction[0], fromY + magnitude * direction[1],
						0);
				moves.add(move);
				magnitude++;
				withinBoard = withinBoard(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
				if (!withinBoard) {
					break;
				}
				targetPiece = board[fromY + magnitude * direction[1]][fromX + magnitude * direction[0]];
			}
			if (Pieces.color(targetPiece) != startColor && withinBoard) {
				Move move = new Move(fromX, fromY, fromX + magnitude * direction[0], fromY + magnitude * direction[1],
						0);
				moves.add(move);
			}
		}
//		System.out.println(System.nanoTime() - start);
		return moves;
	}

	public static ArrayList<Point> slidingSquares(Position position, int fromX, int fromY, int piece) {
		int[][] board = position.getBoard();
		ArrayList<Point> moves = new ArrayList<Point>();
		int[][] directions;
		if (piece == 2 || piece == 8) {
			directions = Pieces.BISHOP_DIRECTIONS;
		} else if (piece == 3 || piece == 9) {
			directions = Pieces.ROOK_DIRECTIONS;
		} else {
			directions = Pieces.QUEEN_DIRECTIONS;
		}
		for (int i = 0; i < directions.length; i++) {
			int[] direction = directions[i];
			int magnitude = 1;
			boolean withinBoard = withinBoard(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
			if (!withinBoard) {
				continue;
			}
			int targetPiece = board[fromY + direction[1]][fromX + direction[0]];
			while (withinBoard && targetPiece == 12) {
				Point move = new Point(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
				moves.add(move);
				magnitude++;
				withinBoard = withinBoard(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
				if (!withinBoard) {
					break;
				}
				targetPiece = board[fromY + magnitude * direction[1]][fromX + magnitude * direction[0]];
			}
			if (withinBoard) {
				Point move = new Point(fromX + magnitude * direction[0], fromY + magnitude * direction[1]);
				moves.add(move);
			}
		}
		return moves;
	}

	public static ArrayList<Move> kingMoves(Position position, int fromX, int fromY) {
		int[][] board = position.getBoard();
		ArrayList<Move> moves = new ArrayList<Move>();
		int[][] offsets = Pieces.QUEEN_DIRECTIONS;
		int startColor = Pieces.color(board[fromY][fromX]);
		for (int i = 0; i < 8; i++) {
			Move move = new Move(fromX, fromY, fromX + offsets[i][0], fromY + offsets[i][1], 0);
			if (!withinBoard(move.toX, move.toY) || Pieces.color(board[move.toY][move.toX]) == startColor) {
				continue;
			}
			moves.add(move);
		}
		if (startColor == 1) {
			Set<Point> enemySquares = generateAttackedSquares(position, 0);
			if (position.isWhiteCanCastleKingSide()) {
				if (!(enemySquares.contains(new Point(4, 0)) || enemySquares.contains(new Point(5, 0))
						|| enemySquares.contains(new Point(6, 0))) && board[0][5] == 12 && board[0][6] == 12) {
					Move move = new Move(4, 0, 6, 0, 0);
					moves.add(move);
				}
			}
			if (position.isWhiteCanCastleQueenSide()) {
				if (!(enemySquares.contains(new Point(4, 0)) || enemySquares.contains(new Point(3, 0))
						|| enemySquares.contains(new Point(2, 0))) && board[0][3] == 12 && board[0][2] == 12
						&& board[0][1] == 12) {
					Move move = new Move(4, 0, 2, 0, 0);
					moves.add(move);
				}
			}
		} else {
			Set<Point> enemySquares = generateAttackedSquares(position, 1);
			if (position.isBlackCanCastleKingSide()) {
				if (!(enemySquares.contains(new Point(4, 7)) || enemySquares.contains(new Point(5, 7))
						|| enemySquares.contains(new Point(6, 7))) && board[7][5] == 12 && board[7][6] == 12) {
					Move move = new Move(4, 7, 6, 7, 0);
					moves.add(move);
				}
			}
			if (position.isBlackCanCastleQueenSide()) {
				if (!(enemySquares.contains(new Point(4, 7)) || enemySquares.contains(new Point(3, 7))
						|| enemySquares.contains(new Point(2, 7))) && board[7][3] == 12 && board[7][2] == 12
						&& board[7][1] == 12) {
					Move move = new Move(4, 7, 2, 7, 0);
					moves.add(move);
				}
			}
		}
		return moves;
	}

	public static ArrayList<Point> kingSquares(Position position, int fromX, int fromY) {
		int[][] board = position.getBoard();
		ArrayList<Point> moves = new ArrayList<Point>();
		int[][] offsets = Pieces.QUEEN_DIRECTIONS;
		int startColor = Pieces.color(board[fromY][fromX]);
		for (int i = 0; i < 8; i++) {
			Point move = new Point(fromX + offsets[i][0], fromY + offsets[i][1]);
			if (!withinBoard(move.x, move.y) || board[move.y][move.x] == startColor) {
				continue;
			}
			moves.add(move);
		}
		return moves;
	}

	public static boolean withinBoard(int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		return true;
	}

	public static boolean isPseudoLegal(Position position, Move move) {

		int[][] board = position.getBoard();
		int fromX = move.fromX, fromY = move.fromY, toX = move.toX, toY = move.toY;

		int movingPiece = board[fromY][fromX];
		int capturedPiece = board[toY][toX];

		// 0 means black, 1 means white, 12 means blank
		int movingColor = setMovingColor(movingPiece);
		int capturedColor = setCapturedColor(capturedPiece);
		boolean opposingColors = setOpposingColors(movingColor, capturedColor);

		Point displacement = displacement(move);

		// Capturing own pieces
		if (movingColor == capturedColor) {
			return false;
		}

		// Moving blank square
		if (movingPiece == 12) {
			return false;
		}

		// Didn't move at all
		if (displacement.x == 0 && displacement.y == 0) {
			return false;
		}

		// White Pawn
		if (movingPiece == 0) {
			if (displacement.x == 0) {

				// Advancing 2 places from start
				if (displacement.y == 2) {
					if (fromY == 1 && board[2][fromX] == 12 && board[3][fromX] == 12) {
						return true;
					} else {
						return false;
					}
				}

				// Advancing 1 place
				else if (displacement.y == 1) {
					if (board[fromY + 1][fromX] == 12) {
						return true;
					} else {
						return false;
					}
				}

				else {
					return false;
				}

			} else if (Math.abs(displacement.x) == 1 && displacement.y == 1) {
				// Normal Capturing
				if (opposingColors)
					return true;

				// En Passant
				else {
					ArrayList<Move> gameHistory = position.getMoveHistory();
					if (gameHistory.size() == 0) {
						return false;
					}
					Move lastMove = gameHistory.get(gameHistory.size() - 1);
					if (lastMove.fromY == 6 && lastMove.toY == 4 && lastMove.fromX == toX) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		// Black Pawn
		else if (movingPiece == 6) {
			if (displacement.x == 0) {

				if (displacement.y == -2) {
					if (fromY == 6 && board[5][fromX] == 12 && board[4][fromX] == 12) {
						return true;
					} else {
						return false;
					}
				}

				else if (displacement.y == -1) {
					if (board[fromY - 1][fromX] == 12) {
						return true;
					} else {
						return false;
					}
				}

				else {
					return false;
				}

			} else if (Math.abs(displacement.x) == 1 && displacement.y == -1) {
				// Normal Capturing
				if (opposingColors) {
					return true;
				}

				// En Passant
				else {
					ArrayList<Move> gameHistory = position.getMoveHistory();
					if (gameHistory.size() == 0) {
						return false;
					}
					Move lastMove = gameHistory.get(gameHistory.size() - 1);
					if (lastMove.fromY == 1 && lastMove.toY == 3 && lastMove.fromX == toX) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		// Knights
		if (movingPiece == 1 || movingPiece == 7) {
			if (displacement.x * displacement.x + displacement.y * displacement.y == 5) {
				return true;
			} else {
				return false;
			}
		}

		// Bishop
		if (movingPiece == 2 || movingPiece == 8) {
			return pseudoLegalBishopMove(displacement, fromX, fromY, position);
		}

		// Rook
		if (movingPiece == 3 || movingPiece == 9) {
			return pseudoLegalRookMove(displacement, fromX, fromY, position);
		}

		// Queen
		if (movingPiece == 4 || movingPiece == 10) {
			return pseudoLegalBishopMove(displacement, fromX, fromY, position)
					|| pseudoLegalRookMove(displacement, fromX, fromY, position);
		}

		// King
		if (movingPiece == 5 || movingPiece == 11) {
			return displacement.x * displacement.x + displacement.y * displacement.y <= 2;
		}

		return true;

	}

	public static boolean isEnPassant(Position position, Move move) {
		int[][] board = position.getBoard();
		int fromX = move.fromX, fromY = move.fromY, toX = move.toX, toY = move.toY;
		int movingPiece = board[fromY][fromX];
		int capturedPiece = board[toY][toX];
		int movingColor = setMovingColor(movingPiece);
		int capturedColor = setCapturedColor(capturedPiece);
		boolean opposingColors = setOpposingColors(movingColor, capturedColor);

		ArrayList<Move> gameHistory = position.getMoveHistory();
		if (gameHistory.size() == 0) {
			return false;
		}
		Move lastMove = gameHistory.get(gameHistory.size() - 1);
		Point displacement = new Point(toX - fromX, toY - fromY);
		if (opposingColors) {
			return false;
		}
		if (lastMove.fromX != toX) {
			return false;
		}
		if (movingPiece == 0 && Math.abs(displacement.x) == 1 && displacement.y == 1) {
			if (lastMove.fromY == 6 && lastMove.toY == 4) {
				return true;
			} else {
				return false;
			}
		} else if (movingPiece == 6 && Math.abs(displacement.x) == 1 && displacement.y == -1) {
			if (lastMove.fromY == 1 && lastMove.toY == 3) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static int isCastling(Position position, Move move) {
		int[][] board = position.getBoard();
		int fromX = move.fromX, fromY = move.fromY, toX = move.toX;
		if (board[fromY][fromX] == 5 && toX - fromX == 2) {
			return 1;
		} else if (board[fromY][fromX] == 5 && toX - fromX == -2) {
			return 2;
		} else if (board[fromY][fromX] == 11 && toX - fromX == 2) {
			return 3;
		} else if (board[fromY][fromX] == 11 && toX - fromX == -2) {
			return 4;
		} else {
			return 0;
		}
	}

	public static int whiteInMate(Position position) {
		ArrayList<Move> legalMoves = generateLegalMoves(position, true);
		boolean whiteInCheck = whiteInCheck(position);
		if (legalMoves.size() == 0) {
			if (whiteInCheck) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}

	public static int blackInMate(Position position) {
		ArrayList<Move> legalMoves = generateLegalMoves(position, false);
		boolean blackInCheck = blackInCheck(position);
		if (legalMoves.size() == 0) {
			if (blackInCheck) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}

	public static int setMovingColor(int movingPiece) {
		if (movingPiece < 6) {
			return 1;
		} else if (movingPiece != 12) {
			return 0;
		} else {
			return 12;
		}
	}

	public static int setCapturedColor(int capturedPiece) {
		if (capturedPiece < 6) {
			return 1;
		} else if (capturedPiece != 12) {
			return 0;
		} else {
			return 12;
		}
	}

	public static boolean setOpposingColors(int movingColor, int capturedColor) {
		if (Math.abs(movingColor - capturedColor) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static Point displacement(Move move) {
		int fromX = move.fromX, fromY = move.fromY, toX = move.toX, toY = move.toY;
		int displacementX = toX - fromX;
		int displacementY = toY - fromY;
		return new Point(displacementX, displacementY);
	}

	public static boolean pseudoLegalBishopMove(Point displacement, int fromX, int fromY, Position position) {
		int[][] board = position.getBoard();
		if (Math.abs(displacement.x) == Math.abs(displacement.y)) {
			int signX = displacement.x / Math.abs(displacement.x);
			int signY = displacement.y / Math.abs(displacement.y);
			for (int x = signX, y = signY; Math.abs(x) < Math.abs(displacement.x); x += signX, y += signY) {
				if (board[fromY + y][fromX + x] != 12) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean pseudoLegalRookMove(Point displacement, int fromX, int fromY, Position position) {
		int[][] board = position.getBoard();
		if (displacement.x == 0 || displacement.y == 0) {
			int signX = displacement.x / Math.abs(displacement.x);
			int signY = displacement.y / Math.abs(displacement.y);
			for (int x = signX, y = signY; x != displacement.x || y != displacement.y; x += signX, y += signY) {
				if (board[fromY + y][fromX + x] != 12) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static int[][] clone(int[][] array) {
		int[][] newBoard = new int[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				newBoard[i][j] = array[i][j];
			}
		}
		return newBoard;
	}

	public static Move clone(Move move) {
		return new Move(move.fromX, move.fromY, move.toX, move.toY, move.promotion);
	}

	public static ArrayList<Move> clone(ArrayList<Move> list) {
		ArrayList<Move> newList = new ArrayList<Move>();
		for (int i = 0; i < list.size(); i++) {
			newList.add(list.get(i));
		}
		return newList;
	}

	public static Position clone(Position position) {
		return new Position(clone(position.getBoard()), position.isTurn(), position.isWhiteCanCastleKingSide(),
				position.isWhiteCanCastleQueenSide(), position.isBlackCanCastleKingSide(),
				position.isBlackCanCastleQueenSide(), clone(position.getMoveHistory()),
				clone(position.getWhitePieceLocations()), clone(position.getBlackPieceLocations()),
				new Point(position.getWhiteKingLocation().x, position.getWhiteKingLocation().y),
				new Point(position.getWhiteKingLocation().x, position.getWhiteKingLocation().y));
	}

	public static int moveGenerationTest(Position position, int depth) {
		if (depth == 0) {
			return 1;
		}
		ArrayList<Move> moves = generateLegalMoves(position, position.isTurn());
		int count = 0;
		for (Move move : moves) {
			Position newPosition = clone(position);
			move.playMove(newPosition);
			count += moveGenerationTest(newPosition, depth - 1);
		}
		return count;
	}

}
