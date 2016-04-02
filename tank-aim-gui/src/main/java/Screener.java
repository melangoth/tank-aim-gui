import org.apache.log4j.Logger;
import org.sikuli.script.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by develrage
 */
public class Screener extends SikulixFrame implements Runnable {
    final static Logger log = Logger.getLogger(Screener.class);
    private static Screener instance = null;
    private final Pattern indicator;
    private final Object captureLock = new Object();
    private final Object regionLock = new Object();
    private Rectangle rField = null;
    private Rectangle rPower = null;
    private Rectangle rAngle = null;
    private BufferedImage fieldCaptured = null;
    private BufferedImage angleCaptured = null;
    private BufferedImage powerCaptured = null;

    private Screener() {
        File f = new File("images");
        ImagePath.add(f.getAbsolutePath());
        SikulixFrame.setImagePaths();
        indicator = new Pattern("indicator.png");
    }

    public static Screener getInstance() {
        if (instance == null) {
            instance = new Screener();
        }

        return instance;
    }

    public void run() {
        log.debug("run()");
        findRegion();

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                log.trace("Screener hearthbeat.");

                synchronized (captureLock) {
                    fieldCaptured = captureRegion(rField);
                }
                synchronized (captureLock) {
                    angleCaptured = captureRegion(rAngle);
                }
                synchronized (captureLock) {
                    powerCaptured = captureRegion(rPower);
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    public BufferedImage getFieldCaptured() {
        synchronized (captureLock) {
            return fieldCaptured;
        }
    }

    public BufferedImage getAngleCaptured() {
        synchronized (captureLock) {
            return angleCaptured;
        }
    }

    public BufferedImage getPowerCaptured() {
        synchronized (captureLock) {
            return powerCaptured;
        }
    }

    public synchronized BufferedImage captureRegion(Rectangle rect) {
        log.trace("Capturing region.");
        BufferedImage img = null;

        try {
            if (rect != null) {
                img = new Robot().createScreenCapture(rect);
            } else {
                log.warn("No screen region defined.");
            }
        } catch (AWTException e) {
            log.error("Error capturing screen.");
        }
        return img;
    }

    public synchronized void findRegion() {
        for (int s = Screen.getNumberScreens() - 1; s >= 0; s--) {
            log.info("Searching app on Screen#" + s);
            Region screen = new Region((new Screen(s)).getBounds());

            float defSimilarity = 0.7f;
            Match ind = waitMatch(screen, indicator.similar(defSimilarity), 5);

            if (ind == null) {
                log.info("Title not found on Screen " + s);
                continue;
            }

            log.info("Indicator found.");

            synchronized (regionLock) {
                rField = new Rectangle(ind.getX() + 4,
                        ind.getY() + 21 + 6,
                        800,
                        540);
                rPower = new Rectangle(rField.x + 303, rField.y + rField.height + 10, 28, 16);
                rAngle = new Rectangle(rPower.x, rPower.y + 27, rPower.width, rPower.height);
            }

//            ind.highlight(1);
//            new Region(rField).highlight(1);
//            new Region(rPower).highlight(1);
//            new Region(rAngle).highlight(3);

            break;
        }
    }
}
