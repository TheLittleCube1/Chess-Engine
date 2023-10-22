package display;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

import input.MouseManager;
import main.Launcher;

public class Display {

	private JFrame frame;
	private Canvas canvas;

	private String title;
	private int width, height;
	
	private MouseManager mouseManager = new MouseManager();

	public Display(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;

		createDisplay();
	}

	private void createDisplay() {
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.addMouseListener(mouseManager);
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		frame.add(canvas);
		frame.pack();
		
		Launcher.mouseManager = mouseManager;
		Launcher.inputData = mouseManager.getInputData();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public MouseManager getMouseManager() {
		return mouseManager;
	}

}