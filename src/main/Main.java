package main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import board.BoardControllerView;

public class Main
{
	private static final int windowWidth = 800;
	private static final int windowHeight = 600;

	private JFrame window;
	private JPanel panel;
	private BoardControllerView bv;

	private Main()
	{
		initWindow();

		bv = new BoardControllerView();
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
				bv.draw((Graphics2D) g);
			}
		};
		panel.setBackground(Library.genColor(255));
		panel.setPreferredSize(new Dimension(windowWidth, windowHeight));
		panel.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				panel.setCursor(new Cursor(bv.checkHover(e.getX(), e.getY(), false) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});
		panel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				bv.checkHover(e.getX(), e.getY(), true);
				panel.repaint();
			}
		});

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