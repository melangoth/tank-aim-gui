import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Runner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Win());
    }

    // todo move and handle block list inside analizer, return only relevant block list to the gui
    // todo find tanks in field blocks
    // todo add support for manual tank location
    // todo implement realtime screen capturing
    // todo monitor tanks relative to their last known place
    // todo try physics
    // todo later reimplement partial repaint, if performance requires
}
