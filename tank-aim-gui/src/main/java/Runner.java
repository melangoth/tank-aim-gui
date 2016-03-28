import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Runner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Win());
    }

    // todo inside analizer, return only relevant block list to the gui
    // todo find tanks in field blocks
    // todo find exact field line inside field blocks
    // todo implement realtime screen capturing
    // todo monitor tanks relative to their last known place
    // todo later reimplement partial repaint, if performance requires
    // todo Wind: wind=x; power=max; angle= angle +/- (x/2/10)
}
