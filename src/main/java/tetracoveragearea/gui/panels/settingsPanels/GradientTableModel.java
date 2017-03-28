package tetracoveragearea.gui.panels.settingsPanels;

import javax.swing.table.AbstractTableModel;

/**
 * Created by anatoliy on 28.03.17.
 */
public class GradientTableModel extends AbstractTableModel {

    public static GradientTableModel instance = new GradientTableModel();

    public static GradientTableModel getInstance() {
        return instance;
    }



    public GradientTableModel() {
    }

    @Override
    public int getRowCount() {
        return 2;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
