package engine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import chess.Move;

public class Toolbox {
	
	public static ArrayList<Move> clone(ArrayList<Move> list) {
		ArrayList<Move> c = new ArrayList<Move>();
		for (Move m : list) {
			c.add(m);
		}
		return c;
	}
	
	public static float mateIn(int depth, boolean winning) {
		if (winning) {
			return 100000f - depth;
		} else {
			return -100000f + depth;
		}
	}
	
	public static float map(float input, float startMin, float startMax, float mappedMin, float mappedMax) {
		return mappedMin + (input - startMin) * (mappedMax - mappedMin) / (startMax - startMin);
	}
	
	public static void drawText(Graphics2D g, String text, int x, int y) {
		
		int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
		int width = g.getFontMetrics().stringWidth(text);
		int textX, textY;
		
		if (alignX == ALIGN_LEFT) {
			textX = x;
		} else if (alignX == ALIGN_CENTER) {
			textX = x - width / 2;
		} else {
			textX = x - width;
		}
		
		if (alignY == ALIGN_TOP) {
			textY = y + height;
		} else if (alignY == ALIGN_CENTER) {
			textY = y + height / 2;
		} else {
			textY = y;
		}
		
		g.drawString(text, textX, textY);
		
	}
	
	public static Point displacement(Point a, Point b) {
		return new Point(b.x - a.x, b.y - a.y);
	}
	
	public static int alignX = 0, alignY = 0;
	public static final int ALIGN_TOP = 0, ALIGN_LEFT = 0, ALIGN_CENTER = 1, ALIGN_BOTTOM = 2, ALIGN_RIGHT = 2;
	
	public static void setAlignX(int align) {
		alignX = align;
	}
	
	public static void setAlignY(int align) {
		alignY = align;
	}
	
	public static void setAlign(int x, int y) {
		alignX = x;
		alignY = y;
	}
	
}
