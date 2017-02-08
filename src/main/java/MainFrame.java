import Testing.GpsTest;
import entities.GpsPoint;
import entities.Rssi;
import jssc.SerialPortException;
import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;
import serialDao.SerialTestDao;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by anatoliy on 16.01.17.
 */
public class MainFrame extends JFrame {

    private JComboBox portBox = new JComboBox(SerialPortList.getPortNames());
    private JToggleButton connectButton;
    private JTextField meanFieldLat;
    private JTextField meanFieldLong;
    private JTextField sdFieldLat;
    private JTextField sdFieldLong;
    private JTextField amountPointsLat;
    private JButton fillRandomButton;
    private JButton addPointsButton;
    private JButton clearTableButton;

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

        meanFieldLat = new JTextField("54.977");
        meanFieldLong = new JTextField("73.433");
        sdFieldLat = new JTextField("0.05");
        sdFieldLong = new JTextField("0.08");
        amountPointsLat = new JTextField("1000");

        addPointsButton = new JButton("Add points");
        addPointsButton.addActionListener(e ->
        {
            try {

                ArrayList<GpsPoint> points = GpsTest.getGetRandomGpsPointList(
                        Double.parseDouble(meanFieldLat.getText()),
                        Double.parseDouble(sdFieldLat.getText()),
                        Double.parseDouble(meanFieldLong.getText()),
                        Double.parseDouble(sdFieldLong.getText()),
                        Integer.parseInt(amountPointsLat.getText())
                );

                double centerLat = GpsTest.getNormalLat().getMean();
                double centerLong = GpsTest.getNormalLong().getMean();

                for (int i = 0; i < points.size(); i++) {
                    GpsPoint point =  points.get(i);

                    Rssi rssi = new Rssi(Optional.of(
                            (int) (30 + 50 * (
                                    Math.pow(Math.abs(centerLat - point.getLatitude()), 2)/GpsTest.getNormalLat().getStandardDeviation() +
                                    Math.pow(Math.abs(centerLong - point.getLongitude()), 2)/GpsTest.getNormalLong().getStandardDeviation()
                    ))));

                    SerialTestDao.getInstance().addGpsWithRssiPoint(point, rssi);
                }
            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this, "Вводи цифры, сложно догадаться чтоли?");

            }
        });

        fillRandomButton = new JButton("Fill random values");
        fillRandomButton.addActionListener(e -> {

            meanFieldLat.setText(String.format(String.valueOf(90*Math.random()), "%.3d"));
            meanFieldLong.setText(String.format(String.valueOf(180*Math.random()), "%.3d"));
            sdFieldLat.setText(String.format(String.valueOf(1*Math.random()), "%.5d"));
            sdFieldLong.setText(String.format(String.valueOf(1*Math.random()), "%.5d"));
            amountPointsLat.setText(String.valueOf((int) (10000*Math.random())));
            }
        );

        clearTableButton = new JButton("Clear table");
        clearTableButton.addActionListener(e ->
                SerialTestDao.getInstance().clearGpsTable()
        );

        LayoutManager layout = new MigLayout();
        setLayout(layout);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 350);

        add(portBox, "w 100% ,wrap");
        add(connectButton, "w 100%, wrap");
        add(meanFieldLat, "w 100% ,wrap");
        add(meanFieldLong, "w 100% ,wrap");
        add(sdFieldLat, "w 100% ,wrap");
        add(sdFieldLong, "w 100% ,wrap");
        add(amountPointsLat, "w 100% ,wrap");
        add(fillRandomButton, "w 100% ,wrap");
        add(addPointsButton, "w 100% ,wrap");
        add(clearTableButton, "w 100% ,wrap");
        setVisible(true);
    }


}
