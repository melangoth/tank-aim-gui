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
    private Rectangle region = null;
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
        log.debug("Capturing region.");

        if (region != null) {
            try {
                Rectangle region;
                synchronized (regionLock) {
                    region = this.region;
                }
                synchronized (captureLock) {
                    imageCaptured = new Robot().createScreenCapture(region);
                }
            } catch (AWTException e) {
                log.warn("Error capturing screen.");
            }
        } else {
            log.warn("No screen region defined.");
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
                region = new Rectangle(ind.getX() + 4,
                        ind.getY() + 21 + 6,
                        800,
                        540);
            }
            break;
        }
    }
}
