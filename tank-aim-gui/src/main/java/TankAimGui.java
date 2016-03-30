import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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

    // Menu buttons
    // todo rewrite to be able to stack menuitems, make them relative to each other
    ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    MenuItem tankSwitch = new MenuItem(new Rectangle(180, MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET);
    MenuItem changeImageButton = new MenuItem(new Rectangle(240, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Chng Img");
    MenuItem decPower = new MenuItem(new Rectangle(460, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showPower = new MenuItem(new Rectangle(475, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incPower = new MenuItem(new Rectangle(510, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
    MenuItem decAngle = new MenuItem(new Rectangle(535, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
    MenuItem showAngle = new MenuItem(new Rectangle(550, MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
    MenuItem incAngle = new MenuItem(new Rectangle(585, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");

    // GUI
    private Tank activeTank;

    private TankAimGui() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        // initialization
        menuItems.add(tankSwitch);
        menuItems.add(decPower);
        menuItems.add(showPower);
        menuItems.add(incPower);
        menuItems.add(decAngle);
        menuItems.add(showAngle);
        menuItems.add(incAngle);
        menuItems.add(changeImageButton);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Analyser analyser = Analyser.getInstance();
                if (e.getY() < INTERACT_MARGIN_TOP) {
                    if (tankSwitch.inside(e)) {
                        if (activeTank == analyser.getP1Tank()) {
                            activeTank = analyser.getP2Tank();
                        } else {
                            activeTank = analyser.getP1Tank();
                        }
                        tankSwitch.setColor(activeTank.getColor());
                        tankSwitch.setText(activeTank.getName());
                    } else if (decPower.inside(e)) {
                        int power = analyser.getPower();
                        if (power <= 0) {
                            power = 100;
                        } else {
                            power--;
                        }
                        analyser.setPower(power);
                    } else if (incPower.inside(e)) {
                        int power = analyser.getPower();
                        if (power >= 100) {
                            power = 0;
                        } else {
                            power++;
                        }
                        analyser.setPower(power);
                    } else if (decAngle.inside(e)) {
                        int angle = analyser.getAngle();
                        if (angle <= 0) {
                            angle = 359;
                        } else {
                            angle--;
                        }
                        analyser.setAngle(angle);
                    } else if (incAngle.inside(e)) {
                        int angle = analyser.getAngle();
                        if (angle >= 359) {
                            angle = 0;
                        } else {
                            angle++;
                        }
                        analyser.setAngle(angle);
                    } else if (changeImageButton.inside(e)) {
                        analyser.loadImagePool(true);
                    }
                } else {
                    //moveTank(activeTank, e.getX(), e.getY());
                    log.warn("Review this method: moveTank()");
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.getY() > INTERACT_MARGIN_TOP) {
                    //moveTank(activeTank, e.getX(), e.getY());
                    log.warn("Review this method: moveTank()");
                }
            }
        });

        // initialization
        activeTank = Analyser.getInstance().getP1Tank();
        tankSwitch.setColor(activeTank.getColor());
        tankSwitch.setText(activeTank.getName());
    }

    public static TankAimGui getInstance() {
        if (instance == null) {
            instance = new TankAimGui();
        }

        return instance;
    }

    // todo revrite to be compatible with separated Analyser
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
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 540);
    }

    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        drawBackground(g);

        // Draw tracer blocks
        drawTracerBlocks(g);

        // Draw buttons
        drawButtons(g);

        // Draw fieldLine
        drawFieldLineBlocks(g);

        // Draw Tanks
        Analyser.getInstance().getP1Tank().paintSprite(g);
        Analyser.getInstance().getP2Tank().paintSprite(g);

        // Draw trajectory
        drawTrajectory(g);
    }

    private void drawTracerBlocks(Graphics g) {
        g.setColor(Color.BLUE);
        for (int[] t : Analyser.getInstance().getTracerBlocks()) {
            g.drawRect(t[0], t[1], t[2], t[3]);
        }
    }

    private void drawTrajectory(Graphics g) {
        g.setColor(activeTank.getColor());
        for (int[] t : Analyser.getInstance().getTrajectoryBlocks()) {
            g.fillOval(t[0], t[1], t[2], t[3]);
        }
    }

    private void drawButtons(Graphics g) {
        showPower.setText(String.format("P:%d", Analyser.getInstance().getPower()));
        showAngle.setText(String.format("A:%d", Analyser.getInstance().getAngle()));
        for (MenuItem item : menuItems) {
            item.paintSprite(g);
        }
    }

    private void drawFieldLineBlocks(Graphics g) {
        g.setColor(Color.MAGENTA);
        for (int[] f : Analyser.getInstance().getFieldLineBlocks()) {
            g.drawRect(f[0], f[1], f[2], f[3]);
        }
    }

    private void drawBackground(Graphics g) {
        log.trace("drawBackground(Graphics g)");
        Image backgroundImage = Analyser.getInstance().getImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        } else {
            log.warn("Background image is null");
            setBackground(Color.DARK_GRAY);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), INTERACT_MARGIN_TOP);
        }
    }
}