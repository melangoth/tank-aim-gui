import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by develrage
 */
class TankAimGui extends JPanel {
    final static Logger log = Logger.getLogger(TankAimGui.class);
    // Global
    private static final int MLINE_BASELINEOFFSET = 3;
    private static final int MLINE_FIRSTLINE = 10;
    private static final int INTERACT_MARGIN_TOP = 45;
    private static final int INTERACT_MARGIN_BOTTOM = 5;
    private static final int INTERACT_MARGIN_LEFT = 5;
    private static final int INTERACT_MARGIN_RIGHT = 5;
    private static TankAimGui instance = null;
    // Sprites
    Tank greenTank = new Tank(Color.GREEN, 77, 131);
    Tank redTank = new Tank(Color.RED, 660, 222);
    // Menu buttons
    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    MenuItem screenShotButton = new MenuItem(new Rectangle(10, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "ScrShot");
    MenuItem analyseButton = new MenuItem(new Rectangle(95, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Analyse");
    MenuItem tankSwitch = new MenuItem(new Rectangle(180, MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET);
    MenuItem changeImageButton = new MenuItem(new Rectangle(240, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Chng Img");
    MenuItem decPower = new MenuItem(new Rectangle(460, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showPower = new MenuItem(new Rectangle(475, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incPower = new MenuItem(new Rectangle(510, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem decAngle = new MenuItem(new Rectangle(535, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showAngle = new MenuItem(new Rectangle(550, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incAngle = new MenuItem(new Rectangle(585, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem ballisticShot = new MenuItem(new Rectangle(610, MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET, "Ballistic");

    // GUI
    private Image backgroundImage = null;
    private int lastAnalysationTime = -1;
    private Tank activeTank;
    private ArrayList<int[]> shotBlocks = new ArrayList<int[]>();
    private ArrayList<int[]> tankBlocks = new ArrayList<int[]>();
    private int power = 100; //93; //72;
    private int angle = 112; //32; //45;
    private ArrayList<int[]> fieldLine = new ArrayList<int[]>();

    private TankAimGui() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        // initialization
        menuItems.add(screenShotButton);
        menuItems.add(analyseButton);
        menuItems.add(tankSwitch);
        menuItems.add(ballisticShot);
        menuItems.add(decPower);
        menuItems.add(showPower);
        menuItems.add(incPower);
        menuItems.add(decAngle);
        menuItems.add(showAngle);
        menuItems.add(incAngle);
        menuItems.add(changeImageButton);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getY() < INTERACT_MARGIN_TOP) {
                    if (screenShotButton.inside(e)) {
                        captureScreen();
                    } else if (analyseButton.inside(e)) {
                        analyseImage();
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
                    } else if (changeImageButton.inside(e)) {
                        Analyser.getInstance().loadImagePool(true);
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
    }

    public static TankAimGui getInstance() {
        if (instance == null) {
            instance = new TankAimGui();
        }

        return instance;
    }

    private void captureScreen() {
        log.trace("captureScreen()");
        // sikuli call
        Thread th = new Thread(new Runnable() {
            public void run() {
                Screener.getInstance().findRegion();
                Screener.getInstance().captureRegion();
            }
        });

        th.start();
    }

    private void analyseImage() {
        long start = (new Date()).getTime();

        // todo replace with getting Analyser autogenerated data
        Analyser analyser = Analyser.getInstance();
        analyser.loadImage(backgroundImage);
        fieldLine = analyser.searchFieldLine();
        tankBlocks = analyser.searchTank();
        greenTank.setCenter(tankBlocks.get(0)[0], tankBlocks.get(0)[1]);
        shotBlocks = analyser.simulateBallisticShot(angle, power, greenTank.getCenterX(), greenTank.getCenterY());

        long end = (new Date()).getTime();
        end = end - start;
        analyseButton.setText(String.format("Analyse (%d)", end));
        System.out.println(String.format("Panel: Analysation took %f seconds.", (double) end / 1000));
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
        log.trace("drawBackground(Graphics g)");
        backgroundImage = Analyser.getInstance().getImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        } else log.warn("Background image is null");
    }

    private void simulateDefaultShot() {
        Analyser analyser = Analyser.getInstance();
        analyser.loadImage(backgroundImage);
        shotBlocks = analyser.simulateBallisticShot(angle, power, greenTank.getCenterX(), greenTank.getCenterY());
        analyser.getTankColor(greenTank);
        repaint();
    }
}