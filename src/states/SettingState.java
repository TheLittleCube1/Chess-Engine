package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import data.Start;
import main.Game;
import main.Launcher;

public class SettingState extends State {
	
	public static final int MARGIN = 40;
	public static final int BOARD_SIZE = Launcher.WIDTH - MARGIN - MARGIN;
	public static final int SQUARE_SIZE = BOARD_SIZE / 8;
	public static final int BOARD_X = MARGIN, BOARD_Y = MARGIN;
	public static final int ICON_DIAMETER = 25;
	public static final int SETTINGS_ICON_DIAMETER = 30;
	
	public static final int ICON_X = Launcher.WIDTH - MARGIN / 2 - ICON_DIAMETER / 2;
	public static final int STATE_ICON_Y = MARGIN / 2 - ICON_DIAMETER / 2;
	
	public SettingState(Game game) {
		super(game);
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public void tick(Graphics2D g) {
		
	}

	@Override
	public void render(Graphics2D g) {
		drawIcons(g);
		drawSettings(g);
	}
	
	public void drawIcons(Graphics2D g) {
		g.drawImage(Game.pawnIcon, ICON_X, STATE_ICON_Y, ICON_DIAMETER, ICON_DIAMETER, null);
	}
	
	public void drawSettings(Graphics2D g) {
		
		// Title
		g.setFont(new Font("serif", Font.PLAIN, 20));
		g.setColor(Color.WHITE);
		g.drawString("Settings", 50, 50);

		int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
		
		// 1. Perspective
		if (Start.perspective) {
			g.drawString("White's perspective", 100, 80 + SETTINGS_ICON_DIAMETER / 2 + height / 2);
			g.setColor(Color.WHITE);
		} else {
			g.drawString("Black's perspective", 100, 80 + SETTINGS_ICON_DIAMETER / 2 + height / 2);
			g.setColor(Color.GRAY);
		}
		g.fillOval(50, 80, SETTINGS_ICON_DIAMETER, SETTINGS_ICON_DIAMETER);
		
		// 2. Territory Map
		g.setColor(Color.WHITE);
		if (GameState.showTerritoryMap) {
			g.drawImage(Game.territoryMapEnabledIcon, 50, 92 + SETTINGS_ICON_DIAMETER, SETTINGS_ICON_DIAMETER, SETTINGS_ICON_DIAMETER, null);
			g.drawString("Showing Territory Map", 100, 92 + 3 * SETTINGS_ICON_DIAMETER / 2 + height / 2);
		} else {
			g.drawImage(Game.territoryMapDisabledIcon, 50, 92 + SETTINGS_ICON_DIAMETER, SETTINGS_ICON_DIAMETER, SETTINGS_ICON_DIAMETER, null);
			g.drawString("Hiding Territory Map", 100, 92 + 3 * SETTINGS_ICON_DIAMETER / 2 + height / 2);
		}
		
	}

}
