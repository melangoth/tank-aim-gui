import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

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

    // sprites
    Tank yellowTank = new Tank(Color.YELLOW);
    MonitorLine posMonitor = new MonitorLine(new Rectangle(10, 10, 100, 20), 2, MLINE_BASELINEOFFSET);

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

        // Draw background
        drawBackground(g);

        // Draw monitor line 1
        posMonitor.setText(String.format("@ %d,%d", yellowTank.getX(), yellowTank.getY()));
        posMonitor.paintSprite(g);

        // Draw Yellow Tank
        yellowTank.paintSprite(g);
    }

    private void drawBackground(Graphics g) {
        File f = new File("images/img2.png");
        Image bgimage = null;
        try {
            bgimage = ImageIO.read(f);
            g.drawImage(bgimage, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}