package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class Main {
    private static final  Preferences settingsPref=Preferences.userRoot().node("swingDemo/settings");
    private static final  Preferences historyPref=Preferences.userRoot().node("swingDemo/history");
    static final Font arialBold = new Font("Arial",Font.BOLD,14);
    static final Color grayColor = new Color(211,211,211);

    public static void main(String[] args){
        //можно обойтись и без этого
         javax.swing.SwingUtilities.invokeLater(() -> {
             try {
                 createAndShowGUI();
             } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                      ClassNotFoundException e) {
                 throw new RuntimeException(e);
             }
         }
        );
    }
    public static void createAndShowGUI() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        JFrame frame =new JFrame();//creating instance of JFrame
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        init(frame);
        frame.setVisible(true);
    }

    private static void init(JFrame frame){
        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //окно дефолтного размера 400 на 600, которое будет открыто по центру доступного разрешения
        Toolkit toolkit= Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width/2 -300,toolkit.getScreenSize().height/2-200,600,400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.setLayout(new BorderLayout());
        //создание экземпляров классов center и top
        Center center = new Center(historyPref);
        Top top = new Top(settingsPref,center,readHistory());
        //заполнение ими фрейма
        frame.add(top.getJPanel(),BorderLayout.NORTH);
        frame.add(center.getJSplitPane(),BorderLayout.CENTER);
        frame.pack();//для работы LayoutManagers
        frame.setVisible(true);
        center.setDividerPos(false, settingsPref);//установка делителей сплитпейнов на положение из префов
    }




    private static String[] readHistory(){
        String[] history =new String[5];
        for (int i = 0; i < 5; i++) {
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        return history;
    }





}