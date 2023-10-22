package hashing;

import java.util.HashMap;

import chess.Position;

public class Hashing {
	
	public static HashMap<String, float[]> transpositionTable = new HashMap<String, float[]>();
	
	public static String hash(Position position) {
		
		String hash = "";
		int[][] board = position.getBoard();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				hash += board[i][j];
			}
		}
		
		hash += position.isTurn() ? 1 : 0;
		
		return hash;
		
	}
	
}
