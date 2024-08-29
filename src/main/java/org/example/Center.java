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
import javax.swing.SwingUtilities;
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
    private final int TITLE_DEFAULT_LENGHT = 11;
    private final int TITLE_INDEX_OF_TRAJECT_NUMBER = 13;
    private final int BUTTON_INDEX_OF_TRAJECT_NUMBER = 11;
    private final int SIZE_OF_DATA = 7;

    private final JSplitPane center;
    private final JSplitPane left;
    private final JSplitPane right;
    private final JPanel rightTop;
    private final JPanel leftTopButtons;
    private final Preferences historyPref;

    private JTable table;
    private final JLabel filePathLabel;
    private final JTextArea text = new JTextArea();
    private int counter = 1;

    private final String[] colNames = {"T,c", "X,м", "Y,м", "Z,м", "Vx,м/c", "Vy,м/c", "Vz,м/c"};
    private final ArrayList<String> catalogButtonsPaths = new ArrayList<>();
    private static final ArrayList<JButton> catalogButtons = new ArrayList<>();

    protected void fillLeftTop(JButton button) {
        leftTopButtons.add(button);
        catalogButtons.add(button);
        leftTopButtons.repaint();
    }

    protected Center(Preferences historyPref) {
        center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        this.historyPref = historyPref;
        center.setResizeWeight(0.5);
        right.setResizeWeight(0.5);
        left.setResizeWeight(0.5);

        JLabel fileLabel = createLabel("Файл");
        filePathLabel = createLabel("Path");
        JLabel catalogLabel = createLabel("Каталог");
        JLabel plotLabel = createLabel("График");
        JLabel tableLabel = createLabel("Таблица");

        JPanel leftTop = new JPanel(new BorderLayout());
        leftTop.add(catalogLabel, BorderLayout.NORTH);
        leftTopButtons = new JPanel();
        leftTopButtons.setLayout(new BoxLayout(leftTopButtons, BoxLayout.Y_AXIS));
        leftTop.add(new JScrollPane(leftTopButtons), BorderLayout.CENTER);

        left.setTopComponent(leftTop);
        JPanel leftBottom = new JPanel(new BorderLayout());
        JPanel leftBottomLabel = new JPanel(new BorderLayout());
        leftBottomLabel.add(fileLabel, BorderLayout.NORTH);
        leftBottomLabel.add(filePathLabel, BorderLayout.SOUTH);
        leftBottom.add(leftBottomLabel, BorderLayout.NORTH);
        text.setEditable(false);
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        leftBottom.add(new JScrollPane(text), BorderLayout.CENTER);
        left.setBottomComponent(leftBottom);

        rightTop = new JPanel(new BorderLayout());
        rightTop.add(tableLabel, BorderLayout.NORTH);
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

    protected void openAction(String string, String[][] tableData, String path, Top top, int i) {
        top.setTitle("Траектории - " + i);
        filePathLabel.setText(path);
        filePathLabel.revalidate();
        filePathLabel.repaint();
        text.setText(string);
        if (rightTop.getComponentCount() > 1) {
            rightTop.remove(1);
        }
        table = new JTable(tableData, colNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        rightTop.add(new JScrollPane(table), BorderLayout.CENTER);
        rightTop.revalidate();
        rightTop.repaint();
    }


    protected void setDividerPos(boolean set, Preferences settingsPref) {
        if (set) {
            settingsPref.putDouble("center", (double) center.getDividerLocation() / center.getWidth());
            settingsPref.putDouble("left", (double) left.getDividerLocation() / left.getHeight());
            settingsPref.putDouble("right", (double) right.getDividerLocation() / right.getHeight());
            showMessageDialog(null, "Настройки сохранены!");
        } else {
            SwingUtilities.invokeLater(() -> {
                center.setDividerLocation(settingsPref.getDouble("center", 0.5));
                left.setDividerLocation(settingsPref.getDouble("left", 0.5));
                right.setDividerLocation(settingsPref.getDouble("right", 0.5));
            });
        }
    }


    private JLabel createLabel(String text) {
        JLabel jlabel = new JLabel(text, SwingConstants.CENTER);
        jlabel.setFont(Appearance.getArialBold());
        jlabel.setBackground(Appearance.getGrayColor());
        jlabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        jlabel.setPreferredSize(new Dimension(0, 30));
        jlabel.setMaximumSize(new Dimension(2000, 30));
        jlabel.setMinimumSize(new Dimension(80, 30));
        jlabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return jlabel;
    }


    protected void clearAll(Top top) {
        for (JButton button : catalogButtons) {
            leftTopButtons.remove(button);
        }
        leftTopButtons.repaint();
        catalogButtons.clear();
        catalogButtonsPaths.clear();
        if (rightTop.getComponentCount() > 1) {
            rightTop.remove(1);
        }
        text.setText("");
        counter = 1;
        filePathLabel.setText("Path");
        top.setTitle("Траектории");
        top.updateHistoryMenu(this);
    }

    protected void setCounter(int i) {
        counter += i;
    }

    protected JSplitPane getJSplitPane() {
        return center;
    }

    protected void openFile(File file, Top top) {
        if (file == null) {
            return;
        }
        if (isOpenFile(file)) {
            showMessageDialog(null, "Эта траектория уже открыта!");
            return;
        }

        JButton catalogButton = new JButton("Траектория " + counter);
        catalogButton.setPreferredSize(new Dimension(0, 30));
        catalogButton.setMaximumSize(new Dimension(2000, 30));
        catalogButton.setMinimumSize(new Dimension(80, 30));
        catalogButton.setBackground(Color.GRAY);
        String string;

        try {
            string = String.join("\n ", readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            showMessageDialog(null, "Файл не соответствует .txt!");
            ex.printStackTrace();
            return;
        }

        ArrayList<String> tableRows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = reader.readLine();
            while (str != null) {
                tableRows.addAll(Arrays.asList(str.split(" {2}")));
                str = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String[][] tableData = new String[tableRows.size() / SIZE_OF_DATA][SIZE_OF_DATA];
        for (int i = 0; i < tableRows.size() / SIZE_OF_DATA; i++) {
            for (int j = 0; j < SIZE_OF_DATA; j++) {
                tableData[i][j] = tableRows.get(i * SIZE_OF_DATA + j);
            }
        }
        int i = counter;
        catalogButton.addActionListener(e -> this.openAction(string, tableData, file.getAbsolutePath(), top, i));
        this.setCounter(1);
        this.fillLeftTop(catalogButton);
        leftTopButtons.revalidate();
        top.saveHistory(file, this.historyPref);
        top.updateHistoryMenu(this);
        catalogButtonsPaths.add(file.getAbsolutePath());

        top.updateCloseMenu(catalogButtons, catalogButtonsPaths, this);
    }


    protected void closeAction(JButton button, int j, Top top, String catalogButtonPath) {
        if (catalogButtons.size() == 1) {
            clearAll(top);
        } else {
            if (top.getTitle().length() > TITLE_DEFAULT_LENGHT) {
                if (Integer.parseInt(top.getTitle().substring(TITLE_INDEX_OF_TRAJECT_NUMBER)) == j) {
                    text.setText("");
                    top.setTitle("Траектории");
                    filePathLabel.setText("Path");
                    if (rightTop.getComponentCount() > 1)
                        rightTop.remove(1);
                }
            }
            leftTopButtons.remove(button);
            catalogButtons.remove(button);
            catalogButtonsPaths.remove(catalogButtonPath);
            leftTopButtons.repaint();
            leftTopButtons.revalidate();
            counter = Integer.parseInt(catalogButtons.get(catalogButtons.size() - 1).getText().substring(BUTTON_INDEX_OF_TRAJECT_NUMBER)) + 1;


        }

    }


    private boolean isOpenFile(File file) {
        for (String catalogButtonsPath : catalogButtonsPaths) {
            if (Objects.equals(catalogButtonsPath, file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }


}
