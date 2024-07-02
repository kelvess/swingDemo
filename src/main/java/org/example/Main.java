package org.example;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;


public class Main {
    private static JSplitPane center,left,right;
    private static JPanel leftTop;
    private static final Border borderGray = BorderFactory.createLineBorder(Color.GRAY,1);
    private static final Font arialBold = new Font("Arial",Font.BOLD,14);
    private static final Font arialMenu = new Font("Arial",Font.PLAIN,16);
    static final Color grayColor = new Color(211,211,211);
    private final static Preferences settingsPref=Preferences.userRoot().node("swingDemo/settings");
    private final static Preferences historyPref=Preferences.userRoot().node("swingDemo/history");
    private static final String[] history = new String[5];

    private static ArrayList<JButton> catalogButtons= new ArrayList<>();

    private static final GridBagConstraints leftTopConstraints= new GridBagConstraints();

    public static void main(String[] args){
     javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {

            try {
                createAndShowGUI();
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    });
    }
    public static void createAndShowGUI() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        JFrame frame =new JFrame();//creating instance of JFrame
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        init(frame);
        frame.setVisible(true);

    }


    static private JMenu createFileMenu()
    {
        JMenu file = new JMenu("Файл");
        file.setFont(arialMenu);
        file.setBackground(grayColor);
        file.setBorder(borderGray);
        file.setHorizontalAlignment(SwingConstants.LEFT);
        JMenuItem open = new JMenuItem("Открыть");

        open.setMargin(new Insets(1, -25, 1, 1));
        JMenuItem openRecent = new JMenuItem("Открыть недавние");
        openRecent.setMargin(new Insets(1, -25, 1, 1));
        JMenuItem close = new JMenuItem("Закрыть>");
        close.setMargin(new Insets(1, -25, 1, 1));
        JMenuItem closeAll = new JMenuItem("Закрыть все");
        closeAll.setMargin(new Insets(1, -25, 1, 1));
        ///вешаем действия на кнопки
        open.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File textFile = chooser.getSelectedFile();
                openFile(textFile);

            }
            catch (Exception ex){
                System.err.println(ex);
            }

        });
        closeAll.addActionListener(e->
                clearAll());


        file.add(open);
        file.addSeparator();
        file.add(openRecent);
        file.addSeparator();
        file.add(close);
        file.addSeparator();
        file.add(closeAll);
        return file;
    }

    static private JMenu createSettingsMenu(){
        JMenu settings = new JMenu("Настройки");
        settings.setHorizontalAlignment(SwingConstants.LEFT);
        settings.setBackground(grayColor);
        settings.setBorder(borderGray);
        settings.setFont(arialMenu);
        settings.setHorizontalAlignment(SwingConstants.CENTER);
        settings.setVerticalAlignment(SwingConstants.CENTER);
        JMenuItem saveSettings = new JMenuItem("Сохранить положение окон");
        saveSettings.setMargin(new Insets(1, -25, 1, 1));
        saveSettings.addActionListener(e -> setDividerPos(true));
        settings.add(saveSettings);
        return settings;
    }

    private static void setDividerPos(boolean set){
        if (set){
            settingsPref.putDouble("left", (double) left.getDividerLocation() /left.getHeight());
            settingsPref.putDouble("right", (double) right.getDividerLocation() /right.getHeight());
            settingsPref.putDouble("center", (double) center.getDividerLocation() /center.getWidth());
        }
        else {
            left.setDividerLocation(settingsPref.getDouble("left",0.5));
            right.setDividerLocation(settingsPref.getDouble("right", 0.5));
            center.setDividerLocation(settingsPref.getDouble("center", 0.5));
            center.revalidate();
            left.revalidate();
            right.revalidate();
            System.out.println("Спизжены настройки");
            System.out.println(settingsPref.getDouble("left",0.5));
            System.out.println(settingsPref.getDouble("right", 0.5));
            System.out.println(settingsPref.getDouble("center", 0.5));
        }
    }



    private static JLabel createLabel(String text,Font font,Color color){
        JLabel jlabel = new JLabel(text, SwingConstants.CENTER);
        jlabel.setFont(font);
        jlabel.setBackground(color);
        jlabel.setBorder(borderGray);
        return jlabel;
    }

    private static void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }


    private static void openFile(File file){
        if (file == null) {
            FileChooser chooser = new FileChooser();
            file = chooser.showOpenDialog(null);
            if (file == null)
                return;
            saveHistory(file);
        }


        JButton catalogButton = new JButton(file.getName());
        catalogButton.setBackground(Color.GRAY);
        fillLeftTop(catalogButton);

    }

    private static void fillLeftTop(JButton button){
        leftTopConstraints.gridy++;
        leftTop.add(button,leftTopConstraints);
        catalogButtons.add(button);
        left.revalidate();
        left.repaint();


    }


    private static void init(JFrame frame){

        JPanel top = new JPanel(new BorderLayout()); //Панель с верхней синей полоской и менюбаром
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar,BoxLayout.X_AXIS));
        menuBar.add(createFileMenu());
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setBackground(Color.GRAY);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(sep);
        menuBar.add(createSettingsMenu());
        menuBar.setMaximumSize(new Dimension(135,30));

        //JSplitPanes
        center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        center.setLeftComponent(left);
        center.setRightComponent(right);
        center.setResizeWeight(0.5);
        right.setResizeWeight(0.5);
        left.setResizeWeight(0.5);
        ///END JSplitPanes




        ///LABELS
        JLabel fileLabel = createLabel("Файл",arialBold, grayColor);
        JLabel filePathLabel = createLabel("filePath",arialBold, grayColor);
        JLabel catalogLabel = createLabel("Каталог",arialBold, grayColor);
        JLabel plotLabel = createLabel("График",arialBold, grayColor);
        JLabel tableLabel = createLabel("Таблица",arialBold, grayColor);
        JLabel blue = new JLabel("Траектории", SwingConstants.CENTER);

        catalogLabel.setBorder(borderGray);

        right.setTopComponent(tableLabel);

        right.setBottomComponent(plotLabel);

        blue.setBackground(Color.CYAN);
        blue.setFont(arialBold);
        blue.setOpaque(true);
        blue.setBounds(0,0,100,30);
        top.add(blue,BorderLayout.NORTH);
        ///END LABELS



        //Заполнение левого верхнего угла
        leftTop = new JPanel(new GridBagLayout());
        leftTop.setBackground(Color.ORANGE);
        leftTopConstraints.insets=new Insets(0,0,0,0);
        leftTopConstraints.anchor=GridBagConstraints.NORTH;
        leftTopConstraints.fill=GridBagConstraints.HORIZONTAL;
        leftTopConstraints.ipady=10;
        leftTopConstraints.weightx=0.5;
        //leftTopConstraints.weighty=0.5;
        leftTop.add(catalogLabel,leftTopConstraints);
        leftTopConstraints.gridy=1;


        left.setTopComponent(leftTop);
        ///

        ///Заполнение левого нижнего угла
        left.setBottomComponent(fileLabel);

        ///

        Box hboxmenu = Box.createHorizontalBox();
        hboxmenu.add(menuBar);
        top.add(hboxmenu, BorderLayout.SOUTH);
        menuBar.setBackground(grayColor);
        menuBar.setBorderPainted(false);
        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit= Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width/2 -300,toolkit.getScreenSize().height/2-200,600,400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.add(top,BorderLayout.NORTH);
        frame.add(center,BorderLayout.CENTER);
        readHistory();
        frame.pack();
        setDividerPos(false);

    }


    private static void saveHistory(File file){
        readHistory();
        swapHistory();
        history[0] = file.getAbsolutePath();
        for (int i = 0; i < 5; i++) {
            if (history[i] != null)
                historyPref.put(Integer.toString(i), history[i]);
        }

    }

    private static void readHistory(){
        for (int i = 0; i < 5; i++) {
            history[i] = historyPref.get(Integer.toString(i), null);
        }
        System.out.println("История прочитана");
    }

    private static void clearAll(){
        for (JButton button : catalogButtons)
            leftTop.remove(button);
        catalogButtons.clear();
        leftTop.revalidate();
        leftTop.repaint();
    }

}