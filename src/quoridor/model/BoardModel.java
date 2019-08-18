package quoridor.model;

import java.util.ArrayList;

public class BoardModel
{
	public static final int gridCount = 9;
	private static final int wallCount = 10;

	private int[][] wall;
	private Point[] player;
	private Grid[][] grid = new Grid[gridCount][gridCount];
	private int playerNow;
	private int[] wallLeft = new int[2];
	private int[][] puttableWall;
	private int playerWin = -1;

	public BoardModel()
	{
		wall = new int[gridCount - 1][gridCount - 1];
		player = new Point[2];
		player[0] = new Point(gridCount / 2, gridCount - 1);
		player[1] = new Point(gridCount / 2, 0);

		initGrid();
		wallLeft[0] = wallCount;
		wallLeft[1] = wallCount;
		computePuttableWall(true);
	}

	private void changePlayer()
	{
		playerWin = player[0].y ==  0 ? 0 : player[1].y == gridCount - 1 ? 1 : -1;
		if(playerWin < 0)
		{
			playerNow = 1 - playerNow;
			computePuttableWall(false);
		}
	}

	public int getPlayerWin()
	{
		return playerWin;
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

	private boolean checkLegal(int x, int y, ArrayList<Grid> checked, int player)
	{
		if(y == (player == 0 ? 0 : gridCount - 1))
		{
			return true;
		}
		if(checked.contains(grid[x][y]))
		{
			return false;
		}
		checked.add(grid[x][y]);
		for(int i = 0; i < 4; i++)
		{
			if(grid[x][y].getMovable(i))
			{
				int[] diff = Grid.convertion(i);
				if(checkLegal(x + diff[0], y + diff[1], checked, player))
				{
					return true;
				}
			}
		}
		return false;
	}

	private void computePuttableWall(boolean firstTime)
	{
		int[][] returnValue = new int[gridCount][gridCount];
		for(int y = 0; y < gridCount - 1; y++)
		{
			for(int x = 0; x < gridCount - 1; x++)
			{
				if(wall[x][y] == 0 || firstTime)
				{
					returnValue[x][y] = 0b11;
					if(firstTime)
					{
						continue;
					}
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
				if(!firstTime)
				{
					for(int i = 1; i <= 2; i++)
					{
						if((returnValue[x][y] & i) == i)
						{
							for(int p = 0; p < 2; p++)
							{
								boolean[] tempData = putWall(x, y, 3 - i, 1, null);
								if(!checkLegal(player[p].x, player[p].y, new ArrayList<Grid>(), p))
								{
									returnValue[x][y] = returnValue[x][y] & (3 - i);
								}
								putWall(x, y, 3 - i, 2, tempData);
							}
						}
					}
				}
			}
		}
		puttableWall = returnValue;
	}

	public int[][] getPuttableWall()
	{
		return puttableWall;
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

	public boolean[] putWall(int x, int y, int direction, int testMode, boolean[] tempData)
	{
		boolean[] returnValue = new boolean[4];
		boolean reverse = testMode == 2;
		wall[x][y] = direction;
		if(reverse)
		{
			wall[x][y] = 0;
		}
		if(direction == 1)
		{
			if(testMode == 1)
			{
				returnValue[0] = grid[x][y].getMovable(0, 1);
				returnValue[1] = grid[x][y + 1].getMovable(0, -1);
				returnValue[2] = grid[x + 1][y].getMovable(0, 1);
				returnValue[3] = grid[x + 1][y + 1].getMovable(0, -1);
			}
			grid[x][y].setMovable(0, 1, reverse ? tempData[0] : false);
			grid[x][y + 1].setMovable(0, -1, reverse ? tempData[1] : false);
			grid[x + 1][y].setMovable(0, 1, reverse ? tempData[2] : false);
			grid[x + 1][y + 1].setMovable(0, -1, reverse ? tempData[3] : false);
		}
		if(direction == 2)
		{
			if(testMode == 1)
			{
				returnValue[0] = grid[x][y].getMovable(1, 0);
				returnValue[1] = grid[x][y + 1].getMovable(1, 0);
				returnValue[2] = grid[x + 1][y].getMovable(-1, 0);
				returnValue[3] = grid[x + 1][y + 1].getMovable(-1, 0);
			}
			grid[x][y].setMovable(1, 0, reverse ? tempData[0] : false);
			grid[x][y + 1].setMovable(1, 0, reverse ? tempData[1] : false);
			grid[x + 1][y].setMovable(-1, 0, reverse ? tempData[2] : false);
			grid[x + 1][y + 1].setMovable(-1, 0, reverse ? tempData[3] : false);
		}
		if(testMode == 0)
		{
			wallLeft[playerNow]--;
			changePlayer();
		}
		return returnValue;
	}

	public void movePlayer(int p, int x, int y)
	{
		player[p].x = x;
		player[p].y = y;
		changePlayer();
	}

	private static class Grid
	{
		private boolean[] movable = new boolean[4];
		private Grid()
		{
			for(int i = 0; i < movable.length; i++)
			{
				movable[i] = true;
			}
		}

		private static int convertion(int dx, int dy)
		{
			return dx == 1 ? 0 : dx == -1 ? 1 : dy == 1 ? 2 : dy == -1 ? 3 : -1;
		}

		private static int[] convertion(int i)
		{
			return new int[] {i == 0 ? 1 : i == 1 ? -1 : 0, i == 2 ? 1 : i == 3 ? -1 : 0};
		}

		private boolean getMovable(int i)
		{
			return movable[i];
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

	public static class Point
	{
		public int x;
		public int y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public Point()
		{
			x = 0;
			y = 0;
		}

		public boolean equals(Point p)
		{
			return p.x == x && p.y == y;
		}

		public Point relative(int dx, int dy)
		{
			return new Point(x + dx, y + dy);
		}
	}
}