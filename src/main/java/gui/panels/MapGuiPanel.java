package gui.panels;

import common.entities.visualPart.TriangleMarkerRssi;
import gui.applet.MapApplet;
import net.miginfocom.swing.MigLayout;
import serialDao.SerialTestDao;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Панель взаимодействия с картой
 *
 * Created by anatoliy on 17.02.17.
 */
public class MapGuiPanel extends JPanel {

    private JToggleButton enablePoints;
    private JComboBox enablePolygons;
    private JLabel interpolationLabel;
    private JTextField interpolationAreaField;
    private JButton interpolate;

    public MapGuiPanel() {
        setLayout(new MigLayout());

        enablePoints = new JToggleButton("Points: OFF");
        enablePoints.addActionListener(
                e -> {
                    if (enablePoints.isSelected()) {
                        enablePoints.setText("Points: ON");
                        MapApplet.getMap().setPoints(SerialTestDao.getInstance().getPoints());
                        MapApplet.getMap().enablePoints(true);
                    } else {
                        enablePoints.setText("Points: OFF");
                        MapApplet.getMap().enablePoints(false);
                    }
                }
        );

        enablePolygons = new JComboBox(new String[] {"OFF", "Delaunay", "Voronoi"});
        enablePoints.addChangeListener(
                e -> {
                    switch (enablePolygons.getSelectedIndex()) {
                        case 0:
                            // Удалить и полигоны и треугольники
                            break;
                        case 1:
                            // Включить треугольники, удалить полигоны
                            break;
                        case 2:
                            // Включить полигоны, удалить треугольники
                            break;
                        default:
                            // Do nothing
                            break;
                    }
                }
        );

        interpolationLabel = new JLabel("Enter interpolation area in km2");

        interpolationAreaField = new JTextField("1");

        interpolate = new JButton("Interpolate");
        interpolate.addActionListener(
                e -> {

                    List<TriangleMarkerRssi> list = SerialTestDao.getInstance().getDelauneyTriangles();
                    MapApplet.getMap().setDelauneyTriangles(list);

                    MapApplet.getMap().enableDelaunayTriangles(true);
                }
        );

        List<JComponent> components = Arrays.asList(
                enablePoints, enablePolygons, interpolationLabel, interpolationAreaField, interpolate
        );

        components.forEach(c -> add(c, "w 100%, wrap"));
    }
}
