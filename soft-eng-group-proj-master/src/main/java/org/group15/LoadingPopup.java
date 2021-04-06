package org.group15;

import javax.swing.*;
import java.awt.*;

public class LoadingPopup extends JFrame {

    public LoadingPopup(int X, int Y, Dimension z) {

        JPanel CPane = new JPanel(new BorderLayout());
        JPanel pane2 = new JPanel(new BorderLayout());
        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        setContentPane(CPane);
        pane2.add(loadingLabel, BorderLayout.CENTER);
        add(pane2, BorderLayout.CENTER);

        setUndecorated(true);
        setOpacity(0.5f);
        setAlwaysOnTop(true);
        setFocusable(true);
        setSize(z);
        setLocation(X,Y);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }
}
