package org.example;

import javax.swing.*;
import java.awt.*;
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
    private static final ArrayList<JButton> catalogButtons= new ArrayList<JButton>();
    void fillLeftTop(JButton button, ArrayList<JButton> catalogButtons){
        leftTopButtons.add(button);
        catalogButtons.add(button);

        leftTopButtons.revalidate();
        leftTopButtons.repaint();
    }


    public Center(Preferences historyPref){
        center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        this.historyPref=historyPref;
        center.setLeftComponent(left);
        center.setRightComponent(right);
        center.setResizeWeight(0.5);
        right.setResizeWeight(0.5);
        left.setResizeWeight(0.5);

        JLabel fileLabel = createLabel("Файл");
        filePathLabel = createLabel("filePath");
        JLabel catalogLabel = createLabel("Каталог");
        JLabel plotLabel = createLabel("График");
        JLabel tableLabel = createLabel("Таблица");

        JPanel leftTop = new JPanel(new BorderLayout());
        leftTop.add(catalogLabel,BorderLayout.NORTH);
        leftTopButtons = new JPanel();
        leftTopButtons.setLayout(new BoxLayout(leftTopButtons,BoxLayout.Y_AXIS));
        leftTop.add(new JScrollPane(leftTopButtons),BorderLayout.CENTER);
        left.setTopComponent(leftTop);

        JPanel leftBottom = new JPanel(new BorderLayout());
        JPanel leftBottomLabel = new JPanel(new BorderLayout());
        leftBottomLabel.add(fileLabel,BorderLayout.NORTH);
        leftBottomLabel.add(filePathLabel,BorderLayout.SOUTH);
        leftBottom.add(leftBottomLabel,BorderLayout.NORTH);
        text.setEditable(false);
        text.setFont(new Font("Arial",Font.PLAIN,12));
        leftBottom.add(new JScrollPane(text),BorderLayout.CENTER);
        left.setBottomComponent(leftBottom);

        rightTop= new JPanel(new BorderLayout());
        rightTop.add(tableLabel,BorderLayout.NORTH);
        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);
        right.setTopComponent(rightTop);
        JPanel rightBottom = new JPanel(new BorderLayout());
        rightBottom.add(plotLabel, BorderLayout.NORTH);
        right.setBottomComponent(rightBottom);

        center.setLeftComponent(left);
        center.setRightComponent(right);

    }

    void openAction(String string, String[][] tableData, String path, Top top){
        top.setTitle("Траектории - "+counter);
        filePathLabel.setText(path);
        filePathLabel.revalidate();
        filePathLabel.repaint();
        text.setText(string);
        if (rightTop.getComponentCount()>1)
            rightTop.remove(1);
        table = new JTable(tableData,colNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);

        rightTop.revalidate();
        rightTop.repaint();
        right.revalidate();
        right.repaint();
    }


    void setDividerPos(boolean set, Preferences settingsPref){
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
        }
    }


    private JLabel createLabel(String text){
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
            leftTopButtons.remove(button);
        catalogButtons.clear();
        catalogButtonsPaths.clear();
        if (rightTop.getComponentCount()>1)
            rightTop.remove(1);
        text.setText("");
        counter=1;
        filePathLabel.setText("Path");
        top.setTitle("Траектории");
    }

    public void setCounter(int i){
        counter+=i;
    }

    public JSplitPane getJSplitPane(){
        return center;
    }

    void openFile(File file, Top top){

        if (file==null){
            return;
        }
        if (isOpenFile(file)){
            return;
        }
        top.saveHistory(file,this.historyPref);
        top.updateHistoryMenu(top.getHistory(),this);
        catalogButtonsPaths.add(file.getAbsolutePath());
        JButton catalogButton = new JButton("Траектория "+counter);
        catalogButton.setPreferredSize(new Dimension(0,30));
        catalogButton.setMaximumSize(new Dimension(2000,30));
        catalogButton.setMinimumSize(new Dimension(80,30));
        catalogButton.setBackground(Color.GRAY);
        String string;
        try {
            string=String.join("\n ", readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            showMessageDialog(null, "Файл не соответсвует траектории!");
            System.err.println(ex);
            return;
        }

        ArrayList<String> tableRows=new ArrayList<>();
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

        String[][] tableData = new String[tableRows.size()/7][7];
        for (int i=0;i<tableRows.size()/7;i++){
            for (int j=0;j<7;j++){
                tableData[i][j]=tableRows.get(i*7+j);
            }
        }


        catalogButton.addActionListener(e->{
            this.openAction(string,tableData,file.getAbsolutePath(),top);

        });
        this.setCounter(1);
        this.fillLeftTop(catalogButton,catalogButtons);
        top.fillSubmenuClose(catalogButton,catalogButtons,catalogButtonsPaths,this);
    }


    public void closeAction(JButton button, int j, Top top){
        leftTopButtons.remove(button);
        if (top.getTitle().length()>11) {
            if (Integer.parseInt(top.getTitle().substring(13)) == j) {
                text.setText("");
                text.repaint();
                text.revalidate();
                top.setTitle("Траектории");
                filePathLabel.setText("Path");
                if (rightTop.getComponentCount()>1)
                    rightTop.remove(1);
            }
        }
        left.repaint();
        left.revalidate();
    }


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
