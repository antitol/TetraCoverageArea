package tetracoveragearea.gui.panels.mapPanels;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Панель взаимодействия с картой
 *
 * Created by anatoliy on 17.02.17.
 */
public class MapGuiPanel extends JPanel {

    public static final Logger log = Logger.getLogger(MapGuiPanel.class);

    private JToggleButton enablePointsButton;
    private JToggleButton enablePolygonsButton;
    private JToggleButton enableOuterShape;
    private JButton selectPoints;
    private JButton flushBuffer;

    public MapGuiPanel() {

        setLayout(new MigLayout());

        setPreferredSize(new Dimension(300, 300));

        enablePointsButton = new JToggleButton("Точки покрытия");
        enablePointsButton.setUI(GuiComponents.getToggleButtonGreenUI());
        enablePointsButton.addActionListener(
                e -> {
                    boolean selected = ((JToggleButton) e.getSource()).isSelected();
                    if (selected) {
                        if (!GeometryStore.getInstance().getGeometryListeners().contains(MapApplet.getInstance().getMap())) {
                            GeometryStore.getInstance().addGeometryListener(MapApplet.getInstance().getMap());
                            MapApplet.getInstance().getMap().setPoints(GeometryStore.getInstance().getPoints());
                        }
                    } else {
                        if (!enablePolygonsButton.isSelected()) {
                            GeometryStore.getInstance().removeGeometryListener(MapApplet.getInstance().getMap());
                        }
                    }
                    MapApplet.getInstance().getMap().showPoints(selected);
                }
        );

        enablePolygonsButton = new JToggleButton("Карта покрытия");
        enablePolygonsButton.setUI(GuiComponents.getToggleButtonGreenUI());

        enablePolygonsButton.addActionListener(

                e -> {
                    {
                        boolean selected = ((JToggleButton) e.getSource()).isSelected();
                        if (selected) {
                            if (!GeometryStore.getInstance().getGeometryListeners().contains(MapApplet.getInstance().getMap())) {
                                GeometryStore.getInstance().addGeometryListener(MapApplet.getInstance().getMap());
                                MapApplet.getInstance().getMap().setTriangles(GeometryStore.getInstance().getTriangles());
                            }
                        } else {
                            if (!enablePointsButton.isSelected()) {
                                GeometryStore.getInstance().removeGeometryListener(MapApplet.getInstance().getMap());
                            }
                        }
                        MapApplet.getInstance().getMap().showDelaunayTriangles(((JToggleButton) e.getSource()).isSelected());
                    }
                }
        );

        JPanel panel = new JPanel();
        panel.setSize(100, 100);

        enableOuterShape = new JToggleButton("Внешняя граница");
        enableOuterShape.setUI(GuiComponents.getToggleButtonGreenUI());
        enableOuterShape.addActionListener(e ->

            MapApplet.getInstance().getMap().showOuterShape(enableOuterShape.isSelected())
        );

        selectPoints = new JButton("Выделить область");
        selectPoints.addActionListener(e -> {

            if (GeometryStore.getInstance().getFilterPoints().size() > 0) {
                List<Point> points = new ArrayList<Point>(GeometryStore.getInstance().getPoints());
                points.removeAll(GeometryStore.getInstance().getFilterPoints());
                GeometryStore.getInstance().setPoints(points);
            }
        });

        flushBuffer = new JButton("Очистить буфер");
        flushBuffer.addActionListener(
                e -> GeometryStore.getInstance().clear());

        List<JComponent> components = Arrays.asList(
                enablePointsButton, enablePolygonsButton, enableOuterShape, selectPoints, flushBuffer
        );

        components.forEach(c -> add(c, "w 100%, wrap"));
    }
}
