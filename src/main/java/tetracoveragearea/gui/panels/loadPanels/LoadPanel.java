package tetracoveragearea.gui.panels.loadPanels;

import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.parserTools.DocumentParser;
import tetracoveragearea.common.parserTools.GeojsonParser;
import tetracoveragearea.common.parserTools.KmlParser;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.primitives.PrimaryPanel;

import javax.swing.*;
import java.io.File;

/**
 * Created by anatoliy on 23.05.17.
 */
public class LoadPanel extends PrimaryPanel {

    private JButton loadNewSetButton = new JButton("Новый набор");
    private JButton loadAdditionalSetButton = new JButton("Дополнительный набор");

    private KmlParser kmlParser = new KmlParser();
    private GeojsonParser geojsonParser = new GeojsonParser();

    DocumentParser parser;

    public LoadPanel() {

        loadNewSetButton.addActionListener(e -> {
            writePointFromFile(false);
        });

        loadAdditionalSetButton.addActionListener(e -> {
            writePointFromFile(true);
        });

        add(loadNewSetButton, "wrap, w 100%");
        add(loadAdditionalSetButton, "wrap, w 100%");
    }

    public void writePointFromFile(boolean additional) {
        JFileChooser fileChooser = GuiComponents.getExportFileChooser();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            String filename = openFile.getName();

            switch (filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()) {
                case "kml":
                    parser = kmlParser;
                    break;
                case "geojson":
                    parser = geojsonParser;
                    break;
                default:
                    GuiComponents.showInformationPane(null, "Неизвестный формат файла");
            }
            if (additional) {
                writeAdditionalSet(openFile, parser);
            } else {
                writeNewSet(openFile, parser);
            }
        }
    }

    /**
     * Запись массива точек из хранилища в файл с помощью парсера
     * @param file - сохраняемый файл
     * @param parser - парсер
     */
    public void writeAdditionalSet(File file, DocumentParser parser) {
        try {
            GeometryStore.getInstance().addPoints(
                    parser.parse(file)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            GuiComponents.showInformationPane(null, "Ошибка чтения файла");
        }
    }

    /**
     * Новый набор точек из хранилища
     * @param file
     * @param parser
     */
    public void writeNewSet(File file, DocumentParser parser) {
        try {
            GeometryStore.getInstance().setPoints(
                    parser.parse(file)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            GuiComponents.showInformationPane(null, "Ошибка чтения файла");
        }
    }

    @Override
    public void onInvoke() {

    }

    @Override
    public void onRevoke() {

    }
}
