package board;

import java.util.ArrayList;
import main.Point;

public class BoardModel
{
	protected static final int gridCount = 9;


	private int[][] wall;
	private Point[] player;
	private Grid[][] grid = new Grid[gridCount][gridCount];
	private int playerNow;

	protected BoardModel()
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

	private void changePlayer()
	{
		playerNow = 1 - playerNow;
	}

	protected int getPlayerNow()
	{
		return playerNow;
	}

	protected int[][] getWall()
	{
		return wall;
	}

	protected Point[] getPlayer()
	{
		return player;
	}

	protected Grid[][] getGrid()
	{
		return grid;
	}


	protected int[][] getPuttableWall()
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

	protected ArrayList<Point> getMovableGrid(int p)
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

	protected void putWall(int x, int y, int direction)
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

	protected void movePlayer(int p, int x, int y)
	{
		player[p].x = x;
		player[p].y = y;
		changePlayer();
	}

	private class Grid
	{
		private boolean movableUp = true;
		private boolean movableDown = true;
		private boolean movableLeft = true;
		private boolean movableRight = true;
	}
}