package board;

import java.util.ArrayList;
import main.Point;

public class BoardModel
{
	public static final int gridCount = 9;
	private static final int wallCount = 10;

	private int[][] wall;
	private Point[] player;
	private Grid[][] grid = new Grid[gridCount][gridCount];
	private int playerNow;
	private int[] wallLeft = new int[2];

	protected BoardModel()
	{
		wall = new int[gridCount - 1][gridCount - 1];
		player = new Point[2];
		player[0] = new Point(gridCount / 2, gridCount - 1);
		player[1] = new Point(gridCount / 2, 0);

		initGrid();
		wallLeft[0] = wallCount;
		wallLeft[1] = wallCount;

		/*
		putWall(0, 0, 1);
		putWall(1, 1, 2);
		putWall(4, 7, 2);
		putWall(7, 4, 2);
		putWall(0, 4, 1);
		putWall(1, 4, 2);
		 */
	}

	private void changePlayer()
	{
		playerNow = 1 - playerNow;
	}

	public int getPlayerNow()
	{
		return playerNow;
	}

	public int[][] getWall()
	{
		return wall;
	}

	public Point[] getPlayer()
	{
		return player;
	}

	public Grid[][] getGrid()
	{
		return grid;
	}

	public int getWallCount(int player)
	{
		return wallLeft[player];
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

	public ArrayList<Point> getMovableGrid(int playerNow)
	{
		ArrayList<Point> returnValue = new ArrayList<Point>();
		int x = player[playerNow].x;
		int y = player[playerNow].y;
		Point opponent = player[1 - playerNow];
		Grid g = grid[x][y];

		int[][] direction = new int[][]{
			{0, -1},
			{0, 1},
			{-1, 0},
			{1, 0},
		};

		for(int i = 0; i < direction.length; i++)
		{
			if(g.getMovable(direction[i][0], direction[i][1]))
			{
				Point p = new Point(x + direction[i][0], y + direction[i][1]);
				if(!p.equals(opponent))
				{
					returnValue.add(p);
				}
				else
				{
					Grid gR = grid[p.x][p.y];
					if(gR.getMovable(direction[i][0], direction[i][1]))
					{
						returnValue.add(p.relative(direction[i][0], direction[i][1]));
					}
					else
					{
						int temp = i < 2 ? 2 : 0;
						for(int j = 0; j < 2; j++)
						{
							if(gR.getMovable(direction[j + temp][0], direction[j + temp][1]))
							{
								returnValue.add(p.relative(direction[j + temp][0], direction[j + temp][1]));
							}
						}
					}
				}
			}
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
				if(x == 0 || x == gridCount - 1)
				{
					grid[x][y].setMovable(x == 0 ? -1 : 1, 0, false);
				}
				if(y == 0 || y == gridCount - 1)
				{
					grid[x][y].setMovable(0, y == 0 ? -1 : 1, false);
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
			grid[x][y].setMovable(0, 1, false);
			grid[x][y + 1].setMovable(0, -1, false);
			grid[x + 1][y].setMovable(0, 1, false);
			grid[x + 1][y + 1].setMovable(0, -1, false);
		}
		//Vertical
		if(direction == 2)
		{
			grid[x][y].setMovable(1, 0, false);
			grid[x][y + 1].setMovable(1, 0, false);
			grid[x + 1][y].setMovable(-1, 0, false);
			grid[x + 1][y + 1].setMovable(-1, 0, false);
		}
	}

	public void movePlayer(int p, int x, int y)
	{
		player[p].x = x;
		player[p].y = y;
		changePlayer();
	}

	private class Grid
	{
		private boolean[] movable = new boolean[4];
		private Grid()
		{
			for(int i = 0; i < movable.length; i++)
			{
				movable[i] = true;
			}
		}

		private int convertion(int dx, int dy)
		{
			return dx == 1 ? 0 : dx == -1 ? 1 : dy == 1 ? 2 : dy == -1 ? 3 : -1;
		}

		private boolean getMovable(int dx, int dy)
		{
			return movable[convertion(dx, dy)];
		}

		private void setMovable(int dx, int dy, boolean b)
		{
			movable[convertion(dx, dy)] = b;
		}
	}
}