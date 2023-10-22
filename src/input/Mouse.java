package input;

import data.Start;
import main.Launcher;
import states.GameState;

public class Mouse {
	
	public static int x() {
		return Launcher.mouseManager.getX();
	}

	public static int y() {
		return Launcher.mouseManager.getY();
	}
	
	public static int toFile(int x) {
		if (x < 0) {
			return -1;
		} else if (x > GameState.BOARD_SIZE) {
			return 8;
		}
		if (Start.perspective) {
			return (int) (8.0 * x / GameState.BOARD_SIZE);
		} else {
			return (int) (8 - 8.0 * x / GameState.BOARD_SIZE);
		}
	}
	
	public static int toRank(int y) {
		if (y < 0) {
			return -1;
		} else if (y > GameState.BOARD_SIZE) {
			return 8;
		}
		if (Start.perspective) {
			return (int) (8 - 8.0 * y / GameState.BOARD_SIZE);
		} else {
			return (int) (8.0 * y / GameState.BOARD_SIZE);
		}
	}
	
	public static int centerOfFileToX(int file) {
		int fileOnScreen;
		if (!Start.perspective) {
			fileOnScreen = 7 - file;
		} else {
			fileOnScreen = file;
		}
		return (int) ((fileOnScreen + 0.5) * GameState.SQUARE_SIZE + GameState.MARGIN);
	}
	
	public static int centerOfRankToY(int rank) {
		int rankOnScreen;
		if (!Start.perspective) {
			rankOnScreen = 7 - rank;
		} else {
			rankOnScreen = rank;
		}
		return (int) ((7.5 - rankOnScreen) * GameState.SQUARE_SIZE + GameState.MARGIN);
	}
	
	public static int fileOnScreen(int x) {
		if (Start.perspective) {
			return x;
		} else {
			return 7 - x;
		}
	}
	
	public static int rankOnScreen(int y) {
		if (Start.perspective) {
			return 7 - y;
		} else {
			return y;
		}
	}
	
}
