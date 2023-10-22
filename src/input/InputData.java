package input;

import java.awt.Point;
import java.util.ArrayList;

import chess.Move;
import chess.MoveGenerator;
import chess.Position;
import data.Pieces;
import main.Launcher;
import states.GameState;

public class InputData {

	private Point selectedSquare = new Point();
	private boolean selecting = false;
	public GameState gameState = Launcher.gameState;
	public Position position;
	public int[][] board;

	public long lastInput = 0;

	public InputData() {
		position = GameState.position;
		board = position.getBoard();
	}

	public boolean isSelecting() {
		return selecting;
	}

	public void setSelecting(boolean selecting) {
		this.selecting = selecting;
	}

	public Point getSelectedSquare() {
		return selectedSquare;
	}

	public void setSelectedSquare(Point selectedSquare) {
		this.selectedSquare = selectedSquare;
	}

	public void clickSquare(Point square, float fractionX, float fractionY) {

		// Point square is from white's perspective

		// Off the board
		if (square.x >= 8 || square.x < 0 || square.y >= 8 || square.y < 0) {
			// Unselect
			setSelectedSquare(null);
			setSelecting(false);
		}

		// In the board
		else {
			if (selecting) {
				if (square.x == selectedSquare.x && square.y == selectedSquare.y) {
					// Unselect
					setSelectedSquare(null);
					setSelecting(false);
				} else if (board[selectedSquare.y][selectedSquare.x] != 12) {
					// Move if legal
					// Select other piece if not
					int piece = board[selectedSquare.y][selectedSquare.x];
					boolean color = Pieces.nonEmptyColor(piece);
					if ((color && !GameState.computerAsWhite) || (!color && !GameState.computerAsBlack)) {
						Move move = new Move(selectedSquare.x, selectedSquare.y, square.x, square.y, 0);
						if ((board[selectedSquare.y][selectedSquare.x] == 0 && square.y == 7)
								|| (board[selectedSquare.y][selectedSquare.x] == 6 && square.y == 0)) {
							boolean topLeft = fractionX < 1 - fractionY;
							boolean topRight = fractionX > fractionY;
							if (topLeft) {
								if (topRight) {
									move.promotion = 4;
								} else {
									move.promotion = 3;
								}
							} else {
								if (topRight) {
									move.promotion = 2;
								} else {
									move.promotion = 1;
								}
							}
						}
						ArrayList<Move> moves = MoveGenerator.generateLegalMovesFromSquare(position, selectedSquare.x,
								selectedSquare.y);
						if (contains(moves, move)) {
							move.playMove(position);
							lastInput = System.nanoTime();
//							for (int depth = 1; depth <= 3; depth++) {
//								long start = System.nanoTime();
//								int output = MoveGenerator.moveGenerationTest(position, depth);
//								System.out.println("Depth: " + depth + ", Result: " + output + ", Time: " + (System.nanoTime() - start) / 1e9 + " seconds");
//							}
//							System.out.println();
							setSelectedSquare(null);
							setSelecting(false);
						} else {
							setSelectedSquare(square);
							setSelecting(true);
						}
					} else {
						setSelectedSquare(square);
						setSelecting(true);
					}
				} else if (board[selectedSquare.y][selectedSquare.x] == 12) {
					setSelectedSquare(square);
					setSelecting(true);
				}
			} else {
				setSelectedSquare(square);
				setSelecting(true);
			}
		}

	}

	public static boolean contains(ArrayList<Move> moves, Move move) {
		for (int i = 0; i < moves.size(); i++) {
			Move tempMove = moves.get(i);
			if (tempMove.fromX == move.fromX && tempMove.fromY == move.fromY && tempMove.toX == move.toX
					&& tempMove.toY == move.toY && tempMove.promotion == move.promotion) {
				return true;
			}
		}
		return false;
	}

	public static void replacePiece(int[][] board, int x, int y, int newPiece) {
		board[y][x] = newPiece;
	}

}
