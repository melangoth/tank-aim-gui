import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by develrage on 2016. 03. 25..
 */
class MyPanel extends JPanel {
    // monitor line 1
    public static final int MLINE_1_LEFT = 10;
    public static final int MLINE_1_BOTTOM = 20;
    public static final int MLINE_1_WIDTH = 100;
    public static final int MLINE_1_HEIGHT = 15;

    // red square
    private int squareX = 50;
    private int squareY = 50;
    private int squareW = 20;
    private int squareH = 20;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });
    }

    private void moveSquare(int x, int y) {
        System.out.println("Moving square.");
        int OFFSET = 1;

        // repaint only if moved
        if ((squareX != x) || (squareY != y)) {

            // repaint monitor area
            repaint(MLINE_1_LEFT, MLINE_1_BOTTOM - MLINE_1_HEIGHT, MLINE_1_WIDTH, MLINE_1_HEIGHT);

            // repaint old position area
            repaint(squareX, squareY, squareW + OFFSET, squareH + OFFSET);
            squareX = x;
            squareY = y;

            // repaint new position area
            repaint(squareX, squareY, squareW + OFFSET, squareH + OFFSET);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(250, 200);
    }

    public void paintComponent(Graphics g) {
        System.out.println("Painting component.");
        super.paintComponent(g);

        // Draw Text
        g.drawString(String.format("@ %d,%d", squareX, squareY), MLINE_1_LEFT, MLINE_1_BOTTOM);

        // Draw Rect
        g.setColor(Color.RED);
        g.fillRect(squareX, squareY, squareW, squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX, squareY, squareW, squareH);
    }
}