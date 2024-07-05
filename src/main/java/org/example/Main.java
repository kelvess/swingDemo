package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.prefs.Preferences;

import static javax.swing.JOptionPane.showMessageDialog;


public class Main {
    private static final  Preferences settingsPref=Preferences.userRoot().node("swingDemo/settings");
    private static final  Preferences historyPref=Preferences.userRoot().node("swingDemo/history");

    private static final Border borderGray = BorderFactory.createLineBorder(Color.GRAY,1);
    static final Font arialBold = new Font("Arial",Font.BOLD,14);

    static final Color grayColor = new Color(211,211,211);








    public static void main(String[] args){
     javax.swing.SwingUtilities.invokeLater(() -> {
         try {
             createAndShowGUI();
         } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                  ClassNotFoundException e) {
             throw new RuntimeException(e);
         }
     });
    }
    public static void createAndShowGUI() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        JFrame frame =new JFrame();//creating instance of JFrame
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        init(frame);
        frame.setVisible(true);

    }











    private static void init(JFrame frame){

        readHistory();//чтение истории и создание субменю с историей

        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit= Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width/2 -300,toolkit.getScreenSize().height/2-200,600,400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.setLayout(new BorderLayout());
        Center center = new Center(historyPref);
        Top top = new Top(settingsPref,center,readHistory());
        frame.add(top.getJPanel(),BorderLayout.NORTH);
        frame.add(center.getJSplitPane(),BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        center.setDividerPos(false, settingsPref);
    }




    private static String[] readHistory(){
        String[] history =new String[5];
        for (int i = 0; i < 5; i++) {
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        return history;
    }





}