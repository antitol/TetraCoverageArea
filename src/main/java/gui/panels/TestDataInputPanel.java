package gui.panels;

import common.entities.serialPart.GpsPoint;
import common.entities.serialPart.Rssi;
import common.entities.visualPart.TriangleMarkerRssi;
import serialDao.SerialTestDao;
import testValues.GpsTestVelues;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Панель для ввода тестовых данных в базу данных
 *
 * Created by anatoliy on 17.02.17.
 */
public class TestDataInputPanel extends JPanel {

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

    public TestDataInputPanel() {

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
            java.util.List<TriangleMarkerRssi> triangles = SerialTestDao.getInstance().getDelauneyTriangles();
            triangles = triangles.get(0).centropolate();
        });

        Arrays.asList(meanFieldLat, meanFieldLong,
                sdFieldLat, sdFieldLong, amountPointsLat, minSlider,
                fadeSlider, fillRandomButton, addPointsButton, clearTableButton)
                .forEach(component -> add(component, "w 100%, wrap"));
    }
}
