package org.example;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static org.example.MainFrame.arialBold;
import static org.example.MainFrame.grayColor;

public class Top {
    private final JLabel title;
    private final JMenu submenu;
    private final JMenu submenuClose;
    private final JPanel top;
    private final String[] history;

    protected Top(Preferences settingsPref, Center center, String[] history){
        this.history=history;
        title = new JLabel("Траектории",SwingConstants.CENTER);
        title.setBackground(grayColor);
        title.setFont(arialBold);
        title.setOpaque(true);
        title.setBounds(0,0,100,30);
        title.setPreferredSize(new Dimension(70,25));
        title.setMaximumSize(new Dimension(200,25));

        top = new JPanel(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        submenuClose=new JMenu();
        submenuClose.setText("Закрыть");

        submenu = new JMenu();
        this.createHistoryMenu(center);
        menuBar.setLayout(new BoxLayout(menuBar,BoxLayout.X_AXIS));
        menuBar.add(createFileMenu(center));
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setBackground(Color.GRAY);
        sep.setMaximumSize(new Dimension(2,100));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(sep);
        menuBar.add(createSettingsMenu(settingsPref,center));
        menuBar.setPreferredSize(new Dimension(200,30));
        menuBar.setMaximumSize(new Dimension(2000,30));

        JPanel cyanJPanel = new JPanel();
        cyanJPanel.setBackground(Color.CYAN);
        cyanJPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN,1));
        menuBar.add(cyanJPanel);
        menuBar.setBorderPainted(false);
        top.add(title,BorderLayout.NORTH);
        top.add(menuBar, BorderLayout.SOUTH);
    }
    private JMenu createFileMenu(Center center)
    {
        JMenu file = new JMenu("Файл");
        file.setFont(new Font("Arial",Font.PLAIN,16));
        file.setBackground(grayColor);
        file.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        file.setHorizontalAlignment(SwingConstants.LEFT);
        JMenuItem open = new JMenuItem("Открыть");
        open.setMargin(new Insets(1, -25, 1, 0));

        JMenuItem closeAll = new JMenuItem("Закрыть все");
        closeAll.setMargin(new Insets(1, -25, 1, 0));

        open.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File textFile = chooser.getSelectedFile();
                center.openFile(textFile,this);
            }catch (Exception ex){
                System.err.println(ex);
            }
        });
        closeAll.addActionListener(e-> center.clearAll(this));
        file.add(open);
        submenu.setMargin(new Insets(1, -25, 1, 0));
        file.add(submenu);
        submenuClose.setMargin(new Insets(1, -25, 1, 0));
        file.add(submenuClose);
        file.add(closeAll);
        return file;
    }



    protected JPanel getJPanel(){
        return top;
    }

    private JMenu createSettingsMenu(Preferences settingsPref,Center center){
        JMenu settings = new JMenu("Настройки");
        settings.setHorizontalAlignment(SwingConstants.LEFT);
        settings.setBackground(grayColor);
        settings.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        settings.setFont(new Font("Arial",Font.PLAIN,16));
        JMenuItem saveSettings = new JMenuItem("Сохранить положение окон");
        saveSettings.setMargin(new Insets(1, -25, 1, 0));
        saveSettings.addActionListener(e -> center.setDividerPos(true,settingsPref));
        settings.add(saveSettings);
        return settings;
    }

    protected void updateCloseMenu(ArrayList<JButton> catalogButtons,ArrayList<String> paths,Center center){
        submenuClose.removeAll();
        fillSubmenuClose(catalogButtons,paths,center);
        submenuClose.revalidate();
        submenuClose.repaint();
    }

    private void createHistoryMenu(Center center){
        submenu.setText("Недавно открытые");
        for (int i=0;i<5;i++)
        {
            if (history[i]!=null) {
                JMenuItem historyItem = new JMenuItem();
                historyItem.setText((history[i].substring(history[i].lastIndexOf("\\") + 1)));
                int finalI = i;
                historyItem.addActionListener(e -> center.openFile(new File(history[finalI]),this));
                historyItem.setMargin(new Insets(1, -25, 1, 0));
                submenu.add(historyItem);
            }
        }
        submenu.setMargin(new Insets(0,3,0,0));
    }


    protected void updateHistoryMenu(Center center){
        submenu.removeAll();
        for (int i=0;i<5;i++){
            if (history[i]!=null) {
                JMenuItem historyItem = new JMenuItem();
                historyItem.setText((history[i].substring(history[i].lastIndexOf("\\") + 1)));
                int finalI = i;
                historyItem.addActionListener(e -> center.openFile(new File(history[finalI]),this));
                historyItem.setMargin(new Insets(1, -25, 1, 0));
                submenu.add(historyItem);
            }
        }
        submenu.repaint();
        submenu.revalidate();
    }

    protected void fillSubmenuClose(ArrayList<JButton> catalogButtons,ArrayList<String> catalogButtonsPaths,Center center){
        for (int i =0;i<catalogButtons.size();i++) {
            JMenuItem close = new JMenuItem();
            close.setText(catalogButtons.get(i).getText());
            int j = Integer.parseInt(catalogButtons.get(i).getText().substring(11));
            int finalI = i;
            close.addActionListener(e -> {
                center.closeAction(catalogButtons.get(finalI), j, this, catalogButtonsPaths.get(finalI));
                submenuClose.remove(close);
                updateCloseMenu(catalogButtons, catalogButtonsPaths, center);
            });
            close.setMargin(new Insets(1, -25, 1, 0));
            submenuClose.add(close);
        }
    }
    private void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }

    protected void saveHistory(File file, Preferences historyPref){
        for (int i=0;i<5;i++){
            if (Objects.equals(history[i], file.getAbsolutePath()))
                return;
        }
        swapHistory();
        history[0] = file.getAbsolutePath();
        for (int i = 0; i < 5; i++) {
            if (history[i] != null)
                historyPref.put(Integer.toString(i), history[i]);
        }
    }


    protected void setTitle(String title){
        this.title.setText(title);
        this.title.revalidate();
    }


    protected String getTitle(){
        return this.title.getText();
    }

}
