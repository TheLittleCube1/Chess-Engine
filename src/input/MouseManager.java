package input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import data.Start;
import main.Launcher;
import states.GameState;
import states.SettingState;
import states.State;

public class MouseManager extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = -8994576182964289862L;
	
	private InputData inputData = new InputData();

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getPoint().x;
		int y = e.getPoint().y;
		if (State.getState() == Launcher.gameState) {
			int file = Mouse.toFile(x - GameState.BOARD_X);
			int rank = Mouse.toRank(y - GameState.BOARD_Y);
			Point square = new Point(file, rank);
			double distanceFromSwitchPerspectiveIconSquared = Math.pow((x - (GameState.ICON_X + GameState.ICON_DIAMETER / 2)), 2) + Math.pow((y - (GameState.SWITCH_PERSPECTIVE_ICON_Y + GameState.ICON_DIAMETER / 2)), 2);
			double distanceFromSettingsIconSquared = Math.pow((x - (GameState.ICON_X + GameState.ICON_DIAMETER / 2)), 2) + Math.pow((y - (GameState.SETTINGS_ICON_Y + GameState.ICON_DIAMETER / 2)), 2);
			if (distanceFromSettingsIconSquared < GameState.ICON_DIAMETER * GameState.ICON_DIAMETER / 4) {
				State.setState(Launcher.settingState);
			} else if (distanceFromSwitchPerspectiveIconSquared < GameState.ICON_DIAMETER * GameState.ICON_DIAMETER / 4) {
				Start.perspective = !Start.perspective;
			} else {
				int screenFile = (x - GameState.BOARD_X) / GameState.SQUARE_SIZE;
				int screenRank = (y - GameState.BOARD_Y) / GameState.SQUARE_SIZE;
				inputData.clickSquare(square, ((float) x - GameState.MARGIN - screenFile * GameState.SQUARE_SIZE) / GameState.SQUARE_SIZE, ((float) y - GameState.MARGIN - screenRank * GameState.SQUARE_SIZE) / GameState.SQUARE_SIZE);
			}
		} else if (State.getState() == Launcher.settingState) {
			double distanceFromSettingsIconSquared = Math.pow((x - (GameState.ICON_X + GameState.ICON_DIAMETER / 2)), 2) + Math.pow((y - (GameState.SETTINGS_ICON_Y + GameState.ICON_DIAMETER / 2)), 2);
			double distanceFromPerspectiveIconSquared = Math.pow((x - (50 + SettingState.SETTINGS_ICON_DIAMETER / 2)), 2) + Math.pow((y - (80 + SettingState.SETTINGS_ICON_DIAMETER / 2)), 2);
			double distanceFromTerritoryIconSquared = Math.pow((x - (50 + SettingState.SETTINGS_ICON_DIAMETER / 2)), 2) + Math.pow((y - (92 + 3 * SettingState.SETTINGS_ICON_DIAMETER / 2)), 2);
			int radiusSquared = (SettingState.SETTINGS_ICON_DIAMETER / 2) * (SettingState.SETTINGS_ICON_DIAMETER / 2);
			if (distanceFromSettingsIconSquared < GameState.ICON_DIAMETER * GameState.ICON_DIAMETER / 4) {
				State.setState(Launcher.gameState);
			} else if (distanceFromPerspectiveIconSquared < radiusSquared) {
				Start.perspective = !Start.perspective;
			} else if (distanceFromTerritoryIconSquared < radiusSquared) {
				GameState.showTerritoryMap = !GameState.showTerritoryMap;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public InputData getInputData() {
		return inputData;
	}
	
}
