import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by develrage
 */
public class TankAimGuiRunner {
    final static Logger log = Logger.getLogger(TankAimGuiRunner.class);

    public static void main(String[] args) {
        Analyser analizer = Analyser.getInstance();
        Thread analizerThread = new Thread(analizer);

        Thread guiRefresherThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                        TankAimGui.getInstance().repaint();
                    } catch (InterruptedException e) {
                        log.warn("Thread sleep interrupted.", e);
                    }
                }
            }
        });

        Screener scr = Screener.getInstance();
        Thread screenerThread = new Thread(scr);

        analizerThread.start();
        SwingUtilities.invokeLater(new Win());
        guiRefresherThread.start();
        screenerThread.start();
    }

    // todo make greenTank auto-search, make red-tank manual-search, switch also trajectory
    // todo implement realtime screen capturing
    // todo Wind: wind=x; power=max; angle= angle +/- (x/2/10)
}
