package gui;

import common.SerialTest;
import common.entities.serialPart.GpsPoint;
import common.entities.serialPart.Rssi;
import common.entities.visualPart.TriangleMarkerRssi;
import jssc.SerialPortException;
import jssc.SerialPortList;
import net.miginfocom.swing.MigLayout;
import serialDao.SerialTestDao;
import testValues.GpsTestVelues;
import gui.applet.MapApplet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private JSlider minSlider;
    private JSlider fadeSlider;
    private JButton fillRandomButton;
    private JButton addPointsButton;
    private JButton clearTableButton;

    private final MapApplet mapApplet = new MapApplet();



    public MainFrame() {

        super("ComTest");

        JPanel appletPanel = new JPanel(new MigLayout());
        appletPanel.setSize(800, 600);

        mapApplet.init();
        mapApplet.setSize(800,600);

        appletPanel.add(mapApplet);

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

        meanFieldLat = new JTextField("54.93");
        meanFieldLong = new JTextField("73.433");
        sdFieldLat = new JTextField("0.05");
        sdFieldLong = new JTextField("0.08");
        amountPointsLat = new JTextField("1000");
        minSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 30);
        minSlider.setPaintLabels(true);
        minSlider.setMajorTickSpacing(20);
        fadeSlider = new JSlider(SwingConstants.HORIZONTAL, 0 , 150, 50);
        fadeSlider.setPaintLabels(true);
        fadeSlider.setMajorTickSpacing(25);

        addPointsButton = new JButton("Add points");
        addPointsButton.addActionListener(e ->
        {
            try {

                ArrayList<GpsPoint> points = GpsTestVelues.getGetRandomGpsPointList(
                        Double.parseDouble(meanFieldLat.getText()),
                        Double.parseDouble(sdFieldLat.getText()),
                        Double.parseDouble(meanFieldLong.getText()),
                        Double.parseDouble(sdFieldLong.getText()),
                        Integer.parseInt(amountPointsLat.getText())
                );

                double centerLat = GpsTestVelues.getNormalLat().getMean();
                double centerLong = GpsTestVelues.getNormalLong().getMean();

                for (int i = 0; i < points.size(); i++) {
                    GpsPoint point =  points.get(i);

                    Rssi rssi = new Rssi(Optional.of(
                            (int) (minSlider.getValue() + fadeSlider.getValue() * (
                                    Math.pow(Math.abs(centerLat - point.getLatitude()), 2)/ GpsTestVelues.getNormalLat().getStandardDeviation() +
                                    Math.pow(Math.abs(centerLong - point.getLongitude()), 2)/ GpsTestVelues.getNormalLong().getStandardDeviation()
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
            minSlider.setValue((int) (100*Math.random()));
            fadeSlider.setValue((int) (150*Math.random()));
            }
        );

        clearTableButton = new JButton("Clear table");
        clearTableButton.addActionListener(e -> {
            java.util.List<TriangleMarkerRssi> triangles = SerialTestDao.getInstance().getTriangles();
            triangles = triangles.get(0).centropolate();
        });

        LayoutManager layout = new MigLayout();
        setLayout(layout);
        setSize(1100, 650);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(appletPanel);

        /* Все GUI компоненты */
        List<JComponent> components = Arrays.asList(
                portBox, connectButton, meanFieldLat, meanFieldLong,
                sdFieldLat, sdFieldLong, amountPointsLat, minSlider,
                fadeSlider, fillRandomButton, addPointsButton, clearTableButton
        );

        JPanel guiPanel = packGuiPanel(components);

        add(guiPanel);
        setVisible(true);
    }

    private JPanel packGuiPanel(List<JComponent> components) {

        JPanel guiPanel = new JPanel(new MigLayout());

        components.forEach(c -> guiPanel.add(c, "w 100% ,wrap"));
        return guiPanel;
    }
}
