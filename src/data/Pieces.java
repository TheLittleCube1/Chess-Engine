package data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Pieces {

	public static final String[] NAMES = { 
			"P", // 0
			"N", // 1
			"B", // 2
			"R", // 3
			"Q", // 4
			"K", // 5
			"p", // 6
			"k", // 7
			"b", // 8
			"r", // 9
			"q", // 10
			"k" // 11
	};

	public static final BufferedImage[] pieces = Start.loadPieces();

	public static int color(int piece) {
		if (piece < 6) {
			return 1;
		} else if (piece != 12) {
			return 0;
		} else {
			return 12;
		}
	}

	public static boolean nonEmptyColor(int piece) {
		if (piece < 6) {
			return true;
		} else {
			return false;
		}
	}

	public static final int[][] BISHOP_DIRECTIONS = getBishopDirections();
	public static final int[][] ROOK_DIRECTIONS = getRookDirections();
	public static final int[][] QUEEN_DIRECTIONS = getQueenDirections();
	public static final int[][] STARTING_WHITE_PIECE_LOCATIONS = startingWhitePieceLocations();
	public static final int[][] STARTING_BLACK_PIECE_LOCATIONS = startingBlackPieceLocations();

	public static int[][] getBishopDirections() {
		return new int[][] { { -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 } };
	}

	public static int[][] getRookDirections() {
		return new int[][] { { 0, -1 }, { -1, 0 }, { 1, 0 }, { 0, 1 } };
	}

	public static int[][] getQueenDirections() {
		return new int[][] { { -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 }, { 0, -1 }, { -1, 0 }, { 1, 0 }, { 0, 1 } };
	}

	public static int[][] startingWhitePieceLocations() {
		int[][] array = { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 0 }, { 7, 0 }, { 0, 1 },
				{ 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, };
		return array;
	}

	public static int[][] startingBlackPieceLocations() {
		int[][] array = { { 0, 7 }, { 1, 7 }, { 2, 7 }, { 3, 7 }, { 4, 7 }, { 5, 7 }, { 6, 7 }, { 7, 7 }, { 0, 6 },
				{ 1, 6 }, { 2, 6 }, { 3, 6 }, { 4, 6 }, { 5, 6 }, { 6, 6 }, { 7, 6 }, };
		return array;
	}

}
