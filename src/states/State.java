package states;

import java.awt.Graphics2D;

import main.Game;

public abstract class State {
	
	private static State currentState = null;
	
	public static void setState(State state){
		currentState = state;
	}
	
	public static State getState(){
		return currentState;
	}
	
	protected Game game;
	
	public State(Game game){
		this.game = game;
	}
	
	public abstract void initialize();
	
	public abstract void tick(Graphics2D g);
	
	public abstract void render(Graphics2D g);
	
}