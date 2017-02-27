package gui.panels;

import common.entities.serialPart.GpsPoint;
import gui.applet.MapApplet;
import gui.applet.map.ToolMarkers;
import net.miginfocom.swing.MigLayout;
import serialDao.SerialTestDao;
import testValues.GpsTestVelues;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;

/**
 * Панель для ввода тестовых данных в базу данных
 *
 * Created by anatoliy on 17.02.17.
 */
public class TestDataInputPanel extends JPanel {

    private JTextField sdFieldLat;
    private JTextField sdFieldLong;
    private JTextField amountPointsLat;
    private JSlider minSlider;
    private JSlider fadeSlider;
    private JButton fillRandomButton;
    private JButton addPointsButton;
    private JButton clearTableButton;

    public TestDataInputPanel() {

        setLayout(new MigLayout());

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
                        (double) (ToolMarkers.getInstence().getMouseClicked().getLocation().getLat()),
                        Double.parseDouble(sdFieldLat.getText()),
                        (double) (ToolMarkers.getInstence().getMouseClicked().getLocation().getLon()),
                        Double.parseDouble(sdFieldLong.getText()),
                        Integer.parseInt(amountPointsLat.getText())
                );

                double centerLat = GpsTestVelues.getNormalLat().getMean();
                double centerLong = GpsTestVelues.getNormalLong().getMean();

                for (int i = 0; i < points.size(); i++) {
                    GpsPoint point =  points.get(i);

                    OptionalInt rssi = OptionalInt.of(
                            (int) (minSlider.getValue() + fadeSlider.getValue() * (
                                    Math.pow(Math.abs(centerLat - point.getSignificantLatitude()), 2)/ GpsTestVelues.getNormalLat().getStandardDeviation() +
                                            Math.pow(Math.abs(centerLong - point.getSignificantLongitude()), 2)/ GpsTestVelues.getNormalLong().getStandardDeviation()
                            )));

                    SerialTestDao.getInstance().addGpsWithRssiPoint(point, rssi);

                }

                MapApplet.getMap().enablePoints(false);
                MapApplet.getMap().setPoints(SerialTestDao.getInstance().getPoints());
                MapApplet.getMap().enablePoints(true);
            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this, "Вводи цифры, сложно догадаться чтоли?");

            }
        });

        fillRandomButton = new JButton("Fill random values");
        fillRandomButton.addActionListener(e -> {

                    sdFieldLat.setText(String.format(String.valueOf(1*Math.random()), "%.5d"));
                    sdFieldLong.setText(String.format(String.valueOf(1*Math.random()), "%.5d"));
                    amountPointsLat.setText(String.valueOf((int) (10000*Math.random())));
                    minSlider.setValue((int) (100*Math.random()));
                    fadeSlider.setValue((int) (150*Math.random()));
                }
        );

        clearTableButton = new JButton("Clear table");
        clearTableButton.addActionListener(e -> {
            MapApplet.getMap().enablePoints(false);
            MapApplet.getMap().enableDelaunayTriangles(false);
            SerialTestDao.getInstance().clearGpsTable();
        });

        Arrays.asList(
                sdFieldLat, sdFieldLong, amountPointsLat, minSlider,
                fadeSlider, fillRandomButton, addPointsButton, clearTableButton)
                .forEach(component -> add(component, "w 100%, wrap")
        );
    }
}
