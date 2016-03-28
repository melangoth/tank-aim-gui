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
    Tank greenTank = new Tank(Color.GREEN, 131, 281);
    Tank redTank = new Tank(Color.RED, 660, 285);
    MonitorLine posMonitor = new MonitorLine(new Rectangle(10, 10, 75, 20), 2, MLINE_BASELINEOFFSET);
    MonitorLine analizeButton = new MonitorLine(new Rectangle(95, 10, 75, 20), 2, MLINE_BASELINEOFFSET, "Analize");
    MonitorLine tankSwitch = new MonitorLine(new Rectangle(180, 10, 50, 20), 2, MLINE_BASELINEOFFSET);
    MonitorLine ballisticShot = new MonitorLine(new Rectangle(300, 10, 50, 20), 2, MLINE_BASELINEOFFSET, "Ballistic");
    MonitorLine decPower = new MonitorLine(new Rectangle(460, 10, 10, 20), 2, MLINE_BASELINEOFFSET, "-");
    MonitorLine showPower = new MonitorLine(new Rectangle(470, 10, 30, 20), 2, MLINE_BASELINEOFFSET, "");
    MonitorLine incPower = new MonitorLine(new Rectangle(500, 10, 10, 20), 2, MLINE_BASELINEOFFSET, "+");
    MonitorLine decAngle = new MonitorLine(new Rectangle(520, 10, 10, 20), 2, MLINE_BASELINEOFFSET, "-");
    MonitorLine showAngle = new MonitorLine(new Rectangle(530, 10, 30, 20), 2, MLINE_BASELINEOFFSET, "");
    MonitorLine incAngle = new MonitorLine(new Rectangle(560, 10, 10, 20), 2, MLINE_BASELINEOFFSET, "+");
    // analizing
    private Image analImage = null;
    private ArrayList<SearchBlock> searchBlocks = new ArrayList<SearchBlock>();
    private int lastAnalTime = -1;
    private Tank activeTank;
    private ArrayList<int[]> shotBlocks = new ArrayList<int[]>();
    private int power = 100; //93; //72;
    private int angle = 154; //32; //45;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (analizeButton.inside(e.getX(), e.getY())) {
                    analizeImage();
                } else if (tankSwitch.inside(e.getX(), e.getY())) {
                    if (activeTank == greenTank) {
                        activeTank = redTank;
                        tankSwitch.setColor(Color.RED);
                        tankSwitch.setText("Red");
                    } else {
                        activeTank = greenTank;
                        tankSwitch.setColor(Color.GREEN);
                        tankSwitch.setText("Green");
                    }
                    repaint();
                } else if (ballisticShot.inside(e.getX(), e.getY())) {
                    simulateBallisticShot();
                } else if (decPower.inside(e.getX(), e.getY())) {
                    power--;
                    simulateDefaultShot();
                } else if (incPower.inside(e.getX(), e.getY())) {
                    power++;
                    simulateDefaultShot();
                } else if (decAngle.inside(e.getX(), e.getY())) {
                    angle--;
                    simulateDefaultShot();
                } else if (incAngle.inside(e.getX(), e.getY())) {
                    angle++;
                    simulateDefaultShot();
                } else {
                    moveTank(activeTank, e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveTank(activeTank, e.getX(), e.getY());
            }
        });

        // initialization
        tankSwitch.setText("Green");
        tankSwitch.setColor(Color.GREEN);
        activeTank = greenTank;

        File f = new File("images/img6.png");
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
        g.setColor(Color.MAGENTA);
        for (SearchBlock block : searchBlocks) {
            g.drawRect(block.getX(), block.getY(), block.getWidth(), block.getHeight());
        }

        // Draw monitor line 1
        posMonitor.setText(String.format("@ %d,%d", greenTank.getX(), greenTank.getY()));
        posMonitor.paintSprite(g);

        // Draw buttons
        analizeButton.paintSprite(g);
        tankSwitch.paintSprite(g);
        ballisticShot.paintSprite(g);
        incPower.paintSprite(g);
        showPower.setText(String.format("%d", power));
        showPower.paintSprite(g);
        decPower.paintSprite(g);
        incAngle.paintSprite(g);
        showAngle.setText(String.format("%d", angle));
        showAngle.paintSprite(g);
        decAngle.paintSprite(g);

        // Draw Tanks
        greenTank.paintSprite(g);
        redTank.paintSprite(g);

        // Draw tanks distance
        g.setColor(Color.YELLOW);
        g.drawLine(greenTank.getCenterX(), greenTank.getCenterY(), redTank.getCenterX(), greenTank.getCenterY());
        g.drawString(String.format("~ %d", Math.abs(greenTank.getCenterX() - redTank.getCenterX())), greenTank.getCenterX() + 10, greenTank.getCenterY() - 10);

        // Draw shotblocks
        g.setColor(Color.ORANGE);
        for (int[] shot : shotBlocks) {
            g.fillOval(shot[0], shot[1], shot[2], shot[3]);
        }
    }

    private void drawBackground(Graphics g) {
        g.drawImage(analImage, 0, 0, null);
    }

    private void simulateDefaultShot() {
        simulateBallisticShot();
    }

    private void simulateBallisticShot() {
        int s = redTank.getCenterX() - greenTank.getCenterX();
        int v = power;
        double g = 10;
        int a = angle;
        double px = 5; // px/paint
        int paints = Math.abs(s);

        int directionX = 1;
        int directionY = -1;
        if (a > 90) {
            a = 180 - a;
            directionX = -1;
            //directionY = 1;
        }

        // todo: refine shot position and density
        int shotSize = 2;
        shotBlocks = new ArrayList<int[]>();
        for (int p = 0; p <= paints + 1; p++) {
            double x = px * p;
            double y = x * tan(a) - g / (2 * p(v, 2) * p(cos(a), 2)) * p(x, 2);

            int coordX = (int) x * directionX + greenTank.getCenterX();
            int coordY = (int) y * directionY + greenTank.getCenterY();

            if (coordX < 0) coordX = coordX * -1;
            if (coordX > this.getWidth()) coordX = this.getWidth() - (coordX - this.getWidth());

            shotBlocks.add(new int[]{
                    coordX,
                    coordY,
                    shotSize, shotSize});
        }
        repaint();
    }

    private double p(double base, double power) {
        return Math.pow(base, power);
    }

    private double cos(double alpha) {
        return Math.cos(Math.PI / 180 * alpha);
    }

    private double sin(double alpha) {
        return Math.sin(Math.PI / 180 * alpha);
    }

    private double tan(double alpha) {
        return Math.tan(Math.PI / 180 * alpha);
    }
}