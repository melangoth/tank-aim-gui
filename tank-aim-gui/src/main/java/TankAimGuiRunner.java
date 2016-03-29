import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class TankAimGuiRunner {

    public static void main(String[] args) {
        Analizer analizer = Analizer.getInstance();
        Thread analizerThread = new Thread(analizer);
        analizerThread.start();

        //SwingUtilities.invokeLater(new Win());

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
