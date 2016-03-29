import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by develrage
 */
public class TankAimGuiRunner {
    final static Logger log = Logger.getLogger(TankAimGuiRunner.class);

    public static void main(String[] args) {
        Analizer analizer = Analizer.getInstance();
        Thread analizerThread = new Thread(analizer);
        analizerThread.start();

        SwingUtilities.invokeLater(new Win());

        Thread guiRefresherThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        TankAimGui.getInstance().repaint();
                    } catch (InterruptedException e) {
                        log.warn("Thread sleep interrupted.", e);
                    }
                }
            }
        });
        guiRefresherThread.start();

        /*Screener scr = Screener.getInstance();
        scr.findRegion();
        scr.captureRegion();*/
    }

    // todo implement realtime screen capturing
    // todo separate screener and analizer to dedicated worker thread
    // todo monitor tanks relative to their last known place
    // todo later reimplement partial repaint, if performance requires
    // todo Wind: wind=x; power=max; angle= angle +/- (x/2/10)
}
