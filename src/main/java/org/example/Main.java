package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static java.nio.file.Files.*;
import static javax.swing.JOptionPane.showMessageDialog;


public class Main {
    private static JSplitPane center,left,right;
    private static JPanel leftTop,leftBottom,rightTop,rightBottom;
    private static final Border borderGray = BorderFactory.createLineBorder(Color.GRAY,1);
    private static final Font arialBold = new Font("Arial",Font.BOLD,14);
    private static final Font arialMenu = new Font("Arial",Font.PLAIN,16);
    static final Color grayColor = new Color(211,211,211);
    private final static Preferences settingsPref=Preferences.userRoot().node("swingDemo/settings");
    private final static Preferences historyPref=Preferences.userRoot().node("swingDemo/history");
    private static final String[] history = new String[5];
    private static final JTextArea text = new JTextArea();
    private static JMenu submenu, submenuClose;

    private static ArrayList<JButton> catalogButtons= new ArrayList<>();
    private static ArrayList<String> catalogButtonsPaths= new ArrayList<>();
    private static JLabel title,filePathLabel;
    private static JPanel leftTopButtons;

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
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        System.out.println(UIManager.getSystemLookAndFeelClassName());
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
        open.setMargin(new Insets(1, -25, 1, 0));
        JMenuItem closeAll = new JMenuItem("Закрыть все");
        closeAll.setMargin(new Insets(1, -25, 1, 0));
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
        submenu.setMargin(new Insets(1, -25, 1, 0));
        file.add(submenu);
        submenuClose.setMargin(new Insets(1, -25, 1, 0));
        file.add(submenuClose);
        file.add(closeAll);
        return file;
    }

    static private JMenu createSettingsMenu(){
        JMenu settings = new JMenu("Настройки");
        settings.setHorizontalAlignment(SwingConstants.LEFT);
        settings.setBackground(grayColor);
        settings.setBorder(borderGray);
        settings.setFont(arialMenu);
        JMenuItem saveSettings = new JMenuItem("Сохранить положение окон");
        saveSettings.setMargin(new Insets(1, -25, 1, 0));
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
            System.out.println("Установлены настройки");
            System.out.println(settingsPref.getDouble("left",0.5));
            System.out.println(settingsPref.getDouble("right", 0.5));
            System.out.println(settingsPref.getDouble("center", 0.5));
        }
    }



    private static JLabel createLabel(String text, Color color){
        JLabel jlabel = new JLabel(text, SwingConstants.CENTER);
        jlabel.setFont(Main.arialBold);
        jlabel.setBackground(color);
        jlabel.setBorder(borderGray);
        jlabel.setPreferredSize(new Dimension(0,30));
        jlabel.setMaximumSize(new Dimension(2000,30));
        jlabel.setMinimumSize(new Dimension(80,30));
        jlabel.setBorder(borderGray);
        return jlabel;
    }

    private static void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }


    private static void openFile(File file){

        if (file==null){
            return;
        }
        if (isOpenFile(file)){
            return;
        }

        saveHistory(file);
        System.out.println("История записана!");
        updateHistoryMenu();
        int a= catalogButtons.size()+1;
        catalogButtonsPaths.add(file.getAbsolutePath());
        JButton catalogButton = new JButton("Траектория "+a);
        catalogButton.setPreferredSize(new Dimension(0,30));
        catalogButton.setMaximumSize(new Dimension(2000,30));
        catalogButton.setMinimumSize(new Dimension(80,30));
        catalogButton.setBackground(Color.GRAY);
        File finalFile = file;
        catalogButton.addActionListener(e->{
            title.setText("Траектории - "+a);
            title.revalidate();
            title.repaint();

            filePathLabel.setText(finalFile.getAbsolutePath());
            filePathLabel.revalidate();
            filePathLabel.repaint();

            try {
                text.setText(String.join("\n ", readAllLines(Paths.get(finalFile.toURI()), StandardCharsets.UTF_8)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });

        fillLeftTop(catalogButton);
        System.out.println("Кнопка добавлена");
        fillSubmenuClose(catalogButton);
        System.out.println("Меню удаления добавлено");

    }

    private static void fillLeftTop(JButton button){
        leftTopButtons.add(button);
        catalogButtons.add(button);
        left.revalidate();
        left.repaint();
        leftTopButtons.revalidate();
        leftTopButtons.repaint();


    }


    private static void init(JFrame frame){

        readHistory();//чтение истории и создание субменю с историей
        submenuClose=new JMenu();
        submenuClose.setText("Закрыть");//присвоение субменю для закрытия названия
        ///Менюшка и надпись над ней
        title = new JLabel("Траектории", SwingConstants.CENTER);//тайтл
        title.setBackground(grayColor);
        title.setFont(arialBold);
        title.setOpaque(true);
        title.setBounds(0,0,100,30);
        title.setPreferredSize(new Dimension(70,25));
        title.setMaximumSize(new Dimension(200,25));



        JPanel top = new JPanel(new BorderLayout());//панелька с тайтлом сверху и меню снизу
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BoxLayout(menuBar,BoxLayout.X_AXIS));
        menuBar.add(createFileMenu());

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);//разделитель между кнопками меню
        sep.setBackground(Color.GRAY);
        sep.setMaximumSize(new Dimension(2,100));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(sep);

        menuBar.add(createSettingsMenu());
        menuBar.setPreferredSize(new Dimension(200,30));
        menuBar.setMaximumSize(new Dimension(2000,30));
        JPanel cyan = new JPanel();//панелька для заполения пустого пространства справа на cyan
        cyan.setBackground(Color.cyan);
        cyan.setBorder(BorderFactory.createLineBorder(Color.cyan,1));
        menuBar.add(cyan);
        menuBar.setBorderPainted(false);
        top.add(title,BorderLayout.NORTH);//добавление полоски тайтла сверху
        top.add(menuBar, BorderLayout.SOUTH);//добавление менюбара в панель топ
        ///

        //JSplitPanes
        center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        center.setLeftComponent(left);
        center.setRightComponent(right);
        center.setResizeWeight(0.5);
        right.setResizeWeight(0.5);
        left.setResizeWeight(0.5);
        ///END JSplitPanes




        ///LABELS
        JLabel fileLabel = createLabel("Файл", grayColor);
        filePathLabel = createLabel("filePath", grayColor);
        JLabel catalogLabel = createLabel("Каталог", grayColor);
        JLabel plotLabel = createLabel("График", grayColor);
        JLabel tableLabel = createLabel("Таблица", grayColor);
        ///END LABELS



        //Заполнение левого верхнего угла
        leftTop = new JPanel(new BorderLayout());
        leftTop.add(catalogLabel,BorderLayout.NORTH);
        leftTopButtons = new JPanel();
        leftTopButtons.setLayout(new BoxLayout(leftTopButtons,BoxLayout.Y_AXIS));
        leftTop.add(new JScrollPane(leftTopButtons),BorderLayout.CENTER);
        left.setTopComponent(leftTop);
        ///

        ///Заполнение левого нижнего угла
        leftBottom = new JPanel(new BorderLayout());
        JPanel leftBottomLabel = new JPanel(new BorderLayout());
        leftBottomLabel.add(fileLabel,BorderLayout.NORTH);
        leftBottomLabel.add(filePathLabel,BorderLayout.SOUTH);
        leftBottom.add(leftBottomLabel,BorderLayout.NORTH);
        text.setEditable(false);
        leftBottom.add(new JScrollPane(text),BorderLayout.CENTER);
        left.setBottomComponent(leftBottom);

        ///


        ///Заполнение правого верхнего угла
        rightTop= new JPanel(new BorderLayout());
        rightTop.add(tableLabel,BorderLayout.NORTH);
        JTable table = new JTable(100,7);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);
        right.setTopComponent(rightTop);
        ///


        ///Заполнение правого нижнего угла
        right.setBottomComponent(plotLabel);
        ///

        frame.setTitle("Траектории");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit= Toolkit.getDefaultToolkit();
        frame.setBounds(toolkit.getScreenSize().width/2 -300,toolkit.getScreenSize().height/2-200,600,400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.add(top,BorderLayout.NORTH);
        frame.add(center,BorderLayout.CENTER);

        frame.pack();
        setDividerPos(false);

    }


    private static void saveHistory(File file){
        for (int i=0;i<5;i++){
            if (history[i]==file.getAbsolutePath())
                return;
        }
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
        createHistoryMenu();
        System.out.println("Меню истории создано");
    }

    private static void clearAll(){
        for (JButton button : catalogButtons)
            leftTopButtons.remove(button);
        catalogButtons.clear();
        catalogButtonsPaths.clear();
        text.setText("");

    }

    private static void createHistoryMenu(){
        submenu = new JMenu();
        submenu.setText("Недавно открытые");
        for (int i=0;i<5;i++)
        {
            if (history[i]!=null) {
                System.out.println(history[i]);
                JMenuItem historyItem = new JMenuItem();
                historyItem.setText((history[i].substring(history[i].lastIndexOf("\\") + 1)));
                int finalI = i;
                historyItem.addActionListener(e -> {
                    openFile(new File(history[finalI]));
                });
                historyItem.setMargin(new Insets(1, -25, 1, 0));
                submenu.add(historyItem);
            }
        }
        submenu.setMargin(new Insets(0,3,0,0));

    }

    private static void updateHistoryMenu(){
        submenu.removeAll();
        for (int i=0;i<5;i++)
        {
            if (history[i]!=null) {
                System.out.println(history[i]);
                JMenuItem historyItem = new JMenuItem();
                historyItem.setText((history[i].substring(history[i].lastIndexOf("\\") + 1)));
                int finalI = i;
                historyItem.addActionListener(e -> {
                    openFile(new File(history[finalI]));
                });
                historyItem.setMargin(new Insets(1, -25, 1, 0));
                submenu.add(historyItem);
            }
        }
    }

    private static boolean isOpenFile(File file){
        for (String catalogButtonsPath : catalogButtonsPaths) {
            if (Objects.equals(catalogButtonsPath, file.getAbsolutePath())) {
                showMessageDialog(null, "Эта траектория уже открыта!");
                return true;
            }
        }
        return false;
    }

    private static void fillSubmenuClose(JButton button){
        JMenuItem close = new JMenuItem();
        close.setText(button.getText());
        int i =catalogButtons.indexOf(button);
        int j =Integer.parseInt( button.getText().substring(11));
        close.addActionListener(e->{
            leftTopButtons.remove(button);
            if (title.getText().length()>11) {
                if (Integer.parseInt(title.getText().substring(13)) == j) {
                    text.setText("");
                    text.repaint();
                    text.revalidate();
                    title.setText("Траектории");
                    filePathLabel.setText("Path");
                }
            }
            submenuClose.remove(close);
            left.repaint();
            left.revalidate();
            catalogButtonsPaths.remove(i);
            catalogButtons.remove(button);
            updateCloseMenu();
        });
        close.setMargin(new Insets(1, -25, 1, 0));
        submenuClose.add(close);
    }

    private static void updateCloseMenu(){
        submenuClose.removeAll();
        for (JButton Button : catalogButtons){
            fillSubmenuClose(Button);
        }
    }
}