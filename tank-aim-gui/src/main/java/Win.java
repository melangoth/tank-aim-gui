import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by develrage
 */
public class Win implements Runnable {
    final static Logger log = Logger.getLogger(Win.class);

    public void createAndShowGUI() {
        log.info("Created GUI on EDT? " +
                SwingUtilities.isEventDispatchThread());
        JFrame frame = new JFrame("Tank Aim GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(200, 50);
        frame.add(TankAimGui.getInstance());
        frame.pack();
        frame.setVisible(true);
    }

    public void run() {
        createAndShowGUI();
    }
}
