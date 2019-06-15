package board;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import main.Library;
import main.Point;
import main.Rect;

public class BoardControllerView
{
	private static final int gridCount = BoardModel.gridCount;
	private static final int gridSize = 48;
	private static final int paddingWidth = 16;
	private static final Color gridColor = Library.genColor(64);
	private static final Color gridMovableColor = new Color(255, 255, 128);
	private static final Color wallColor = new Color(128, 64, 0);
	private static final Color[] playerColor = {new Color(255, 128, 128), new Color(128, 255, 128)};

	private static final int playerSize = 32;
	private static final int playerOffSet = (gridSize - playerSize) / 2;

	public static final int totalSize = paddingWidth * (gridCount + 1) + gridSize * gridCount;

	private BoardModel bm;
	private ArrayList<Rect> inputList = new ArrayList<Rect>();

	public BoardControllerView()
	{
		bm = new BoardModel();

		refreshInputList();
	}

	private void refreshInputList()
	{
		inputList.clear();

		ArrayList<Point> movableInfo = bm.getMovableGrid(bm.getPlayerNow());
		for(Point p : movableInfo)
		{
			inputList.add(new Rect(p.x * (gridSize + paddingWidth) + paddingWidth, p.y * (gridSize + paddingWidth) + paddingWidth, gridSize, gridSize, () ->
			{
				bm.movePlayer(bm.getPlayerNow(), p.x, p.y);
			}));
		}
	}

	public boolean checkHover(int x, int y, boolean run)
	{
		for(Rect r : inputList)
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
		return false;
	}


	public void draw(Graphics2D g)
	{

		ArrayList<Point> movableInfo = bm.getMovableGrid(bm.getPlayerNow());

		g.setColor(gridColor);
		for(int y = 0; y < BoardModel.gridCount; y++)
		{
			for(int x = 0; x < BoardModel.gridCount; x++)
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
				int [][] wall = bm.getWall();
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
			g.fillOval(bm.getPlayer()[i].x * (gridSize + paddingWidth) + paddingWidth + playerOffSet, bm.getPlayer()[i].y * (gridSize + paddingWidth) + paddingWidth + playerOffSet, playerSize, playerSize);
		}


		int[][] wallable = bm.getPuttableWall();
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(16f));

		boolean showWallDebug = false;
		if(showWallDebug)
		{
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
}