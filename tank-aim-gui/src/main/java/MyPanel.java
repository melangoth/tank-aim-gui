import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by develrage on 2016. 03. 25..
 */
class MyPanel extends JPanel {
    public static final int MONITORLINE1X = 10;
    public static final int MONITORLINE1Y = 35;
    private int squareX = 50;
    private int squareY = 50;
    private int squareW = 20;
    private int squareH = 20;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(),e.getY());
            }
        });
    }

    private void moveSquare(int x, int y) {
        int OFFSET = 1;

        // repaint only if moved
        if ((squareX!=x) || (squareY!=y)) {

            // repaint monitor area
            repaint(MONITORLINE1X, MONITORLINE1Y-15, 100, 15);

            // repaint old position area
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
            squareX=x;
            squareY=y;

            // repaint new position area
            repaint(squareX,squareY,squareW+OFFSET,squareH+OFFSET);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.drawString("This is my custom Panel!",10,20);
        g.drawString(String.format("@ %d,%d", squareX, squareY), MONITORLINE1X, MONITORLINE1Y);

        // Draw Rect
        g.setColor(Color.RED);
        g.fillRect(squareX,squareY,squareW,squareH);
        g.setColor(Color.BLACK);
        g.drawRect(squareX,squareY,squareW,squareH);
    }
}