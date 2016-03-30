import org.apache.log4j.Logger;
import org.sikuli.script.*;

import java.util.List;
import java.util.Properties;

/**
 * Created by develrage
 */
public class SikulixFrame {
    final static Logger log = Logger.getLogger(SikulixFrame.class);
    protected Properties props = new Properties();

    public static void setImagePaths() {
        log.info("Logging image paths...");
        log.info(ImagePath.getBundlePath());
        List<ImagePath.PathEntry> paths = ImagePath.getPaths();
        for (ImagePath.PathEntry pe : paths) {
            log.info(pe.toString());
        }

    }

    protected void sleepms(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.warn("Wait interrupted.");
            e.printStackTrace();
        }
    }

    protected void sleep(int seconds) {
        try {
            log.info("sleeping (" + seconds + ") ...");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            log.warn("Wait interrupted.");
            e.printStackTrace();
        }
    }

    protected Location getRelative(Location relativeTo, Location position) {
        return new Location(relativeTo.x + position.x, relativeTo.y + position.y);
    }

    protected Match getMatch(Region inRegion, Pattern pattern) {
        Match m = null;

        try {
            m = inRegion.find(pattern);
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            log.warn("Can't find pattern in region.");
        }

        return m;
    }

    protected Match waitMatch(Region inRegion, Pattern pattern, int seconds) {
        Match m = null;

        try {
            m = inRegion.wait(pattern, seconds);
        } catch (FindFailed findFailed) {
            //findFailed.printStackTrace();
            log.warn("Can't wait pattern in region.");
        }

        return m;
    }
}
