import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Runner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Win());
    }

    // todo move and handle block list inside analizer, return only relevant block list to the gui
    // todo find tanks in filed blocks
    // todo add paint-time monitor
    // todo add fixed tanks
    // todo try physics
    // todo later reimplement partial repaint, if performance requires
}
