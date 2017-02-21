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

    private JComboBox portBox = new JComboBox(SerialPortList.getPortNames());
    private JToggleButton connectButton;

    public SerialGuiPanel() {

        setLayout(new MigLayout());

        connectButton = new JToggleButton("Connect");
        connectButton.addActionListener(e -> {

                    if (connectButton.isSelected()) {

                        SerialTest.initPort(portBox.getSelectedItem().toString());
                        SerialTest.startTimers(3100, 3100);

                        connectButton.setText("Disconnect");

                    } else {
                        connectButton.setText("Connect");
                        try {
                            SerialTest.stopTimers();
                            SerialTest.getSerialPort().closePort();
                        } catch (SerialPortException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
        );

        add(portBox, "w 100%, wrap");
        add(connectButton, "w 100%, wrap");
    }
}
