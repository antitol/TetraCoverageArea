package tetracoveragearea.gui.panels.devicePanels.telnetGui;

import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by anatoliy on 22.05.17.
 */
public class ConnectButtonColumn extends DefaultCellEditor implements ActionListener {

    private JToggleButton connectButton = new JToggleButton();

    public ConnectButtonColumn(JCheckBox checkBox) {
        super(checkBox);
        connectButton.setOpaque(true);
        connectButton.setUI(GuiComponents.getToggleButtonGreenUI());
        connectButton.setText("Подключение");
        connectButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        connectButton.setText(connectButton.isSelected() ? "Отключение" : "Подключение");
        fireEditingStopped();
    }

    @Override
    public Object getCellEditorValue() {
        return connectButton.isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        connectButton.setSelected((Boolean) value);
        return connectButton;
    }
}
