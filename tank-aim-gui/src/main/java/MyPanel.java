import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by develrage on 2016. 03. 25..
 */
class MyPanel extends JPanel {
    // global
    public static final int MLINE_BASELINEOFFSET = 3;
    public static final int INTERACT_MARGIN_TOP = 40;
    public static final int INTERACT_MARGIN_BOTTOM = 5;
    public static final int INTERACT_MARGIN_LEFT = 5;
    public static final int INTERACT_MARGIN_RIGHT = 5;

    // monitor line 1
    public static final int MLINE_1_LEFT = 10;
    public static final int MLINE_1_TOP = 10;
    public static final int MLINE_1_WIDTH = 100;
    public static final int MLINE_1_HEIGHT = 20;
    public static final int MLINE_1_PADDING = 2;

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
        // repaint only if moved
        if ((squareX != x) || (squareY != y)) {
            squareX = Math.min(Math.max(x, INTERACT_MARGIN_LEFT), this.getWidth() - INTERACT_MARGIN_RIGHT - squareW);
            squareY = Math.min(Math.max(y, INTERACT_MARGIN_TOP), this.getHeight() - INTERACT_MARGIN_BOTTOM - squareH);

            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(480, 320);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.drawString(String.format("@ %d,%d", squareX, squareY),
                MLINE_1_LEFT + MLINE_1_PADDING,
                MLINE_1_TOP + MLINE_1_HEIGHT - MLINE_BASELINEOFFSET - MLINE_1_PADDING);
        g.drawRect(MLINE_1_LEFT, MLINE_1_TOP, MLINE_1_WIDTH, MLINE_1_HEIGHT);

        // Draw Rect
        g.setColor(Color.YELLOW);
        g.fillRect(squareX, squareY, squareW, squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX, squareY, squareW, squareH);
    }
}