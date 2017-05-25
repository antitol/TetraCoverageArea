package tetracoveragearea.gui.panels.devicePanels.telnetGui;

import tetracoveragearea.common.telnet.BSTelnetManager;
import tetracoveragearea.common.telnet.BStation;
import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anatoliy on 22.05.17.
 */
public class TelnetTableModel extends AbstractTableModel {

    public TelnetTableModel() {
        for (BStation bs : BStation.values()) {
            ipAddresses.add(bs.getAddress());
        }

        connections.addAll(Arrays.asList(false, false, false, false, false));
    }

    List<String> ipAddresses = new ArrayList<>();

    List<Boolean> connections = new ArrayList<>();

    HashMap<BStation, BSTelnetManager> telnetClients = new HashMap<>();

    @Override
    public int getRowCount() {
        return ipAddresses.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "IP-адрес";
            case 1: return "Подключение";
            default: return "";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return ipAddresses.get(rowIndex);
            case 1: return connections.get(rowIndex);
            default: return null;
        }
    }

    public void addRow() {
        ipAddresses.add("10.3.101.1");
        connections.add(false);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        ipAddresses.remove(row);
        connections.remove(row);
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case 0:
                ipAddresses.set(rowIndex, aValue.toString());
                break;
            case 1:
                if ((boolean) aValue != connections.get(rowIndex)) {
                    connections.set(rowIndex, (boolean) aValue);
                    telnetConnect(rowIndex, BStation.getByAddress(ipAddresses.get(rowIndex)), connections.get(rowIndex));
                }
                break;
            default: break;
        }

        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    public void telnetConnect(int rowIndex, BStation bStation, boolean connect) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (connect) {
                    telnetClients.put(bStation, new BSTelnetManager(bStation));
                    if (telnetClients.get(bStation).startMoniting()) {

                    } else {
                        GuiComponents.showInformationPane(null, "Ошибка подключения к базовой станции " + bStation.getAddress());
                        setValueAt(Boolean.valueOf(false), rowIndex, 1);
                    }
                } else {
                    telnetClients.get(bStation).stopMonitoring();
                    telnetClients.remove(bStation);
                }
            }
        });
    }
}
