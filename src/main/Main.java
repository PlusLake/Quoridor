package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main
{
	private static final int windowWidth = 800;
	private static final int windowHeight = 600;

	private JFrame window;
	private JPanel panel;
	private Board board;

	private Main()
	{
		initWindow();

		board = new Board();
		window.setVisible(true);
	}

	private void initWindow()
	{
		window = new JFrame();
		window.setTitle("Quoridor");
		panel = new JPanel(null)
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.translate((windowWidth - Board.totalSize) / 2, (windowHeight - Board.totalSize) / 2);
				board.drawBoard((Graphics2D) g);
			}
		};
		panel.setBackground(Library.genColor(255));
		panel.setPreferredSize(new Dimension(windowWidth, windowHeight));
		window.setContentPane(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();

		Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(new Point((windowSize.width - window.getWidth()) / 2, (windowSize.height - window.getHeight()) / 2));
	}

	public static void main(String[] args)
	{
		new Main();
	}
}