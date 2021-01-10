package quoridor.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import lombok.Getter;

public class Wall extends JComponent {
    private static BoardUI.WallInfo wallInfo = BoardUI.getWallInfo();
    private static int width = wallInfo.getWidth();
    private static int length = wallInfo.getLength();
    private static Color color = wallInfo.getColor();

    @Getter
    private boolean rotated;

    public Wall(Runnable cb) {
        setSize(width, length);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) {
                    passMoveToParent(true);
                }
                if (e.getButton() == 3) {
                    rotate();
                    passMoveToParent(false);
                    cb.run();
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                passMoveToParent(false);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                passMoveToParent(false);
            }
        });
    }

    private void passMoveToParent(boolean press) {
        Container c = Wall.this.getParent();
        c.dispatchEvent(new MouseEvent(c, press ? 501 : 503, System.currentTimeMillis(), 0, c.getMousePosition().x,
                c.getMousePosition().y, c.getMousePosition().x, c.getMousePosition().y, 0, false, 0));
    }

    @Override
    protected void paintComponent(Graphics gRaw) {
        Graphics2D g = (Graphics2D) gRaw;
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void rotate() {
        rotated = !rotated;
        setSize(rotated ? length : width, rotated ? width : length);
    }
}
