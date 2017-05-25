package tetracoveragearea.gui.panels.devicePanels.telnetGui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anatoliy on 20.05.17.
 */
public class BaseStationPanel extends SubPanel {

    public static final Logger log = Logger.getLogger(BaseStationPanel.class);

    TelnetTableModel telnetTableModel = new TelnetTableModel();
    private JTable telnetTable = new JTable(telnetTableModel);

    private JButton addRow = new JButton();
    private JButton removeRow = new JButton();

    public BaseStationPanel() {

        setName("Telnet");

        setLayout(new MigLayout());

        telnetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        telnetTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        telnetTable.getColumnModel().getColumn(1).setPreferredWidth(160);

        ConnectButtonColumn buttonColumn = new ConnectButtonColumn(new JCheckBox());
        telnetTable.getColumnModel().getColumn(1).setCellEditor(buttonColumn);
        telnetTable.getColumnModel().getColumn(1).setCellRenderer(new ConnectButtonRenderer());

        addRow.setText("Добавить");
        removeRow.setText("Удалить");

        addRow.addActionListener(e -> telnetTableModel.addRow());
        removeRow.addActionListener(e -> telnetTableModel.removeRow(telnetTable.getSelectedRow()));

        JScrollPane scrollPane = new JScrollPane(telnetTable);
        scrollPane.setPreferredSize(new Dimension(280, 250));

        add(scrollPane, "span 2, wrap");
        add(addRow, "w 50%");
        add(removeRow, "w 50%, wrap");
    }
}
