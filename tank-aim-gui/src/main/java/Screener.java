import org.apache.log4j.Logger;
import org.sikuli.script.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by develrage on 2016.03.29.
 */
public class Screener extends SikulixFrame {
    final static Logger log = Logger.getLogger(Screener.class);
    private static Screener instance = null;
    private final float defSimilarity = 0.7f;
    private final Pattern indicator;
    private Rectangle region = null;

    private Screener() {
        File f = new File("images");
        ImagePath.add(f.getAbsolutePath());
        SikulixFrame.setImagePaths();
        indicator = new Pattern("indicator.png");
    }

    public synchronized static Screener getInstance() {
        if (instance == null) {
            instance = new Screener();
        }

        return instance;
    }

    public synchronized BufferedImage captureRegion() {
        BufferedImage capture = null;

        if (region != null) {
            try {
                capture = new Robot().createScreenCapture(region);
            } catch (AWTException e) {
                log.warn("Error capturing screen.");
            }
        } else {
            log.warn("No screen region defined.");
        }

        return capture;
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
            //ind.highlight(2);

            region = new Rectangle(ind.getX() + 4,
                    ind.getY() + 21 + 10,
                    800,
                    540);

            Region bigRegion = new Region(
                    (int) region.getX(),
                    (int) region.getY(),
                    region.width,
                    region.height,
                    s);

            //bigRegion.highlight(2);
            break;
        }
    }
}
