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

    // sprites
    Tank yellowTank = new Tank(Color.YELLOW);

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveTank(yellowTank, e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveTank(yellowTank, e.getX(), e.getY());
            }
        });
    }

    private void moveTank(Tank tank, int x, int y) {
        // repaint only if moved
        if ((tank.getX() != x) || (tank.getY() != y)) {
            int xx = Math.min(
                    this.getWidth() - INTERACT_MARGIN_RIGHT - tank.getWidth(),
                    x
            );
            xx = Math.max(INTERACT_MARGIN_LEFT, xx);
            tank.setX(xx);

            int yy = Math.min(
                    this.getHeight() - INTERACT_MARGIN_BOTTOM - tank.getHeight(),
                    y
            );
            yy = Math.max(INTERACT_MARGIN_TOP, yy);
            tank.setY(yy);

            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(480, 320);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.drawString(String.format("@ %d,%d", yellowTank.getX(), yellowTank.getY()),
                MLINE_1_LEFT + MLINE_1_PADDING,
                MLINE_1_TOP + MLINE_1_HEIGHT - MLINE_BASELINEOFFSET - MLINE_1_PADDING);
        g.drawRect(MLINE_1_LEFT, MLINE_1_TOP, MLINE_1_WIDTH, MLINE_1_HEIGHT);

        // Draw Yellow Tank
        yellowTank.paintSprite(g);
    }
}