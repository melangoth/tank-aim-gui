import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
    Tank greenTank = new Tank(Color.GREEN, 50, 50);
    Tank redTank = new Tank(Color.RED, 80, 50);
    MonitorLine posMonitor = new MonitorLine(new Rectangle(10, 10, 75, 20), 2, MLINE_BASELINEOFFSET);
    MonitorLine analizeButton = new MonitorLine(new Rectangle(95, 10, 75, 20), 2, MLINE_BASELINEOFFSET, "Analize");
    // analizing
    private Image analImage = null;
    private ArrayList<SearchBlock> searchBlocks = new ArrayList<SearchBlock>();
    private int lastAnalTime = -1;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (analizeButton.inside(e.getX(), e.getY())) {
                    analizeImage();
                } else {
                    moveTank(greenTank, e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveTank(greenTank, e.getX(), e.getY());
            }
        });

        File f = new File("images/img1.png");
        try {
            analImage = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analizeImage() {
        Date start = new Date();

        Analizer anal = new Analizer(analImage);
        while (anal.hasSearchBlock()) {
//            sleepSome(500);
            SearchBlock block = anal.nextSearchBlock();
            searchBlocks.add(block);
        }

        int end = (new Date()).compareTo(start);
        analizeButton.setText(String.format("Analize (%d)", end));
        repaint();
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
        if ((tank.getCenterX() != x) || (tank.getCenterY() != y)) {
            // limit right
            int xx = Math.min(
                    this.getWidth() - INTERACT_MARGIN_RIGHT - tank.getWidth() / 2,
                    x
            );
            // limit left
            xx = Math.max(INTERACT_MARGIN_LEFT + tank.getWidth() / 2, xx);

            // limit bottom
            int yy = Math.min(
                    this.getHeight() - INTERACT_MARGIN_BOTTOM - tank.getHeight() / 2,
                    y
            );
            // limit top
            yy = Math.max(INTERACT_MARGIN_TOP + tank.getHeight() / 2, yy);

            tank.setCenter(xx, yy);

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
        posMonitor.setText(String.format("@ %d,%d", greenTank.getX(), greenTank.getY()));
        posMonitor.paintSprite(g);

        // Draw Analize button
        analizeButton.paintSprite(g);

        // Draw Tanks
        greenTank.paintSprite(g);
        redTank.paintSprite(g);
    }

    private void drawBackground(Graphics g) {
        g.drawImage(analImage, 0, 0, null);
    }
}