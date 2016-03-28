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

    // Menu buttons
    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    MenuItem posMonitor = new MenuItem(new Rectangle(10, 10, 75, 20), 2, MLINE_BASELINEOFFSET);
    MenuItem analizeButton = new MenuItem(new Rectangle(95, 10, 75, 20), 2, MLINE_BASELINEOFFSET, "Analize");
    MenuItem tankSwitch = new MenuItem(new Rectangle(180, 10, 50, 20), 2, MLINE_BASELINEOFFSET);
    MenuItem getFieldButton = new MenuItem(new Rectangle(240, 10, 75, 20), 2, MLINE_BASELINEOFFSET, "Get Field");
    MenuItem decPower = new MenuItem(new Rectangle(460, 10, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showPower = new MenuItem(new Rectangle(475, 10, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incPower = new MenuItem(new Rectangle(510, 10, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem decAngle = new MenuItem(new Rectangle(535, 10, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showAngle = new MenuItem(new Rectangle(550, 10, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incAngle = new MenuItem(new Rectangle(585, 10, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem ballisticShot = new MenuItem(new Rectangle(610, 10, 50, 20), 2, MLINE_BASELINEOFFSET, "Ballistic");

    // analizing
    private Image analImage = null;
    private ArrayList<SearchBlock> searchBlocks = new ArrayList<SearchBlock>();
    private int lastAnalTime = -1;
    private Tank activeTank;
    private ArrayList<int[]> shotBlocks = new ArrayList<int[]>();
    private int power = 72; //93; //72;
    private int angle = 45; //32; //45;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        // initialization
        menuItems.add(posMonitor);
        menuItems.add(analizeButton);
        menuItems.add(tankSwitch);
        menuItems.add(ballisticShot);
        menuItems.add(decPower);
        menuItems.add(showPower);
        menuItems.add(incPower);
        menuItems.add(decAngle);
        menuItems.add(showAngle);
        menuItems.add(incAngle);
        menuItems.add(getFieldButton);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getY() < INTERACT_MARGIN_TOP) {
                    if (analizeButton.inside(e)) {
                        analizeImage();
                    } else if (tankSwitch.inside(e)) {
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
                    } else if (ballisticShot.inside(e)) {
                        simulateDefaultShot();
                    } else if (decPower.inside(e)) {
                        if (power <= 0) {
                            power = 100;
                        } else {
                            power--;
                        }
                        simulateDefaultShot();
                    } else if (incPower.inside(e)) {
                        if (power >= 100) {
                            power = 0;
                        } else {
                            power++;
                        }
                        simulateDefaultShot();
                    } else if (decAngle.inside(e)) {
                        if (angle <= 0) {
                            angle = 359;
                        } else {
                            angle--;
                        }
                        simulateDefaultShot();
                    } else if (incAngle.inside(e)) {
                        if (angle >= 359) {
                            angle = 0;
                        } else {
                            angle++;
                        }
                        simulateDefaultShot();
                    }
                } else {
                    moveTank(activeTank, e.getX(), e.getY());
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.getY() > INTERACT_MARGIN_TOP) {
                    moveTank(activeTank, e.getX(), e.getY());
                }
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

            simulateDefaultShot();
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
        showPower.setText(String.format("P:%d", power));
        showAngle.setText(String.format("A:%d", angle));
        for (MenuItem item : menuItems) {
            item.paintSprite(g);
        }

        // Draw Tanks
        greenTank.paintSprite(g);
        redTank.paintSprite(g);

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
        Analizer analizer = new Analizer(analImage);
        shotBlocks = analizer.simulateBallisticShot(angle, power, greenTank.getCenterX(), greenTank.getCenterY());
        repaint();
    }
}