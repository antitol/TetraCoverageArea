import gui.MainFrame;

import javax.swing.*;

/**
 * Created by anatoliy on 16.01.17.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
