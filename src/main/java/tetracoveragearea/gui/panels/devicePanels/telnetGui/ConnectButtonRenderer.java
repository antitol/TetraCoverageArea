package tetracoveragearea.gui.panels.devicePanels.telnetGui;

import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by anatoliy on 22.05.17.
 */
public class ConnectButtonRenderer extends JToggleButton implements TableCellRenderer {

    public ConnectButtonRenderer() {
        setUI(GuiComponents.getToggleButtonGreenUI());
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean selected = (boolean) value;
        if (selected) {
            setText("Отключение");
        } else {
            setText("Подключение");
        }

        setSelected(selected);
        return this;
    }
}
