package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.parserTools.DocumentParser;
import tetracoveragearea.common.parserTools.GeojsonParser;
import tetracoveragearea.common.parserTools.KmlParser;
import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 15.03.17.
 */
public class MenuPanel extends JPanel {

    private JFileChooser fileChooser = new JFileChooser("src/main/resources/export") {
        @Override
        public File getSelectedFile() {
            try {
                if (super.getSelectedFile().getName().indexOf('.') == -1) {
                    setSelectedFile(new File(super.getSelectedFile().getPath().concat(fileChooser.getFileFilter().getDescription())));
                }
            } catch (NullPointerException ex) {
                return null;
            }

            return super.getSelectedFile();
        }


        @Override
        public void approveSelection(){
            File f = getSelectedFile();
            if(f.exists() && getDialogType() == SAVE_DIALOG){
                int result = JOptionPane.showConfirmDialog(this,"Файл существует, перезаписать?","Подтвердите действие",JOptionPane.YES_NO_CANCEL_OPTION);
                switch(result){
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                }
            }
            super.approveSelection();
        }
    };

    private KmlParser kmlParser = new KmlParser();
    private GeojsonParser geojsonParser = new GeojsonParser();

    private JToggleButton deviceButton;
    private JToggleButton databaseButton;
    private JToggleButton loadButton;
    private JToggleButton saveButton;
    private JToggleButton settingsButton;
    private JToggleButton filterButton;
    private JToggleButton testButton;

    private ButtonGroup buttonGroup;

    public MenuPanel() {

        fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".kml", "kml"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".geojson", "geojson"));

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


        loadButton.addActionListener(e -> {

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();
                String filename = openFile.getName();

                switch (filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()) {
                    case "kml":
                        writeFile(openFile, kmlParser);
                        break;
                    case "geojson":
                        writeFile(openFile, geojsonParser);
                        break;
                    default:
                        GuiComponents.showInformationPane(null, "Неизвестный формат файла");
                }
            }
        });

        saveButton.addActionListener(e -> {

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                String filename = saveFile.getName();

                switch (filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()) {
                    case "kml":
                        kmlParser.write(saveFile, GeometryStore.getInstance().getPoints());
                        break;
                    case "geojson":
                        geojsonParser.write(saveFile, GeometryStore.getInstance().getPoints());
                        break;
                    default:
                        GuiComponents.showInformationPane(null, "Неизвестный формат файла");
                }
            }
        });
    }

    /**
     * Запись массива точек из хранилища в файл с помощью парсера
     * @param file - сохраняемый файл
     * @param parser - парсер
     */
    public void writeFile(File file, DocumentParser parser) {
        try {
            GeometryStore.getInstance().setPoints(
                    parser.parse(file)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            GuiComponents.showInformationPane(null, "Ошибка чтения файла");
        }
    }
}
