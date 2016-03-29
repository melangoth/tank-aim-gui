import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by develrage
 */
public class Win implements Runnable {
    final static Logger log = Logger.getLogger(Win.class);
    private JFrame frame;

    public void createAndShowGUI() {
        log.info("Created GUI on EDT? " +
                SwingUtilities.isEventDispatchThread());
        frame = new JFrame("Tank Aim GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(TankAimGui.getInstance());
        frame.pack();
        frame.setVisible(true);
    }

    public void run() {
        createAndShowGUI();
    }
}
