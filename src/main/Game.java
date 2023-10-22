package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import display.Display;
import graphics.ImageLoader;
import graphics.SpriteSheet;
import states.GameState;
import states.SettingState;
import states.State;

public class Game implements Runnable {

	private Display display;
	public int width, height;
	public String title;

	private boolean running = false;
	private Thread thread;

	public int frameCount = 0;

	private BufferStrategy bs;
	public Graphics2D g;

	public GameState gameState = new GameState(this);
	public SettingState settingState = new SettingState(this);

	public static BufferedImage piecesImage, switchPerspectiveIcon, settingsIcon, pawnIcon, territoryMapEnabledIcon, territoryMapDisabledIcon;
	public static SpriteSheet sheet;

	public Game(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	private void init() {
		// Create Display
		display = new Display(title, width, height);

		// Initialize
		State.setState(gameState);
		gameState.initialize();
		settingState.initialize();

		prepareImages();
	}

	public void tick() {

		if (State.getState() != null) {
			State.getState().tick(g);
		}

	}

	public void render() {
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}

		g = (Graphics2D) bs.getDrawGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		if (State.getState() != null) {
			State.getState().render(g);
		}

		bs.show();
		g.dispose();
	}

	public void run() {

		init();

		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;

		while (running) {
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				ticks++;
				frameCount++;
				delta--;
			}

			if (timer >= 1e9) {
//				System.out.println(ticks + " FPS");
				ticks = 0;
				timer = 0;
			}
		}

		stop();

	}

	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Display getDisplay() {
		return display;
	}

	public Graphics2D getGraphics2D() {
		return g;
	}

	public static void prepareImages() {
		piecesImage = ImageLoader.loadImage("/textures/piecesSheet.png");
		switchPerspectiveIcon = ImageLoader.loadImage("/textures/Switch Perspective Icon.png");
		settingsIcon = ImageLoader.loadImage("/textures/Settings Icon.png");
		pawnIcon = ImageLoader.loadImage("/textures/Pawn Icon.png");
		territoryMapEnabledIcon = ImageLoader.loadImage("/textures/Territory Map Enabled Icon.png");
		territoryMapDisabledIcon = ImageLoader.loadImage("/textures/Territory Map Disabled Icon.png");
		sheet = new SpriteSheet(piecesImage);
	}

}