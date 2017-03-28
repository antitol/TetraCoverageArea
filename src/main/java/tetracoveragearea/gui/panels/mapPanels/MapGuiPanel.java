package tetracoveragearea.gui.panels.mapPanels;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.components.GuiComponents;

import javax.swing.*;
import java.awt.*;
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
    private JButton flushBuffer;

    public MapGuiPanel() {

        setLayout(new MigLayout());

        setPreferredSize(new Dimension(300, 300));

        enablePointsButton = new JToggleButton("Точки покрытия");
        enablePointsButton.setUI(GuiComponents.getToggleButtonGreenUI());
        enablePointsButton.addActionListener(
                e -> MapApplet.getInstance().getMap().showPoints(((JToggleButton) e.getSource()).isSelected())
        );

        enablePolygonsButton = new JToggleButton("Карта покрытия");
        enablePolygonsButton.setUI(GuiComponents.getToggleButtonGreenUI());

        enablePolygonsButton.addActionListener(

                e -> {
                    MapApplet.getInstance().getMap().showDelaunayTriangles(((JToggleButton) e.getSource()).isSelected());
                }
        );

        JPanel panel = new JPanel();
        panel.setSize(100, 100);

        enableOuterShape = new JToggleButton("Внешняя граница");
        enableOuterShape.setUI(GuiComponents.getToggleButtonGreenUI());
        enableOuterShape.addActionListener(e ->

            MapApplet.getInstance().getMap().showOuterShape(enableOuterShape.isSelected())
        );

        flushBuffer = new JButton("Очистить буфер");
        flushBuffer.addActionListener(
                e -> GeometryStore.getInstance().clear());

        List<JComponent> components = Arrays.asList(
                enablePointsButton, enablePolygonsButton, enableOuterShape, flushBuffer
        );

        components.forEach(c -> add(c, "w 100%, wrap"));
    }
}
