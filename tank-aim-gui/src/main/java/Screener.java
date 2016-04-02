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
    @SuppressWarnings("FieldCanBeLocal")
    private final float defSimilarity = 0.7f;
    private final Pattern indicator;
    private final Object captureLock = new Object();
    private final Object regionLock = new Object();
    private Rectangle rField = null;
    private Rectangle rPower = null;
    private Rectangle rAngle = null;
    private BufferedImage imageCaptured = null;

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
                captureRegion();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    public synchronized BufferedImage getImageCaptured() {
        synchronized (captureLock) {
            return imageCaptured;
        }
    }

    public synchronized void captureRegion() {
        log.debug("Capturing regions.");

        try {
            synchronized (regionLock) {
                for (Rectangle r : new Rectangle[]{rField, rAngle, rPower})
                    if (r != null) {
                        synchronized (captureLock) {
                            imageCaptured = new Robot().createScreenCapture(r);
                        }
                    } else {
                        log.warn("No screen region defined.");
                    }
            }
        } catch (AWTException e) {
            log.error("Error capturing screen.");
        }

    }

    public synchronized void findRegion() {
        for (int s = Screen.getNumberScreens() - 1; s >= 0; s--) {
            log.info("Searching app on Screen#" + s);
            Region screen = new Region((new Screen(s)).getBounds());

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
                rPower = new Rectangle(rField.x + 303, rField.y + rField.height + 8, 30, 18);
                rAngle = new Rectangle(rPower.x, rPower.y + 26, rPower.width, rPower.height);
            }

//            ind.highlight(1);
//            new Region(rField).highlight(1);
//            new Region(rPower).highlight(1);
//            new Region(rAngle).highlight(3);

            break;
        }
    }
}
