package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 15.03.17.
 */
public class MenuPanel extends JPanel {

    private JToggleButton deviceButton;
    private JToggleButton databaseButton;
    private JToggleButton loadButton;
    private JToggleButton saveButton;
    private JToggleButton settingsButton;
    private JToggleButton filterButton;
    private JToggleButton testButton;

    private ButtonGroup buttonGroup;

    public MenuPanel() {

        setLayout(new MigLayout("debug, insets 0, gapy 0, center"));

        deviceButton = new JToggleButton();
        databaseButton = new JToggleButton();
        loadButton = new JToggleButton();
        saveButton = new JToggleButton();
        settingsButton = new JToggleButton();
        filterButton = new JToggleButton();
        testButton = new JToggleButton();

        try {
            deviceButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/device.png")));
            deviceButton.setToolTipText("Устройства");

            databaseButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/db-settings.png")));
            databaseButton.setToolTipText("Устройства");

            loadButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/download.png")));
            loadButton.setToolTipText("Загрузка из внешних источников");

            saveButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/upload.png")));
            saveButton.setToolTipText("Сохранить во внешнем источнике");

            settingsButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/settings.png")));
            settingsButton.setToolTipText("Настройки");

            filterButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/filter.png")));
            filterButton.setToolTipText("Фильтры");

            testButton.setIcon(new ImageIcon(getClass().getClassLoader().
                    getResource("assets/test.png")));
            testButton.setToolTipText("Ввод тестовых данных");
        } catch (NullPointerException ex) {

            deviceButton.setText("Устройства");
            databaseButton.setText("Настройка БД");
            loadButton.setText("Загрузка");
            saveButton.setText("Сохранить");
            settingsButton.setText("Настройки");
            filterButton.setText("Фильтры");
            testButton.setText("Тест");
        }

        deviceButton.addActionListener(e -> MainContentPanel.getInstance().swtichDevicePanel());
        databaseButton.addActionListener(e -> MainContentPanel.getInstance().swtichDatabasePanel());
        filterButton.addActionListener(e -> MainContentPanel.getInstance().swtichFilterPanel());
        testButton.addActionListener(e -> MainContentPanel.getInstance().swtichTestPanel());
        settingsButton.addActionListener(e -> MainContentPanel.getInstance().swtichSettingsPanel());


        buttonGroup = new ButtonGroup();

        List<JToggleButton> buttonGroupList = Arrays.asList(deviceButton, databaseButton, loadButton, saveButton, settingsButton, filterButton, testButton);

        buttonGroupList.forEach(button -> {
//            button.setBackground(Color.white);
            buttonGroup.add(button);
            add(button, "wrap, w 100%");
        } );

    }
}
