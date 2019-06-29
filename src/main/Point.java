package main;

public class Point
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