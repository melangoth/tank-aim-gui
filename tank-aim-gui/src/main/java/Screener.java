import org.apache.log4j.Logger;
import org.sikuli.script.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by develrage
 */
public class Screener extends SikulixFrame implements Runnable {
    final static Logger log = Logger.getLogger(Screener.class);
    private static Screener instance = null;
    private final Pattern indicator;
    private final Object captureLock = new Object();
    private final Object regionLock = new Object();
    private final Object upsLock = new Object();
    private Rectangle rField = null;
    private Rectangle rPower = null;
    private Rectangle rAngle = null;
    private BufferedImage fieldCaptured = null;
    private BufferedImage angleCaptured = null;
    private BufferedImage powerCaptured = null;
    // UPS monitor
    private ArrayList<Long> workMillis = new ArrayList<>();
    private double ups;

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

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                log.trace("Screener hearthbeat.");
                findRegion();

                // Refresh UPS monitor
                workMillis.add(new Date().getTime());
                if (workMillis.size() > 10) {
                    calcUPSAvg();
                }

                Thread.sleep(250);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    public synchronized void captureScreenRegions() {
        synchronized (captureLock) {
            fieldCaptured = captureRegion(rField);
        }
        synchronized (captureLock) {
            angleCaptured = captureRegion(rAngle);
        }
        synchronized (captureLock) {
            powerCaptured = captureRegion(rPower);
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

    private BufferedImage captureRegion(Rectangle rect) {
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

    private void findRegion() {
        for (int s = Screen.getNumberScreens() - 1; s >= 0; s--) {
            log.trace("Searching app on Screen#" + s);
            Region screen = new Region((new Screen(s)).getBounds());

            float defSimilarity = 0.7f;
            Match ind = waitMatch(screen, indicator.similar(defSimilarity), 5);

            if (ind == null) {
                log.error("Title not found on Screen " + s);
                continue;
            }

            log.debug("Indicator found.");

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

    private void calcUPSAvg() {
        double sum = 0;
        for (int i = workMillis.size() - 1; i >= 1; i--) {
            sum += workMillis.get(i) - workMillis.get(i - 1);
        }
        sum /= (workMillis.size() - 1);
        synchronized (upsLock) {
            ups = 1000 / sum;
//            log.info(String.format("Calculating UPS (%d): %f", workMillis.size(), ups));
        }
        workMillis.remove(0);
    }

    public double getUPS() {
        synchronized (upsLock) {
            return ups;
        }
    }
}
