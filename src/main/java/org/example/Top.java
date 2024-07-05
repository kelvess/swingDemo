package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static org.example.Main.*;

public class Top {
    private final JLabel title;
    private final JMenu submenu;
    private final JMenu submenuClose;
    private final JPanel top;
    private final String[] history;

    public Top(Preferences settingsPref, Center center, String[] history){
        //заполнение массива с историей
        this.history=history;
        //самый верхний лейбл с заголовком номера открытой траектории и его настройка
        title = new JLabel("Траектории",SwingConstants.CENTER);
        title.setBackground(grayColor);
        title.setFont(arialBold);
        title.setOpaque(true);
        title.setBounds(0,0,100,30);
        title.setPreferredSize(new Dimension(70,25));
        title.setMaximumSize(new Dimension(200,25));

        //основная панель, где на севере будет лейбл, снизу менюбар
        top = new JPanel(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        submenuClose=new JMenu();
        submenuClose.setText("Закрыть");

        submenu = new JMenu();
        this.createHistoryMenu(center);//этим методом субменю recent заполняется из префов
        menuBar.setLayout(new BoxLayout(menuBar,BoxLayout.X_AXIS));
        //заполнение меню "файл"
        menuBar.add(createFileMenu(center));
        //установка разделителя
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setBackground(Color.GRAY);
        sep.setMaximumSize(new Dimension(2,100));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(sep);
        //создание и заполнение меню "настройки"
        menuBar.add(createSettingsMenu(settingsPref,center));
        //настройки менюбара
        menuBar.setPreferredSize(new Dimension(200,30));
        menuBar.setMaximumSize(new Dimension(2000,30));

        JPanel cyan = new JPanel();//панелька для заполения пустого пространства справа cyan
        cyan.setBackground(Color.cyan);
        cyan.setBorder(BorderFactory.createLineBorder(Color.cyan,1));
        menuBar.add(cyan);
        menuBar.setBorderPainted(false);//так красивее
        top.add(title,BorderLayout.NORTH);//добавление полоски тайтла сверху
        top.add(menuBar, BorderLayout.SOUTH);//добавление менюбара в основную панель


    }
    //создаёт меню "файл" и заполняет менюитемами/субменю
    private JMenu createFileMenu(Center center)
    {
        //менюш
        JMenu file = new JMenu("Файл");
        file.setFont(new Font("Arial",Font.PLAIN,16));
        file.setBackground(grayColor);
        file.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        file.setHorizontalAlignment(SwingConstants.LEFT);
        //менюитемы
        JMenuItem open = new JMenuItem("Открыть");
        open.setMargin(new Insets(1, -25, 1, 0));

        JMenuItem closeAll = new JMenuItem("Закрыть все");
        closeAll.setMargin(new Insets(1, -25, 1, 0));

        ///вешаем действия на менюитемы
        open.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File textFile = chooser.getSelectedFile();
                center.openFile(textFile,this);
            }
            catch (Exception ex){
                System.err.println(ex);//если что-то случилось с открытием файла - ничего не делаем (кнопка не создастся)
            }
        });
        closeAll.addActionListener(e-> center.clearAll(this));//полная очистка

        file.add(open);
        submenu.setMargin(new Insets(1, -25, 1, 0));
        file.add(submenu);
        submenuClose.setMargin(new Insets(1, -25, 1, 0));//подобные отступы оттого, что при данном L&F появляется слева пустое место на ~25 пикселей
        file.add(submenuClose);
        file.add(closeAll);
        return file;
    }



    public JPanel getJPanel(){
        return top;
    }

    //создает меню "настройки" и менюитем "сохранить положение окон"
    private JMenu createSettingsMenu(Preferences settingsPref,Center center){
        JMenu settings = new JMenu("Настройки");
        settings.setHorizontalAlignment(SwingConstants.LEFT);//возможно не нужно
        settings.setBackground(grayColor);
        settings.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        settings.setFont(new Font("Arial",Font.PLAIN,16));
        JMenuItem saveSettings = new JMenuItem("Сохранить положение окон");
        saveSettings.setMargin(new Insets(1, -25, 1, 0));
        saveSettings.addActionListener(e -> center.setDividerPos(true,settingsPref));//при нажатии запись в префы положения делителей
        settings.add(saveSettings);
        return settings;
    }

    //изначально планировал при последовательном открытии файлов просто дополнять кнопки, при закрытии одного из середины полностью пересоздавать все кнопки
    //в последнем коммите упростил код и теперь при каждом открытии файла кнопки пересоздаются заново
    void updateCloseMenu(ArrayList<JButton> catalogButtons,ArrayList<String> paths,Center center){
        submenuClose.removeAll();
        fillSubmenuClose(catalogButtons,paths,center);
        submenuClose.revalidate();
        submenuClose.repaint();
    }
    //заполняет субменю с историей
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
        submenu.setMargin(new Insets(0,3,0,0));//скорее всего не влияет на внешний вид
    }

    //перезаписывает субменю с историей, почти 1в1 с методом createHistoryMenu
    void updateHistoryMenu(String[] history, Center center){
        submenu.removeAll();
        for (int i=0;i<5;i++)
        {
            if (history[i]!=null) {
                System.out.println(history[i]);
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

    //полностью заполняет субменю "закрыть", срабатывает при нажатии на кнопку из раздела "файл"->"открыть"
    void fillSubmenuClose(ArrayList<JButton> catalogButtons,ArrayList<String> catalogButtonsPaths,Center center){
        for (JButton button : catalogButtons) {
            JMenuItem close = new JMenuItem();
            close.setText(button.getText());
            int j = Integer.parseInt(button.getText().substring(11));
            close.addActionListener(e -> {
                center.closeAction(button, j, this);
                submenuClose.remove(close);
                updateCloseMenu(catalogButtons, catalogButtonsPaths, center);

            });
            close.setMargin(new Insets(1, -25, 1, 0));
            submenuClose.add(close);
        }
    }
    //сдвигает историю на 1, таким образом позволяя потерять последнюю и добавить новую
    private void swapHistory(){
        for (int i=4;i>0;i--){
            history[i]=history[i-1];
        }
    }

    //сохраняет историю, если файл с таким путём еще не записан в историю
    void saveHistory(File file, Preferences historyPref){
        for (int i=0;i<5;i++){
            if (Objects.equals(history[i], file.getAbsolutePath()))
                return;
        }
        swapHistory();
        history[0] = file.getAbsolutePath();//установка на пустое место новой истории
        for (int i = 0; i < 5; i++) {
            if (history[i] != null)
                historyPref.put(Integer.toString(i), history[i]);
        }
    }

    public String[] getHistory(){
        return history;
    }

    public void setTitle(String title){
        this.title.setText(title);
        this.title.revalidate();
    }


    public String getTitle(){
        return this.title.getText();
    }

}
