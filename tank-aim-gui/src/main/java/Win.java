import javax.swing.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Win implements Runnable {

    public void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MyPanel());
        f.pack();
        f.setVisible(true);
    }

    public void run() {
        createAndShowGUI();
    }
}
