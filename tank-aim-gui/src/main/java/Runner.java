import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Runner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Win());

        /*Screener scr = Screener.getInstance();
        scr.findRegion();
        scr.captureRegion();*/
    }

    // todo implement realtime screen capturing
    // todo monitor tanks relative to their last known place
    // todo later reimplement partial repaint, if performance requires
    // todo Wind: wind=x; power=max; angle= angle +/- (x/2/10)
}
