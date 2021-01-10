package quoridor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;
import quoridor.ui.BoardUI;

public class Main {
    private JFrame window;

    private Main() {
        initWindow();
        window.setVisible(true);
    }

    private void initWindow() {
        window = new JFrame();
        window.setTitle("Quoridor");

        window.setContentPane(new BoardUI().getPanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(new Point((screen.width - window.getWidth()) / 2, (screen.height - window.getHeight()) / 2));
    }

    public static void main(String[] args) {
        new Main();
    }
}
