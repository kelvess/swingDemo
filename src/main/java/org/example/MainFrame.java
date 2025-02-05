package org.example;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.prefs.Preferences;

public class MainFrame {
    public static final int HISTORY_CAPACITY = 5;
    private static final Preferences historyPref = Preferences.userRoot().node("swingDemo/history");
    private static final Preferences settingsPref = Preferences.userRoot().node("swingDemo/settings");

    public void createAndShowGUI() {
        JFrame frame = new JFrame();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        init(frame);
    }

    private void init(JFrame frame) {
        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());
        Center center = new Center(historyPref);
        frame.add(center.getJSplitPane(), BorderLayout.CENTER);
        Top top = new Top(center, readHistory(), settingsPref);
        frame.add(top.getJPanel(), BorderLayout.NORTH);
        frame.setVisible(true);
        center.setDividerPos(false, settingsPref);
    }

    private String[] readHistory() {
        String[] history = new String[HISTORY_CAPACITY];
        for (int i = 0; i < HISTORY_CAPACITY; i++) {
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        return history;
    }
}
