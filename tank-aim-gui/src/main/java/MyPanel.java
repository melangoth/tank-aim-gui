import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by develrage on 2016. 03. 25..
 */
class MyPanel extends JPanel {
    // global
    private static final int MLINE_BASELINEOFFSET = 3;
    private static final int INTERACT_MARGIN_TOP = 40;
    private static final int INTERACT_MARGIN_BOTTOM = 5;
    private static final int INTERACT_MARGIN_LEFT = 5;
    private static final int INTERACT_MARGIN_RIGHT = 5;
    // sprites
    Tank yellowTank = new Tank(Color.YELLOW);
    MonitorLine posMonitor = new MonitorLine(new Rectangle(10, 10, 100, 20), 2, MLINE_BASELINEOFFSET);
    MonitorLine analizeButton = new MonitorLine(new Rectangle(120, 10, 50, 20), 2, MLINE_BASELINEOFFSET, "Analize");
    // analizing
    private Image analImage = null;
    private ArrayList<SearchBlock> searchBlocks = new ArrayList<SearchBlock>();

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (analizeButton.inside(e.getX(), e.getY())) {
                    analizeImage();
                } else {
                    moveTank(yellowTank, e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveTank(yellowTank, e.getX(), e.getY());
            }
        });

        File f = new File("images/img5.png");
        try {
            analImage = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analizeImage() {
        Analizer anal = new Analizer(analImage);
        while (anal.hasSearchBlock()) {
//            sleepSome(500);
            SearchBlock block = anal.nextSearchBlock();
            searchBlocks.add(block);

            repaint();
        }
    }

    private void sleepSome(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        return new Dimension(800, 540);
    }

    public void paintComponent(Graphics g) {
//        System.out.println("Painting Component..." + (new Date()).getTime());
        super.paintComponent(g);

        // Draw background
        drawBackground(g);

        // Draw search blocks
        for (SearchBlock block : searchBlocks) {
            g.setColor(Color.MAGENTA);
            g.drawRect(block.getX(), block.getY(), block.getWidth(), block.getHeight());
        }

        // Draw monitor line 1
        posMonitor.setText(String.format("@ %d,%d", yellowTank.getX(), yellowTank.getY()));
        posMonitor.paintSprite(g);

        // Draw Analize button
        analizeButton.paintSprite(g);

        // Draw Yellow Tank
        yellowTank.paintSprite(g);
    }

    private void drawBackground(Graphics g) {
        g.drawImage(analImage, 0, 0, null);
    }
}