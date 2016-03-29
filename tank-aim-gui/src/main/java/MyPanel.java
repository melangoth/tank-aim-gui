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
    // Global
    private static final int MLINE_BASELINEOFFSET = 3;
    private static final int MLINE_FIRSTLINE = 10;
    private static final int INTERACT_MARGIN_TOP = 45;
    private static final int INTERACT_MARGIN_BOTTOM = 5;
    private static final int INTERACT_MARGIN_LEFT = 5;
    private static final int INTERACT_MARGIN_RIGHT = 5;
    // Sprites
    Tank greenTank = new Tank(Color.GREEN, 77, 131);
    Tank redTank = new Tank(Color.RED, 660, 222);
    // Fields
    String[] fields = new String[]{"images/img6.png", "images/img7.png", "images/img8.png"};
    int fieldPointer = 1; // field background
    // Menu buttons
    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    MenuItem screenShotButton = new MenuItem(new Rectangle(10, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "ScrShot");
    MenuItem analizeButton = new MenuItem(new Rectangle(95, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Analize");
    MenuItem tankSwitch = new MenuItem(new Rectangle(180, MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET);
    MenuItem getFieldButton = new MenuItem(new Rectangle(240, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Get Field");
    MenuItem decPower = new MenuItem(new Rectangle(460, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showPower = new MenuItem(new Rectangle(475, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incPower = new MenuItem(new Rectangle(510, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem decAngle = new MenuItem(new Rectangle(535, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showAngle = new MenuItem(new Rectangle(550, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incAngle = new MenuItem(new Rectangle(585, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem ballisticShot = new MenuItem(new Rectangle(610, MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET, "Ballistic");

    // analizing
    private Image analImage = null;
    private int lastAnalTime = -1;
    private Tank activeTank;
    private ArrayList<int[]> shotBlocks = new ArrayList<int[]>();
    private ArrayList<int[]> tankBlocks = new ArrayList<int[]>();
    private int power = 100; //93; //72;
    private int angle = 112; //32; //45;
    private ArrayList<int[]> fieldLine = new ArrayList<int[]>();

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        // initialization
        menuItems.add(screenShotButton);
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
                    if (screenShotButton.inside(e)) {
                        captureScreen();
                    } else if (analizeButton.inside(e)) {
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
                    } else if (getFieldButton.inside(e)) {
                        changeField();
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

        loadImage(fields[fieldPointer]);
    }

    private void captureScreen() {
        // sikuli call
    }

    private void loadImage(String path) {
        File f = new File(path);
        try {
            analImage = ImageIO.read(f);
            analizeImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeField() {
        fieldPointer++;
        if (fieldPointer >= fields.length) fieldPointer = 0;
        loadImage(fields[fieldPointer]);
        repaint();
    }

    private void analizeImage() {
        long start = (new Date()).getTime();

        Analizer anal = new Analizer(analImage);
        fieldLine = anal.searchFieldLine();
        tankBlocks = anal.searchTank();
        greenTank.setCenter(tankBlocks.get(0)[0], tankBlocks.get(0)[1]);
        shotBlocks = anal.simulateBallisticShot(angle, power, greenTank.getCenterX(), greenTank.getCenterY());

        long end = (new Date()).getTime();
        end = end - start;
        analizeButton.setText(String.format("Analize (%d)", end));
        System.out.println(String.format("Panel: Analization took %f seconds.", (double) end / 1000));
        repaint();
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
        super.paintComponent(g);

        // Draw background
        drawBackground(g);

        // Draw fieldLine
        g.setColor(Color.MAGENTA);
        for (int[] fl : fieldLine) {
            g.drawRect(fl[0], fl[1], fl[2], fl[3]);
        }

        // Draw buttons
        screenShotButton.paintSprite(g);
        showPower.setText(String.format("P:%d", power));
        showAngle.setText(String.format("A:%d", angle));
        for (MenuItem item : menuItems) {
            item.paintSprite(g);
        }

        // Draw Tanks
        greenTank.paintSprite(g);
        //redTank.paintSprite(g);
        for (int[] t : tankBlocks) {
            g.setColor(Color.ORANGE);
            g.fillRect(t[0] - t[2] / 2, t[1] - t[3] / 2, t[2], t[3]);
        }

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
        analizer.getTankColor(greenTank);
        repaint();
    }
}