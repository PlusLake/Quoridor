package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class Board
{
	private static final int gridCount = 9;
	private static final int gridSize = 48;
	private static final int paddingWidth = 16;
	private static final Color gridColor = Library.genColor(64);
	private static final Color gridMovableColor = new Color(255, 255, 128);
	private static final Color wallColor = new Color(128, 64, 0);
	private static final Color[] playerColor = {new Color(255, 128, 128), new Color(128, 255, 128)};

	private static final int playerSize = 32;
	private static final int playerOffSet = (gridSize - playerSize) / 2;

	protected static final int totalSize = paddingWidth * (gridCount + 1) + gridSize * gridCount;

	private int[][] wall;
	private Point[] player;
	private Grid[][] grid = new Grid[gridCount][gridCount];

	protected Board()
	{
		wall = new int[gridCount - 1][gridCount - 1];
		player = new Point[2];
		player[0] = new Point(gridCount / 2, gridCount - 1);
		player[1] = new Point(gridCount / 2, 0);

		initGrid();
		putWall(0, 0, 1);
		putWall(1, 1, 2);
		putWall(4, 7, 2);
		putWall(7, 4, 2);
		putWall(0, 4, 1);
		putWall(1, 4, 2);
	}

	public int[][] getPuttableWall()
	{
		int[][] returnValue = new int[gridCount][gridCount];

		for(int y = 0; y < gridCount - 1; y++)
		{
			for(int x = 0; x < gridCount - 1; x++)
			{
				if(wall[x][y] == 0)
				{
					returnValue[x][y] = 0b11;
				}
				if(x - 1 >= 0 && wall[x - 1][y] == 1)
				{
					returnValue[x][y] = returnValue[x][y] & 0b01;
				}
				if(x + 1 < gridCount - 1 && wall[x + 1][y] == 1)
				{
					returnValue[x][y] = returnValue[x][y] & 0b01;
				}

				if(y - 1 >= 0 && wall[x][y - 1] == 2)
				{
					returnValue[x][y] = returnValue[x][y] & 0b10;
				}
				if(y + 1 < gridCount - 1 && wall[x][y + 1] == 2)
				{
					returnValue[x][y] = returnValue[x][y] & 0b10;
				}
			}
		}

		return returnValue;
	}

	public ArrayList<Point> getMovableGrid(int p)
	{
		ArrayList<Point> returnValue = new ArrayList<Point>();
		int x = player[p].x;
		int y = player[p].y;
		Grid g = grid[x][y];
		if(g.movableUp)
		{
			returnValue.add(new Point(x, y - 1));
		}
		if(g.movableDown)
		{
			returnValue.add(new Point(x, y + 1));
		}
		if(g.movableLeft)
		{
			returnValue.add(new Point(x - 1, y));
		}
		if(g.movableRight)
		{
			returnValue.add(new Point(x + 1, y));
		}
		return returnValue;
	}

	private void initGrid()
	{
		for(int y = 0; y < gridCount; y++)
		{
			for(int x = 0; x < gridCount; x++)
			{
				grid[x][y] = new Grid();
				if(x == 0)
				{
					grid[x][y].movableLeft = false;
				}
				if(x == gridCount - 1)
				{
					grid[x][y].movableRight = false;
				}
				if(y == 0)
				{
					grid[x][y].movableUp = false;
				}
				if(y == gridCount - 1)
				{
					grid[x][y].movableDown = false;
				}
			}
		}
	}

	public void putWall(int x, int y, int direction)
	{
		wall[x][y] = direction;

		//Horizontal
		if(direction == 1)
		{
			grid[x][y].movableDown = false;
			grid[x][y + 1].movableUp = false;
			grid[x + 1][y].movableDown = false;
			grid[x + 1][y + 1].movableUp = false;
		}
		//Vertical
		if(direction == 2)
		{
			grid[x][y].movableRight = false;
			grid[x + 1][y].movableLeft = false;
			grid[x][y + 1].movableRight = false;
			grid[x + 1][y + 1].movableLeft = false;
		}
	}

	public void movePlayer(int p, int dx, int dy)
	{
		player[p].x += dx;
		player[p].y += dy;
	}

	private class Grid
	{
		private boolean movableUp = true;
		private boolean movableDown = true;
		private boolean movableLeft = true;
		private boolean movableRight = true;
	}


	// debug UIs
	protected void drawBoard(Graphics2D g)
	{

		ArrayList<Point> movableInfo = getMovableGrid(0);

		g.setColor(gridColor);
		for(int y = 0; y < gridCount; y++)
		{
			for(int x = 0; x < gridCount; x++)
			{
				for(Point p : movableInfo)
				{
					if(p.x == x && p.y == y)
					{
						g.setColor(gridMovableColor);
						break;
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
				if(wall[x][y] == 1)
				{
					g.fillRect((x + 1) * (gridSize + paddingWidth) - gridSize, (y + 1) * (gridSize + paddingWidth), gridSize * 2 + paddingWidth, paddingWidth);
				}
				if(wall[x][y] == 2)
				{
					g.fillRect((x + 1) * (gridSize + paddingWidth), (y + 1) * (gridSize + paddingWidth) - gridSize, paddingWidth, gridSize * 2 + paddingWidth);
				}
			}
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i = 0; i < 2; i++)
		{
			g.setColor(playerColor[i]);
			g.fillOval(player[i].x * (gridSize + paddingWidth) + paddingWidth + playerOffSet, player[i].y * (gridSize + paddingWidth) + paddingWidth + playerOffSet, playerSize, playerSize);
		}


		int[][] wallable = getPuttableWall();
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(16f));
		for(int y = 0; y < gridCount - 1; y++)
		{
			for(int x = 0; x < gridCount - 1; x++)
			{
				if(wallable[x][y] == 0b11)
				{
					g.drawString("全", (x + 1) * (gridSize + paddingWidth), (y + 1) * (gridSize + paddingWidth) + 14);
				}
				if(wallable[x][y] == 0b10)
				{
					g.drawString("横", (x + 1) * (gridSize + paddingWidth), (y + 1) * (gridSize + paddingWidth) + 14);
				}
				if(wallable[x][y] == 0b01)
				{
					g.drawString("縦", (x + 1) * (gridSize + paddingWidth), (y + 1) * (gridSize + paddingWidth) + 14);
				}
			}
		}
	}
}