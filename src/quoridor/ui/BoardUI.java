package quoridor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import quoridor.common.CallBack;
import quoridor.common.Library;
import quoridor.common.MouseCheck;
import quoridor.model.BoardModel;
import quoridor.model.BoardModel.Point;

public class BoardUI
{
	private static final int canvasWidth = 800;
	private static final int canvasHeight = 600;

	private static final int paddingWidth = 15;
	private static final int paddingHalfWidth = paddingWidth / 2;
	private static final int gridSize = 47;
	private static final int gridSizePaddingWidth = gridSize + paddingWidth;
	private static final int gridCount = BoardModel.gridCount;
	private static final int wallLength = gridSize * 2 + paddingWidth;
	private static final int wallHalfLength = wallLength / 2;
	private static final Color gridColor = Library.genColor(64);
	private static final Color gridMovableColor[] = new Color[] {new Color(255, 192, 192), new Color(192, 255, 128)};
	private static final Color wallColor = new Color(128, 64, 0);
	private static final Color[] playerColor = {new Color(255, 128, 128), new Color(128, 255, 128)};
	private static final Color[] playerColorWin = {new Color(192, 64, 64), new Color(64, 192, 64)};
	private static final Color infoPanelColor = Library.genColor(192);
	private static final Color infoPanelNameColor = Library.genColor(224);
	private static final Color fontColor = Library.genColor(16);
	private static final Color infoPanelStickColor = Library.genColor(160);
	private static final Color circleNormal = new Color(255, 255, 192);
	private static final Path2D star = genStar();
	private static final BasicStroke starStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final double gridHalfSize = gridSize / 2;

	private static final int totalSize = paddingWidth * (gridCount + 1) + gridSize * gridCount;
	private static final Point boardOffset = new Point((canvasWidth - totalSize) / 2, (canvasHeight - totalSize) / 2);
	private static final int playerSize = 31;
	private static final int playerOffSet = (gridSize - playerSize) / 2;
	private static final int infoPanelHeight = 192;
	private static final int infoPanelWidth = boardOffset.x - paddingWidth;
	private static final int infoPanelRx = canvasWidth - infoPanelWidth - paddingWidth;
	private static final int infoPanelLy = boardOffset.y + paddingWidth;
	private static final int infoPanelRy = canvasHeight - boardOffset.y - infoPanelHeight - paddingWidth;
	private static final int infoPanelNameHeight = 32;
	private static final int infoPanelNameRy = infoPanelRy + infoPanelHeight - infoPanelNameHeight;
	private static final int infoPanelStringOffset = -8;
	private static final int infoPanelNameStringLy = infoPanelLy + infoPanelNameHeight + infoPanelStringOffset;
	private static final int infoPanelNameStringRy = infoPanelNameRy + infoPanelNameHeight + infoPanelStringOffset;
	private static final int infoPanelStickLy = infoPanelLy + infoPanelHeight - infoPanelNameHeight;
	private static final int infoPanelStickStringLy = infoPanelStickLy + infoPanelNameHeight + infoPanelStringOffset;
	private static final int infoPanelStickStringRy = infoPanelRy + infoPanelNameHeight + infoPanelStringOffset;
	private static final int infoPanelWallLx = paddingWidth + (infoPanelWidth - paddingWidth) / 2;
	private static final int infoPanelWallRx = infoPanelRx + (infoPanelWidth - paddingWidth) / 2;
	private static final int infoPanelWallLy = infoPanelLy + (infoPanelHeight - wallLength) / 2;
	private static final int infoPanelWallRy = infoPanelRy + (infoPanelHeight - wallLength) / 2;
	private static final int circleRadius = 16;
	private static final int circleDiameter = circleRadius * 2;
	private static final int restartButtonSize = gridSize;
	private static final int restartButtonY = canvasHeight - restartButtonSize - paddingWidth - boardOffset.y;

	private static Font font;

	private BoardModel bm;
	private ArrayList<MouseCheck> inputList = new ArrayList<MouseCheck>();
	private String[] playerName = new String[2];
	private int[] playerNameOffset = new int[2];
	private int [] stickCountOffset = new int[2];
	private JPanel panel;
	private boolean wallMode = false;
	private Wall wall;
	private BufferedImage restartButton;
	private int restartButtonOffset;

	public static WallInfo getWallInfo()
	{
		WallInfo wi = new WallInfo();
		wi.color = wallColor;
		wi.length = wallLength;
		wi.width = paddingWidth;
		return wi;
	}

	public BoardUI()
	{
		initPanel();
		bm = new BoardModel();

		try
		{
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(("/quoridor/resource/consola.ttf"))).deriveFont(20f);
			restartButton = ImageIO.read(getClass().getResourceAsStream("/quoridor/resource/refresh.png"));
			restartButtonOffset = (restartButtonSize - restartButton.getWidth()) / 2;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for(int i = 0; i < 2; i++)
		{
			setPlayerName(i, "Player" + i);
		}

		setStickOffset();
		refreshInputList();
	}

	public JPanel getPanel()
	{
		return panel;
	}

	private void initPanel()
	{
		panel = new JPanel(null)
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				draw((Graphics2D) g);
			}
		};

		panel.setBackground(Library.genColor(255));
		panel.setPreferredSize(new Dimension(canvasWidth, canvasHeight));

		panel.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				panel.setCursor(new Cursor(checkHover(e.getX(), e.getY(), false) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
		});

		panel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				checkHover(e.getX(), e.getY(), true);
				panel.repaint();
			}
		});
	}

	private void setPlayerName(int player, String name)
	{
		playerName[player] = name;
		playerNameOffset[player] = (infoPanelWidth - Library.getTextWidth(font, name)) / 2 ;
	}

	private void setStickOffset()
	{
		stickCountOffset[0] = (infoPanelWidth - Library.getTextWidth(font, String.valueOf(bm.getWallCount(0)))) / 2 + infoPanelRx;
		stickCountOffset[1] = (infoPanelWidth - Library.getTextWidth(font, String.valueOf(bm.getWallCount(1)))) / 2 + paddingWidth;
	}

	private void moveWall(int x, int y)
	{
		if(wall != null)
		{
			wall.setLocation(x - (wall.getState() ? wallHalfLength : paddingHalfWidth ), y - (wall.getState() ? paddingHalfWidth : wallHalfLength));
		}
	}

	private void refreshInputList()
	{
		inputList.clear();
		inputList.add(new MouseCheck(paddingWidth, restartButtonY, restartButtonSize, restartButtonSize, () ->
		{
			bm = new BoardModel();
			setStickOffset();
			refreshInputList();
		}));

		if(bm.getPlayerWin() >= 0)
		{
			return;
		}

		if(!wallMode)
		{
			ArrayList<Point> movableInfo = bm.getMovableGrid(bm.getPlayerNow());
			for(Point p : movableInfo)
			{
				inputList.add(new MouseCheck(boardOffset.x + p.x * (gridSize + paddingWidth) + paddingWidth, boardOffset.y + p.y * (gridSize + paddingWidth) + paddingWidth, gridSize, gridSize, () ->
				{
					bm.movePlayer(bm.getPlayerNow(), p.x, p.y);
				}));
			}

			if(bm.getWallCount(bm.getPlayerNow()) > 0)
			{
				CallBack cbWall = () ->
				{
					wallMode = true;
					panel.add(wall = new Wall(() -> refreshInputList()));
					panel.addMouseMotionListener(new MouseAdapter()
					{
						@Override
						public void mouseMoved(MouseEvent e)
						{
							moveWall(e.getX(), e.getY());
						}
					});
					moveWall(panel.getMousePosition().x, panel.getMousePosition().y);
					refreshInputList();
				};
				inputList.add(new MouseCheck(bm.getPlayerNow() == 0 ? infoPanelWallRx : infoPanelWallLx, bm.getPlayerNow() == 0 ? infoPanelWallRy : infoPanelWallLy, paddingWidth, wallLength, cbWall));
			}
		}
		else
		{
			for(int y = 0; y < gridCount - 1; y++)
			{
				for(int x = 0; x < gridCount - 1; x++)
				{
					int[][] puttable = bm.getPuttableWall();
					if((puttable[x][y] & (wall.getState() ? 2 : 1)) > 0)
					{
						final int dx = x;
						final int dy = y;
						inputList.add(new MouseCheck(boardOffset.x + (gridSize + paddingWidth) * (x + 1) - paddingHalfWidth + circleRadius, boardOffset.y + (gridSize + paddingWidth) * (y + 1) - paddingHalfWidth + circleRadius, circleRadius, () ->
						{
							bm.putWall(dx, dy, wall.getState() ? 1 : 2, 0, null);
							wallMode = false;
							panel.remove(wall);
							setStickOffset();
							refreshInputList();
						}));
					}
				}
			}
		}
		panel.repaint();
	}

	private boolean checkHover(int x, int y, boolean run)
	{
		for(MouseCheck r : inputList)
		{
			if(r.intersects(x, y))
			{
				if(run)
				{
					r.run();
					refreshInputList();
				}
				return true;
			}
		}
		if(run && wallMode)
		{
			wallMode = false;
			panel.remove(wall);
			refreshInputList();
		}
		return false;
	}

	private void drawBoard(Graphics2D g)
	{
		ArrayList<Point> movableInfo = bm.getMovableGrid(bm.getPlayerNow());
		g.setColor(gridColor);
		for(int y = 0; y < BoardModel.gridCount; y++)
		{
			for(int x = 0; x < BoardModel.gridCount; x++)
			{
				if(bm.getPlayerWin() < 0)
				{
					for(Point p : movableInfo)
					{
						if(p.x == x && p.y == y)
						{
							g.setColor(gridMovableColor[bm.getPlayerNow()]);
							break;
						}
					}
				}
				g.fillRect(x * (gridSize + paddingWidth) + paddingWidth, y * (gridSize + paddingWidth) + paddingWidth, gridSize, gridSize);
				g.setColor(gridColor);
			}
		}

		g.setColor(wallColor);
		for(int y = 0; y < gridCount - 1; y++)
		{
			for(int x = 0; x < gridCount - 1; x++)
			{
				int [][] wall = bm.getWall();
				if(wall[x][y] == 1)
				{
					g.fillRect((x + 1) * (gridSize + paddingWidth) - gridSize, (y + 1) * (gridSize + paddingWidth), wallLength, paddingWidth);
				}
				if(wall[x][y] == 2)
				{
					g.fillRect((x + 1) * (gridSize + paddingWidth), (y + 1) * (gridSize + paddingWidth) - gridSize, paddingWidth, wallLength);
				}
			}
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 0; i < 2; i++)
		{
			int tX = bm.getPlayer()[i].x * gridSizePaddingWidth + paddingWidth;
			int tY = bm.getPlayer()[i].y * gridSizePaddingWidth + paddingWidth;
			g.translate(tX, tY);
			g.setColor(playerColor[i]);
			g.fillOval(playerOffSet, playerOffSet, playerSize, playerSize);

			if(bm.getPlayerWin() == i)
			{
				g.setStroke(starStroke);
				g.setColor(playerColorWin[i]);
				g.translate(gridHalfSize, gridHalfSize);
				g.fill(star);
				g.draw(star);
				g.translate(-gridHalfSize, -gridHalfSize);
			}

			g.translate(-tX, -tY);
		}

		if(wallMode)
		{
			int[][] puttable = bm.getPuttableWall();
			g.setColor(circleNormal);
			for(int y = 0; y < gridCount - 1; y++)
			{
				for(int x = 0; x < gridCount - 1; x++)
				{
					if((puttable[x][y] & (wall.getState() ? 2 : 1)) > 0)
					{
						g.fillOval((gridSize + paddingWidth) * (x + 1) - paddingHalfWidth, (gridSize + paddingWidth) * (y + 1) - paddingHalfWidth, circleDiameter, circleDiameter);
					}
				}
			}
		}
	}

	private void drawInfo(Graphics2D g)
	{
		g.setColor(infoPanelColor);
		g.fillRect(paddingWidth, infoPanelLy, infoPanelWidth, infoPanelHeight);
		g.fillRect(infoPanelRx, infoPanelRy, infoPanelWidth, infoPanelHeight);

		g.setColor(infoPanelNameColor);
		g.fillRect(paddingWidth, infoPanelLy, infoPanelWidth, infoPanelNameHeight);
		g.fillRect(infoPanelRx, infoPanelNameRy, infoPanelWidth, infoPanelNameHeight);
		g.fillRect(paddingWidth, infoPanelStickLy, infoPanelWidth, infoPanelNameHeight);
		g.fillRect(infoPanelRx, infoPanelRy, infoPanelWidth, infoPanelNameHeight);

		g.fillRect(paddingWidth, restartButtonY, restartButtonSize, restartButtonSize);
		g.drawImage(restartButton, paddingWidth + restartButtonOffset, restartButtonY + restartButtonOffset, null);

		g.setColor(fontColor);
		g.setFont(font);

		g.drawString(playerName[1], paddingWidth + playerNameOffset[1], infoPanelNameStringLy);
		g.drawString(playerName[0], infoPanelRx + playerNameOffset[0], infoPanelNameStringRy);

		g.drawString(String.valueOf(bm.getWallCount(0)), stickCountOffset[0], infoPanelStickStringRy);
		g.drawString(String.valueOf(bm.getWallCount(1)), stickCountOffset[1], infoPanelStickStringLy);

		g.setColor(infoPanelStickColor);
		if(bm.getWallCount(1) > 0)
		{
			g.fillRect(infoPanelWallLx, infoPanelWallLy, paddingWidth, wallLength);
		}
		if(bm.getWallCount(0) > 0)
		{
			g.fillRect(infoPanelWallRx, infoPanelWallRy, paddingWidth, wallLength);
		}
	}

	private static Path2D genStar()
	{
		Path2D p = new Path2D.Double();
		double length = 12;
		double rate = 0.32;
		int count = 10;
		p.moveTo(0, -length);
		for(int k = 1; k < count; k++)
		{
			double angle = Math.PI * 2 / count * k - Math.PI / 2;
			double tempLen = k % 2 == 0 ? length : length * rate;
			p.lineTo(tempLen * Math.cos(angle), tempLen * Math.sin(angle));
		}
		p.closePath();
		return p;
	}

	private void draw(Graphics2D g)
	{
		g.translate(boardOffset.x, boardOffset.y);
		drawBoard(g);
		g.translate(-boardOffset.x, -boardOffset.y);

		drawInfo(g);
	}

	public static class WallInfo
	{
		private int width;
		private int length;
		private Color color;

		public int getWidth()
		{
			return width;
		}

		public int getLength()
		{
			return length;
		}

		public Color getColor()
		{
			return color;
		}
	}
}