package quoridor.common;

public class MouseCheck {
    private int x;
    private int y;
    private int width;
    private int height;
    private Runnable callBack;

    public MouseCheck(int x, int y, int width, int height, Runnable callback) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.callBack = callback;
    }

    public MouseCheck(int x, int y, int radius, Runnable callback) {
        this.x = x;
        this.y = y;
        this.width = radius;
        this.callBack = callback;
    }

    public boolean intersects(int px, int py) {
        return height == 0 ? (Math.sqrt((px - (x)) * (px - x) + (py - y) * (py - y)) < width)
                : (px >= x) && (px < x + width) && (py >= y) && (py < y + height);
    }

    public void run() {
        callBack.run();
    }
}
