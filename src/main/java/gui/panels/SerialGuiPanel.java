package gui.panels;

import common.SerialTest;
import jssc.SerialPortException;
import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Панель взаимодействия с серийным портом
 *
 * Created by anatoliy on 17.02.17.
 */
public class SerialGuiPanel extends JPanel {

    private JLabel portStateLabel = new JLabel("Устройство не подключено");
    private JComboBox portBox = new JComboBox(SerialPortList.getPortNames());
    private JToggleButton connectButton;

    public SerialGuiPanel() {

        setLayout(new MigLayout());

        connectButton = new JToggleButton("Connect");
        connectButton.addActionListener(e -> {

                    if (connectButton.isSelected()) {

                        if (SerialTest.getInstance().initPort(portBox.getSelectedItem().toString())) {
                            portStateLabel.setText("Устройство ");
                        }
                        SerialTest.getInstance().startTimers(5000, 5000, 5000);

                        connectButton.setText("Disconnect");

                    } else {
                        connectButton.setText("Connect");
                        try {
                            SerialTest.getInstance().stopTimers();
                            SerialTest.getInstance().getSerialPort().closePort();
                        } catch (SerialPortException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
        );

        portBox.addActionListener(e -> {
                    Object selected = portBox.getSelectedItem();
                    portBox.setModel(new DefaultComboBoxModel(SerialPortList.getPortNames()));
                    portBox.setSelectedItem(selected);
                }
        );

        add(portStateLabel, "w 100%, wrap");
        add(portBox, "w 100%, wrap");
        add(connectButton, "w 100%, wrap");
    }
}
