package engine;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;

import chess.Move;
import chess.MoveGenerator;
import chess.PieceLocationMap;
import chess.Position;
import data.Pieces;
import states.GameState;

public class Evaluation {

	public static float pawn = 1, knight = 3.2f, bishop = 3.3f, rook = 5, queen = 9;
	public static DecimalFormat to2DecimalPlaces = new DecimalFormat("0.00");
	public static DecimalFormat to3DecimalPlaces = new DecimalFormat("0.000");
	public static int numPos = 0;
	public static ArrayList<Float> times = new ArrayList<Float>();
	
	public static float getPieceValue(int piece, int x, int y) {
		if (piece == 0) {
			return pawn + PieceLocationMap.pawnMap[7 - y][x];
		} else if (piece == 6) {
			return pawn + PieceLocationMap.pawnMap[y][x];
		} else if (piece == 1) {
			return knight + PieceLocationMap.knightMap[7 - y][x];
		} else if (piece == 7) {
			return knight + PieceLocationMap.knightMap[y][x];
		} else if (piece == 2) {
			return bishop + PieceLocationMap.bishopMap[7 - y][x];
		} else if (piece == 8) {
			return bishop + PieceLocationMap.bishopMap[y][x];
		} else if (piece == 3) {
			return rook + PieceLocationMap.rookMap[7 - y][x];
		} else if (piece == 9) {
			return rook + PieceLocationMap.rookMap[y][x];
		} else if (piece == 4) {
			return queen + PieceLocationMap.queenMap[7 - y][x];
		} else if (piece == 10) {
			return queen + PieceLocationMap.queenMap[y][x];
		} else return 0;
	}

	public static float eval(Position position, int maxDepth, int depth, float alpha, float beta) {
		if (depth == 0) {
			numPos++;
			return evaluatePosition(position);
		}
		
		ArrayList<Move> legalMoves = MoveGenerator.generateLegalMoves(position, position.isTurn());
		ArrayList<Move> moves = orderMoves(position, legalMoves);
		
		if (moves.size() == 0) {
			if (MoveGenerator.someoneInCheck(position)) {
				numPos++;
				return -99999 + 100 * (maxDepth - depth);
			} else {
				numPos++;
				return 0;
			}
		}
		
		for (Move move : moves) {
			Position newPosition = move.playMove(MoveGenerator.clone(position));
			float evaluation = -eval(newPosition, maxDepth, depth - 1, -beta, -alpha);
			if (evaluation >= beta) {
				return beta;
			}
			alpha = Math.max(alpha, evaluation);
		}
		
		return alpha;
	}
	
	public static float noMoveOrderingEval(Position position, int maxDepth, int depth, float alpha, float beta) {
		if (depth == 0) {
			numPos++;
			return evaluatePosition(position);
		}
		
		ArrayList<Move> moves = MoveGenerator.generateLegalMoves(position, position.isTurn());
		
		if (moves.size() == 0) {
			if (MoveGenerator.someoneInCheck(position)) {
				numPos++;
				return -99999 + 100 * (maxDepth - depth);
			} else {
				numPos++;
				return 0;
			}
		}
		
		for (Move move : moves) {
			Position newPosition = move.playMove(MoveGenerator.clone(position));
			float evaluation = -eval(newPosition, maxDepth, depth - 1, -beta, -alpha);
			if (evaluation >= beta) {
				return beta;
			}
			alpha = Math.max(alpha, evaluation);
		}
		
		return alpha;
	}
	
	public static Move bestMove(Position position, int depth) {
		
		ArrayList<Move> legalMoves = MoveGenerator.generateLegalMoves(position, position.isTurn());
		float bestEval = -100000;
		Move bestMove = legalMoves.get(0);
		for (int i = 0; i < legalMoves.size(); i++) {
			Move move = legalMoves.get(i);
			Position newPosition = move.playMove(MoveGenerator.clone(position));
			float eval = -eval(newPosition, depth - 1, depth - 1, -100000f, 100000f);
			if (eval > bestEval) {
				bestMove = MoveGenerator.clone(move);
				bestEval = eval;
			}
		}
		
		System.out.println("Evaluation: " + evalToString(Float.parseFloat(to2DecimalPlaces.format(bestEval)), position.isTurn()));
		System.out.println("Best Move: " + bestMove.toString());
		return bestMove;
	}

	public static float evaluatePosition(Position position) {
		float whiteEval = countMaterial(position, true);
		float blackEval = countMaterial(position, false);
		float perspective = (position.isTurn()) ? 1 : -1;
		if (whiteEval + blackEval <= 18) {
			return forceKingToCornerEval(position, 1f) * perspective;
		}
		return (whiteEval - blackEval) * perspective;
	}

	public static float countMaterial(Position position, boolean color) {
		int[][] board = position.getBoard();
		float material = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 12)
					continue;
				if (Pieces.nonEmptyColor(board[i][j]) != color)
					continue;

				material += getPieceValue(board[i][j], j, i);
			}
		}
		return material;
	}

	public static Move chooseComputerMove(Position position, int depth) {
		long start = System.nanoTime();
		numPos = 0;
		Move bestMove = bestMove(position, depth);
		float time = (System.nanoTime() - start) / 1e9f;
		times.add(time);
		if (times.size() == 4) {
			times.remove(0);
		}
		float average = 0;
		for (float t : times) {
			average += t / times.size();
		}
		if (average <= 0.1) {
			GameState.searchDepth++;
		}
		return bestMove;
	}
	
	public static ArrayList<Move> orderMoves(Position position, ArrayList<Move> legalMoves) {
		ArrayList<Move> orderedMoves = new ArrayList<Move>();
		while (!legalMoves.isEmpty()) {
			int index = 0;
			float bestScore = -100000;
			for (int i = 0; i < legalMoves.size(); i++) {
				float score = 0;
				Move move = legalMoves.get(i);
				int fromX = move.fromX, fromY = move.fromY, toX = move.toX, toY = move.toY;
				int[][] board = position.getBoard();
				
				// Capturing
				if (board[toY][toX] != 12) {
					score = 10 * getPieceValue(board[toY][toX], toX, toY) - getPieceValue(board[fromY][fromX], fromX, fromY);
				}
				
				// Promotion
				if (move.promotion != 0) {
					score += getPieceValue(move.promotion, toX, toY) - getPieceValue(board[fromY][fromX], fromX, fromY);
				}
				
				if (score > bestScore) {
					bestScore = score;
					index = i;
				}
			}
			orderedMoves.add(legalMoves.get(index));
			legalMoves.remove(index);
		}
		return orderedMoves;
	}
	
	public static String evalToString(float eval, boolean turn) {
		String body;
		if (eval == -99999) {
			body = "Checkmate";
		} else if (eval == 99999) {
			body = "Checkmate";
		} else if (eval >= 70000) {
			body = "Mate in " + (int) ((99998 - eval) / 100);
		} else if (eval <= -70000) {
			body = "Mate in " + (int) ((99999 + eval) / 100);
		} else {
			body = "" + Math.abs(eval);
		}
		if ((turn && eval < 0) || (!turn && eval > 0)) {
			body = "-" + body;
		}
		return body;
	}
	
	public static float forceKingToCornerEval(Position position, float weight) {
		
		int evaluation = 0;
		
		Point opponentKingSquare, friendlyKingSquare;
		if (position.isTurn()) {
			friendlyKingSquare = position.getWhiteKingLocation();
			opponentKingSquare = position.getBlackKingLocation();
		} else {
			friendlyKingSquare = position.getBlackKingLocation();
			opponentKingSquare = position.getWhiteKingLocation();
		}
		int opponentKingFile = opponentKingSquare.x, opponentKingRank = opponentKingSquare.y;
		int friendlyKingFile = friendlyKingSquare.x, friendlyKingRank = opponentKingSquare.y;
		
		int opponentKingDistanceFromCenterFile = Math.max(3 - opponentKingFile, 4 - opponentKingFile);
		int opponentKingDistanceFromCenterRank = Math.max(3 - opponentKingRank, 4 - opponentKingRank);
		int totalDistanceFromCenter = opponentKingDistanceFromCenterFile + opponentKingDistanceFromCenterRank;
		evaluation += totalDistanceFromCenter;
		
		int distanceBetweenKingsFile = Math.abs(opponentKingFile - friendlyKingFile);
		int distanceBetweenKingsRank = Math.abs(opponentKingRank - friendlyKingRank);
		int distanceBetweenKings = distanceBetweenKingsFile + distanceBetweenKingsRank;
		evaluation += 14 - distanceBetweenKings;
		
		int kingSquares = MoveGenerator.generateLegalMovesFromSquare(position, opponentKingSquare.x, opponentKingSquare.y).size();
		evaluation -= kingSquares;
		
		return evaluation * weight;
	}

}
