package tetracoveragearea.gui.panels.settingsPanels.gradient;

import tetracoveragearea.gui.tools.MultilayerGradient;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static tetracoveragearea.gui.tools.MultilayerGradient.*;

/**
 * Модель таблицы настройки градиента сигнала
 * Created by anatoliy on 28.03.17.
 */
public class GradientTableModel extends AbstractTableModel {

    public static GradientTableModel instance = new GradientTableModel();

    public static GradientTableModel getInstance() {
        return instance;
    }

    private MultilayerGradient multilayerGradient = new MultilayerGradient(
            new ArrayList<>(Arrays.asList(
                    new ColorLayer(Color.GREEN, 20f),
                    new ColorLayer(Color.YELLOW, 60f),
                    new ColorLayer(Color.RED, 100f)
            ))
    );

    private String[] columnNames = {"Цвет", "Уровень сигнала"};



    public GradientTableModel() {
    }

    @Override
    public int getRowCount() {
        return multilayerGradient.getLayers().size();

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ColorLayer currentLayer = multilayerGradient.getLayer(rowIndex);

        try {

            switch (columnIndex) {
                case 0:
                    return currentLayer.getColor();
                case 1:
                    return currentLayer.getPart();

                default:
                    return null;
            }
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case 0:
                multilayerGradient.getLayer(rowIndex).setColor((Color) aValue);
                break;
            case 1:
                multilayerGradient.getLayer(rowIndex).setPart(Float.parseFloat(aValue.toString()));
                multilayerGradient.sortLayers();
                break;

        }

        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public MultilayerGradient getMultilayerGradient() {
        return multilayerGradient;
    }

    public void setMultilayerGradient(MultilayerGradient multilayerGradient) {
        this.multilayerGradient = multilayerGradient;
    }
}
