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

                SwingUtilities.invokeLater(new Win());

                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        TankAimGui.getInstance().repaint();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.warn("Thread sleep interrupted.", e);
                    }
                }
            }
        });

        Screener scr = Screener.getInstance();
        Thread screenerThread = new Thread(scr);

//        screenerThread.start();
        analizerThread.start();
        guiRefresherThread.start();
    }

    // todo add button to disable auto aim (reading realtime aim values)
    // todo add button to relocate window
    // todo improve tank search (main problem: drops)
    // todo use capture on-demand from Analyser, do not capture continously
    // todo Wind: wind=x; power=max; angle= angle +/- (x/2/10)
}
