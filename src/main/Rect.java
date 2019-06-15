package main;

public class Rect
{
	private int x;
	private int y;
	private int width;
	private int height;
	private CallBack callBack;

	public Rect(int x, int y, int width, int height, CallBack cb)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.callBack = cb;
	}

	public boolean intersects(int px, int py)
	{
		return (px >= x) && (px < x + width) && (py >= y) && (py < y + height);
	}

	public void run()
	{
		callBack.run();
	}
}