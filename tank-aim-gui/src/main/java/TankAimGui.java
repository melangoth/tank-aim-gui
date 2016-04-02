import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by develrage
 */
class TankAimGui extends JPanel {
    final static Logger log = Logger.getLogger(TankAimGui.class);
    // Global
    private static final int MLINE_BASELINEOFFSET = 3;
    private static final int MLINE_FIRSTLINE = 5;
    private static final int INTERACT_MARGIN_TOP = 45;
    private static final int INTERACT_MARGIN_BOTTOM = 5;
    private static final int INTERACT_MARGIN_LEFT = 5;
    private static final int INTERACT_MARGIN_RIGHT = 5;
    private static TankAimGui instance = null;

    // Menu buttons
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    MenuItem tankSwitch;
    MenuItem changeImageButton;
    MenuItem decPower;
    MenuItem showPower;
    MenuItem incPower;
    MenuItem decAngle;
    MenuItem showAngle;
    MenuItem incAngle;

    private TankAimGui() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        // initialization
        changeImageButton = new MenuItem(new Rectangle(130, MLINE_FIRSTLINE, 75, 20), 2, MLINE_BASELINEOFFSET, "Chng Img");
        menuItems.add(changeImageButton);
        tankSwitch = new MenuItem(new Rectangle(rightOf(changeImageButton), MLINE_FIRSTLINE, 50, 20), 2, MLINE_BASELINEOFFSET);
        menuItems.add(tankSwitch);
        decPower = new MenuItem(new Rectangle(500, MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
        menuItems.add(decPower);
        showPower = new MenuItem(new Rectangle(rightOf(decPower, 0), MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
        menuItems.add(showPower);
        incPower = new MenuItem(new Rectangle(rightOf(showPower, 0), MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
        menuItems.add(incPower);
        decAngle = new MenuItem(new Rectangle(rightOf(incPower), MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " -");
        menuItems.add(decAngle);
        showAngle = new MenuItem(new Rectangle(rightOf(decAngle, 0), MLINE_FIRSTLINE, 35, 20), 2, MLINE_BASELINEOFFSET, "");
        menuItems.add(showAngle);
        incAngle = new MenuItem(new Rectangle(rightOf(showAngle, 0), MLINE_FIRSTLINE, 15, 20), 2, MLINE_BASELINEOFFSET, " +");
        menuItems.add(incAngle);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // todo: remove analyser def., extract all code to separate methods
                Analyser analyser = Analyser.getInstance();
                if (e.getY() < INTERACT_MARGIN_TOP) {
                    if (tankSwitch.inside(e)) {
                        switchTank();
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
                    moveTank(e);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.getY() > INTERACT_MARGIN_TOP) {
                    moveTank(e);
                }
            }
        });

        // initialization
        Tank activeTank = Analyser.getInstance().getActiveTank();
        tankSwitch.setColor(activeTank.getColor());
        tankSwitch.setText(activeTank.getName());
    }

    public static TankAimGui getInstance() {
        if (instance == null) {
            instance = new TankAimGui();
        }

        return instance;
    }

    private int rightOf(MenuItem relative) {
        return rightOf(relative, 10);
    }

    private int rightOf(MenuItem relative, int distance) {
        return relative.getX() + relative.getWidth() + distance;
    }

    private void switchTank() {
        Analyser analyser = Analyser.getInstance();
        analyser.switchActiveTank();
        tankSwitch.setColor(analyser.getActiveTank().getColor());
        tankSwitch.setText(analyser.getActiveTank().getName());
    }

    private void moveTank(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        Analyser analyser = Analyser.getInstance();
        Tank tank = analyser.getActiveTank();
        log.debug(String.format("Active tank is: %s", tank.getName()));

        // repaint only if moved, and if secondary tank is active
        if (tank.getName().equals("P2Tank") &&
                ((tank.getCenterX() != x) || (tank.getCenterY() != y))) {
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

        // Draw aim captures
        drawAimCaptures(g);

        // Draw tracer blocks
//        drawTracerBlocks(g);

        // Draw buttons
        drawButtons(g);

        // Draw fieldLine
        drawFieldLineBlocks(g);

        // Draw Tanks
        drawTanks(g);
    }

    private void drawAimCaptures(Graphics g) {
        BufferedImage img = Screener.getInstance().getAngleCaptured();
        if (img != null) {
            g.drawImage(img, showAngle.getX(), showAngle.getY() + showAngle.getHeight() + 2, null, null);
        }

        img = Screener.getInstance().getPowerCaptured();
        if (img != null) {
            g.drawImage(img, showPower.getX(), showPower.getY() + showPower.getHeight() + 2, null, null);
        }
    }

    private void drawTracerBlocks(Graphics g) {
        g.setColor(Color.BLUE);
        for (int[] t : Analyser.getInstance().getTracerBlocks()) {
            g.drawRect(t[0], t[1], t[2], t[3]);
        }
    }

    private void drawTanks(Graphics g) {
        Tank[] tanks = new Tank[]{Analyser.getInstance().getP1Tank(), Analyser.getInstance().getP2Tank()};

        for (Tank tank : tanks) {
            g.setColor(tank.getColor());
            tank.paintSprite(g);
            if (Analyser.getInstance().getActiveTank() == tank) {
                for (int[] t : tank.getTrajectoryBlocks()) {
                    g.fillOval(t[0], t[1], t[2], t[3]);
                }
            }
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