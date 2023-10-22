package states;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import engine.Evaluation;
import chess.Move;
import chess.MoveGenerator;
import chess.Position;
import data.Pieces;
import data.Start;
import input.InputData;
import input.Mouse;
import main.Game;
import main.Launcher;

public class GameState extends State {
	
	public static final int MARGIN = 40;
	public static final int BOARD_SIZE = Launcher.WIDTH - MARGIN - MARGIN;
	public static final int SQUARE_SIZE = BOARD_SIZE / 8;
	public static final int BOARD_X = MARGIN, BOARD_Y = MARGIN;
	public static final int ICON_DIAMETER = 25;
	public static final String[] FILES = { "a", "b", "c", "d", "e", "f", "g", "h" };
	
	public static final int ICON_X = Launcher.WIDTH - MARGIN / 2 - ICON_DIAMETER / 2;
	public static final int SETTINGS_ICON_Y = MARGIN / 2 - ICON_DIAMETER / 2;
	public static final int SWITCH_PERSPECTIVE_ICON_Y = MARGIN;
	
	public static int[][] board = MoveGenerator.clone(Start.STARTING_BOARD);
	public static int[][] whitePieceLocations;
	public static int[][] blackPieceLocations;
	public static Position position = new Position(board, true, true, true, true, true, new ArrayList<Move>(), new int[16][2], new int[16][2], new Point(4, 0), new Point(4, 7));
	
	public static boolean showTerritoryMap = false;
	
	public static int searchDepth = 4;
	
	public static boolean computerAsWhite = false;
	public static boolean computerAsBlack = true;
	
	public GameState(Game game) {
		super(game);
	}
	
	// Overrides
	
	@Override
	public void initialize() {
		
	}

	@Override
	public void tick(Graphics2D g) {
		if (Launcher.game.frameCount == 3) {
			//System.out.println(MoveGenerator.perft(position, 4));
			whitePieceLocations = MoveGenerator.clone(Pieces.STARTING_WHITE_PIECE_LOCATIONS);
			blackPieceLocations = MoveGenerator.clone(Pieces.STARTING_BLACK_PIECE_LOCATIONS);
			position.setWhitePieceLocations(whitePieceLocations);
			position.setBlackPieceLocations(blackPieceLocations);
//			long start = System.nanoTime();
//			int result = MoveGenerator.moveGenerationTest(position, searchDepth);
//			System.out.println("Depth: " + searchDepth + ", Result: " + result + ", Time: " + (System.nanoTime() - start) / 1e9 + " seconds, Speed: " + (int) Math.floor((result) / ((System.nanoTime() - start) / 1e9)) + " positions/second");
		} else if (Launcher.game.frameCount > 4) {
			if (computerAsWhite && MoveGenerator.generateLegalMoves(position, position.isTurn()).size() > 0 && position.isTurn() && (System.nanoTime() - Launcher.mouseManager.getInputData().lastInput) > 50000000) {
				Move move = Evaluation.chooseComputerMove(position, searchDepth);
				move.playMove(position);
				Launcher.inputData.lastInput = System.nanoTime();
			} else if (computerAsBlack && MoveGenerator.generateLegalMoves(position, position.isTurn()).size() > 0 && !position.isTurn() && (System.nanoTime() - Launcher.mouseManager.getInputData().lastInput) > 50000000) {
				Move move = Evaluation.chooseComputerMove(position, searchDepth);
				move.playMove(position);
				Launcher.inputData.lastInput = System.nanoTime();
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		drawBoard(g, BOARD_X, BOARD_Y, SQUARE_SIZE, BOARD_SIZE, board, Start.perspective);
		drawIcons(g);
	}
	
	// GameState Methods

	public void drawBoard(Graphics2D g, int x, int y, int squareSize, int boardSize, int[][] board,
			boolean perspective) {
		drawSquares(g, x, y, squareSize);
		renderPieces(g, x, y, squareSize, board, perspective);
		writeCoordinates(g, x, y, squareSize, perspective);
		if (Launcher.inputData.isSelecting()) {
			int file = Launcher.inputData.getSelectedSquare().x;
			int rank = Launcher.inputData.getSelectedSquare().y;
			if (board[rank][file] != 12) {
				boolean color = Pieces.nonEmptyColor(board[rank][file]);
				boolean friendlyPiece = (color && !computerAsWhite) || (!color && !computerAsBlack);
				if (friendlyPiece) {
					drawLegalMoves(g);
				}
			}
		}
	}

	public void drawSquares(Graphics2D g, int x, int y, int squareSize) {
		InputData inputData = Launcher.inputData;
		boolean selected = inputData.isSelecting();
		Point selectedSquare = inputData.getSelectedSquare();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				int pI = (Start.perspective) ? i : 7 - i;
				int pJ = (Start.perspective) ? j : 7 - j;
				
				if (showTerritoryMap) {
					
					boolean white = MoveGenerator.generateAttackedSquares(position, 1).contains(new Point(i, 7 - j));
					boolean black = MoveGenerator.generateAttackedSquares(position, 0).contains(new Point(i, 7 - j));
					
					if (white) {
						g.setColor(Color.WHITE);
					}
					
					if (black) {
						g.setColor(Color.GRAY);
					}
					
					if (white && black) {
						g.setColor(Color.LIGHT_GRAY);
					}
					
					if (!white && !black) {
						g.setColor(Color.LIGHT_GRAY);
					}
					
					g.fillRect(x + pI * squareSize, y + pJ * squareSize, squareSize, squareSize);
					g.setColor(Color.BLACK);
					g.drawRect(x + pI * squareSize, y + pJ * squareSize, squareSize, squareSize);
					
				} else {
					
					// Light Square
					if ((i + j) % 2 == 0) {
						if (selected && Mouse.fileOnScreen(selectedSquare.x) == i && Mouse.rankOnScreen(selectedSquare.y) == j) {
							// Selected
							g.setColor(new Color(224, 195, 112));
						} else {
							// Not Selected
							g.setColor(new Color(255, 239, 196));
						}
					}

					// Dark Square
					else {
						if (selected && Mouse.fileOnScreen(selectedSquare.x) == i && Mouse.rankOnScreen(selectedSquare.y) == j) {
							// Selected
							g.setColor(new Color(89, 65, 1));
						} else {
							// Not Selected
							g.setColor(new Color(130, 95, 0));
						}
					}
					g.fillRect(x + i * squareSize, y + j * squareSize, squareSize, squareSize);
					
				}
			}
		}
	}

	public void renderPieces(Graphics2D g, int x, int y, int squareSize, int[][] board, boolean perspective) {
		// Loop through the board and drawing the pieces
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				BufferedImage piece = Pieces.pieces[board[7 - rank][file]];
				renderPiece(g, piece, file * squareSize, rank * squareSize, x, y, BOARD_SIZE, squareSize, perspective);
			}
		}
	}

	public void renderPiece(Graphics2D g, BufferedImage piece, int x, int y, int boardX, int boardY, int boardSize,
			int size, boolean perspective) {
		// Draw piece
		if (perspective) {
			// White's perspective
			g.drawImage(piece, boardX + x, boardY + y, size, size, null);
		} else {
			// Black's perspective
			g.drawImage(piece, boardX + boardSize - size - x, boardY + boardSize - size - y, size, size, null);
		}
	}

	public void writeCoordinates(Graphics2D g, int boardX, int boardY, int squareSize, boolean perspective) {
		int fontSize = 20;
		g.setColor(Color.WHITE);
		g.setFont(new Font("serif", Font.BOLD, fontSize));
		int boardSize = squareSize * 8;
		for (int i = 0; i < 8; i++) {
			String f;
			String r;
			if (perspective) {
				f = FILES[i];
				r = "" + (8 - i);
			} else {
				f = FILES[7 - i];
				r = "" + (i + 1);
			}
			int width = g.getFontMetrics().stringWidth(f);
			int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
			g.drawString(f, boardX + (int) ((i + 0.5) * squareSize) - width / 2, boardY + boardSize + fontSize);
			g.drawString(r, boardX - fontSize, (int) ((i + 1) * squareSize + height / 2));
		}
	}
	
	public void drawIcons(Graphics2D g) {
		g.drawImage(Game.settingsIcon, ICON_X, SETTINGS_ICON_Y, ICON_DIAMETER, ICON_DIAMETER, null);
		g.drawImage(Game.switchPerspectiveIcon, ICON_X, SWITCH_PERSPECTIVE_ICON_Y, ICON_DIAMETER, ICON_DIAMETER, null);
	}

	public static void replacePiece(int x, int y, int newPiece) {
		board[y][x] = newPiece;
	}
	
	public void drawLegalMoves(Graphics2D g) {
		Point selectedSquare = Launcher.inputData.getSelectedSquare();
		ArrayList<Move> legalMoves = MoveGenerator.generateLegalMovesFromSquare(position, selectedSquare.x, selectedSquare.y);
		for (int i = 0; i < legalMoves.size(); i++) {
			int x = Mouse.centerOfFileToX(legalMoves.get(i).toX);
			int y = Mouse.centerOfRankToY(legalMoves.get(i).toY);
			if (selectedSquare.x != legalMoves.get(i).fromX || selectedSquare.y != legalMoves.get(i).fromY) {
				continue;
			}
			if (legalMoves.get(i).promotion != 0) {
				g.setColor(Color.GRAY);
				g.setStroke(new BasicStroke(3));
				g.drawLine(x - GameState.SQUARE_SIZE / 2, y - GameState.SQUARE_SIZE / 2, x + GameState.SQUARE_SIZE / 2, y + GameState.SQUARE_SIZE / 2);
				g.drawLine(x - GameState.SQUARE_SIZE / 2, y + GameState.SQUARE_SIZE / 2, x + GameState.SQUARE_SIZE / 2, y - GameState.SQUARE_SIZE / 2);
			}
			g.setColor(new Color(95, 154, 232));
			g.fillOval(x - GameState.SQUARE_SIZE / 10, y - GameState.SQUARE_SIZE / 10, GameState.SQUARE_SIZE / 5, GameState.SQUARE_SIZE / 5);
		}
	}

}