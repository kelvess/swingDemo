package org.example;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.prefs.Preferences;

import static java.nio.file.Files.readAllLines;
import static javax.swing.JOptionPane.showMessageDialog;

public class Center {

    private final JSplitPane center;
    private final JSplitPane left;
    private final JSplitPane right;
    private final JPanel rightTop;
    private final JPanel leftTopButtons;
    private final Preferences historyPref;

    private JTable table;
    private final JLabel filePathLabel;
    private  final JTextArea text = new JTextArea();
    private int counter=1;

    private final String[] colNames={"T,c","X,м","Y,м","Z,м","Vx,м/c","Vy,м/c","Vz,м/c"};
    private final ArrayList<String> catalogButtonsPaths= new ArrayList<>();
    private static final ArrayList<JButton> catalogButtons= new ArrayList<>();
    void fillLeftTop(JButton button){
        //добавляет кнопку в панель, в arraylist и перерисовывает панель
        leftTopButtons.add(button);
        catalogButtons.add(button);
        leftTopButtons.repaint();
    }


    public Center(Preferences historyPref){
        //инициализация полей сплит пейнов и их настройки масштабированния
        center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        this.historyPref=historyPref;//присвоение полю предпочтений истории
        center.setResizeWeight(0.5);
        right.setResizeWeight(0.5);
        left.setResizeWeight(0.5);


        //создание лейблов для четырех окон, один из них может изменяться поэтому создан как поле
        JLabel fileLabel = createLabel("Файл");
        filePathLabel = createLabel("Path");
        JLabel catalogLabel = createLabel("Каталог");
        JLabel plotLabel = createLabel("График");
        JLabel tableLabel = createLabel("Таблица");


        //заполнение левого верхнего элемента
        JPanel leftTop = new JPanel(new BorderLayout());
        leftTop.add(catalogLabel,BorderLayout.NORTH);//добавили в север лейбл "каталог"
        leftTopButtons = new JPanel();
        leftTopButtons.setLayout(new BoxLayout(leftTopButtons,BoxLayout.Y_AXIS));//создание отдельной панели для кнопок
        leftTop.add(new JScrollPane(leftTopButtons),BorderLayout.CENTER);//добавили в центр скролпейн, чтобы лейбл оставался
        // на месте и крутить можно было только сами кнопки
        left.setTopComponent(leftTop);


        //заполнение левого нижнего элемента
        JPanel leftBottom = new JPanel(new BorderLayout());//основная панель
        JPanel leftBottomLabel = new JPanel(new BorderLayout());//дополнительная панелька для двух лейблов
        leftBottomLabel.add(fileLabel,BorderLayout.NORTH);
        leftBottomLabel.add(filePathLabel,BorderLayout.SOUTH);
        leftBottom.add(leftBottomLabel,BorderLayout.NORTH);//добавили на север доп. панель с лейблами
        text.setEditable(false);
        text.setFont(new Font("Arial",Font.PLAIN,12));
        leftBottom.add(new JScrollPane(text),BorderLayout.CENTER);//добавили на центр скролпейн с текстом
        left.setBottomComponent(leftBottom);


        //заполнение правого верхнего элемента
        rightTop= new JPanel(new BorderLayout());
        rightTop.add(tableLabel,BorderLayout.NORTH);
        table = new JTable();//инициализирую таблицу без кол-ва строк и столбцов, в принципе можно удалить эту строчку
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);// и эту тоже
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);//ну и эту
        right.setTopComponent(rightTop);

        //заполнение правого нижнего элемента (в данном случае только лейбл "график")
        JPanel rightBottom = new JPanel(new BorderLayout());
        rightBottom.add(plotLabel, BorderLayout.NORTH);
        right.setBottomComponent(rightBottom);

        //уставновка в центральный слпитпейн боковых
        center.setLeftComponent(left);
        center.setRightComponent(right);
    }

    void openAction(String string, String[][] tableData, String path, Top top){
        top.setTitle("Траектории - "+counter);
        //обновление лейбла с filepath
        filePathLabel.setText(path);
        filePathLabel.revalidate();
        filePathLabel.repaint();
        //установка текста в соответсвующую область
        text.setText(string);
        if (rightTop.getComponentCount()>1)//если таблица справа сверху уже есть, то удаляем
            rightTop.remove(1);
        table = new JTable(tableData,colNames); //создаём новую таблицу конструктором с "нашими" даннными
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);//добавляем вместо старой таблицы (если она была)
        //после удаления и добавления нового перерисовать панель
        rightTop.revalidate();
        rightTop.repaint();
    }


    void setDividerPos(boolean set, Preferences settingsPref){
        //устанавливает настройки делителей,либо сохраняет их в префы
        if (set){
            settingsPref.putDouble("left", (double) left.getDividerLocation() /left.getHeight());
            settingsPref.putDouble("right", (double) right.getDividerLocation() /right.getHeight());
            settingsPref.putDouble("center", (double) center.getDividerLocation() /center.getWidth());
        }
        else {
            left.setDividerLocation(settingsPref.getDouble("left",0.5));
            right.setDividerLocation(settingsPref.getDouble("right", 0.5));
            center.setDividerLocation(settingsPref.getDouble("center", 0.5));
        }
    }


    private JLabel createLabel(String text){//метод для создания лейблов "центра"
        JLabel jlabel = new JLabel(text, SwingConstants.CENTER);
        jlabel.setFont(Main.arialBold);
        jlabel.setBackground(Main.grayColor);
        jlabel.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        jlabel.setPreferredSize(new Dimension(0,30));
        jlabel.setMaximumSize(new Dimension(2000,30));
        jlabel.setMinimumSize(new Dimension(80,30));
        jlabel.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        return jlabel;
    }


     void clearAll(Top top){
        for (JButton button : catalogButtons)
            leftTopButtons.remove(button);//удаляем все кнопки из верхней левой панели
        leftTopButtons.repaint();
        catalogButtons.clear();//очистка поля arraylist с типом JButton
        catalogButtonsPaths.clear();//очистка поля arraylist с типом String
        if (rightTop.getComponentCount()>1)
            rightTop.remove(1);//если таблица есть - удаляем
        text.setText("");//установка пустого окна с текстом
        counter=1;//установка счётчика открытых файлов на 1
        filePathLabel.setText("Path");//установка filepath на default
        top.setTitle("Траектории");
        top.updateHistoryMenu(this);
    }

    public void setCounter(int i){//сеттер для номера следующего открытого файла
        counter+=i;
    }

    public JSplitPane getJSplitPane(){//геттер для центрального сплитпейна
        return center;
    }

    void openFile(File file, Top top){
        if (file==null){//нет файла - нет дела
            return;
        }
        if (isOpenFile(file)){//если файл уже содержится в "каталоге" - ничего не делать (алерт вылетит из этого метода)
            return;
        }

        top.saveHistory(file,this.historyPref);//сохранить в истории открытых файлов
        top.updateHistoryMenu(this);//обновить менюшку с недавними
        catalogButtonsPaths.add(file.getAbsolutePath());//добавить в arraylist<string> путей
        JButton catalogButton = new JButton("Траектория "+counter);//создание и настройка кнопки для размещения в левом верхнем углу
        catalogButton.setPreferredSize(new Dimension(0,30));
        catalogButton.setMaximumSize(new Dimension(2000,30));
        catalogButton.setMinimumSize(new Dimension(80,30));
        catalogButton.setBackground(Color.GRAY);
        String string;
        //попытка чтенния файла в строку для заполения текстовой области
        try {
            string=String.join("\n ", readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            showMessageDialog(null, "Файл не соответсвует траектории!");
            System.err.println(ex);
            return;
        }

        ArrayList<String> tableRows=new ArrayList<>();//переменная для хранения строк файла, которая будет заполнятся при соот-вии файла регулярке ниже
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                if (str.matches("[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8} {2}[0-9]{0,8}\\.[0-9]{0,8}")) {
                    tableRows.addAll(Arrays.asList(str.split(" {2}")));
                }
                str = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        //разделяем arraylist в двумерный массив для передачи в конструктор JTable
        String[][] tableData = new String[tableRows.size()/7][7];
        for (int i=0;i<tableRows.size()/7;i++){
            for (int j=0;j<7;j++){
                tableData[i][j]=tableRows.get(i*7+j);
            }
        }

        //установка на кнопку действия
        catalogButton.addActionListener(e-> this.openAction(string,tableData,file.getAbsolutePath(),top));
        this.setCounter(1);//увеличние на +1 счётчика следующей траектории
        this.fillLeftTop(catalogButton);//заполнение левой верхней панели
        leftTopButtons.revalidate();
        top.updateCloseMenu(catalogButtons,catalogButtonsPaths,this);//полное перезаполнение субменю для удаления этой кнопки
    }

    //действие при нажатии на одну из кнопок из раздела "закрыть"
    public void closeAction(JButton button, int j, Top top, String catalogButtonPath){
        if (catalogButtons.size()==1){//теперь если кнопка осталась последняя - будет
            // выполняться clearAll - то есть следующие открытые файлы будут нумерованы с 1, а не с последнего+1
            clearAll(top);
        }
        else{
            if (top.getTitle().length() > 11) {//если траектория была открыта
                if (Integer.parseInt(top.getTitle().substring(13)) == j) {//если номер траектории совпадает с открытой сейчас
                    text.setText("");//установить текст на default
                    top.setTitle("Траектории");//установить тайл на default
                    filePathLabel.setText("Path");//установить filepath на default
                    if (rightTop.getComponentCount() > 1)//удалить таблицу если была
                        rightTop.remove(1);
                }
            }

            //удаление из верхней левой панельки кнопки открытия
            leftTopButtons.remove(button);
            //удаление из arraylist<jbutton> с кнопками
            catalogButtons.remove(button);
            //удаление из arraylist<string> с путями
            catalogButtonsPaths.remove(catalogButtonPath);


            leftTopButtons.repaint();//перерисовка после удаления
            leftTopButtons.revalidate();//только repaint() не решает отрисовку после закрытия
        }
        for (String h:catalogButtonsPaths){
            System.out.println(h);
            System.out.println(filePathLabel.getText());
        }
        for (JButton h: catalogButtons){
            System.out.println(h.getText());
        }

    }


    //проверка на то, есть ли путь к файлу среди открытых
    boolean isOpenFile(File file){
        for (String catalogButtonsPath : catalogButtonsPaths) {
            if (Objects.equals(catalogButtonsPath, file.getAbsolutePath())) {
                showMessageDialog(null, "Эта траектория уже открыта!");
                return true;
            }
        }
        return false;
    }



}
