import jssc.SerialPortException;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anatoliy on 16.01.17.
 */
public class MainFrame extends JFrame {

    private JComboBox portBox = new JComboBox(SerialPortList.getPortNames());
    private JToggleButton connectButton;

    public MainFrame() {

        super("ComTest");


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

        LayoutManager layout = new net.miginfocom.swing.MigLayout();
        setLayout(layout);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(200, 100);

        add(portBox, "w 100% ,wrap");
        add(connectButton, "w 100%");
        setVisible(true);
    }


}
