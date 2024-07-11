package org.example;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class MainFrame {

    private static final  Preferences historyPref=Preferences.userRoot().node("swingDemo/history");
    private static final Preferences settingsPref=Preferences.userRoot().node("swingDemo/settings");
    static final Font arialBold = new Font("Arial",Font.BOLD,14);
    static final Color grayColor = new Color(211,211,211);
    public static void createAndShowGUI(){
        JFrame frame =new JFrame();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        init(frame);
    }

    private static void init(JFrame frame){
        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600,400));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());
        Center center = new Center(historyPref);
        frame.add(center.getJSplitPane(),BorderLayout.CENTER);
        Top top = new Top(center,readHistory(),settingsPref);
        frame.add(top.getJPanel(),BorderLayout.NORTH);
        frame.setVisible(true);
        center.setDividerPos(false,settingsPref);
    }

    private static String[] readHistory(){
        String[] history =new String[5];
        for (int i = 0; i < 5; i++) {
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        return history;
    }


}
