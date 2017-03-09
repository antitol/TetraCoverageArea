package gui.panels;

import gui.applet.MapApplet;
import gui.components.GuiComponents;
import gui.dialogs.FilterDialog;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
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
    private JButton filtersButton;
    private JToggleButton enableOuterShape;

    public MapGuiPanel() {

        setLayout(new MigLayout());

        enablePointsButton = new JToggleButton("Точки покрытия");
        enablePointsButton.setUI(GuiComponents.getToggleButtonGreenUI());
        enablePointsButton.addActionListener(
                e -> {
                    MapApplet.getMap().showPoints(((JToggleButton) e.getSource()).isSelected());
                }
        );

        enablePolygonsButton = new JToggleButton("Карта покрытия");
        enablePolygonsButton.setUI(GuiComponents.getToggleButtonGreenUI());

        enablePolygonsButton.addActionListener(

                e -> {
                    MapApplet.getMap().showDelaunayTriangles(((JToggleButton) e.getSource()).isSelected());
                }
        );

        JPanel panel = new JPanel();
        panel.setSize(100, 100);

        filtersButton = new JButton("Фильтры");
        filtersButton.addActionListener(e -> {
            FilterDialog.getInstance().setVisible(true);

            // Некрасивый код здесь
            FilterDialog.getInstance().getTimeFilterPanel().timedateChanged();
        });

        enableOuterShape = new JToggleButton("Внешняя граница");
        enableOuterShape.setUI(GuiComponents.getToggleButtonGreenUI());
        enableOuterShape.addActionListener(e ->

            MapApplet.getMap().showOuterShape(enableOuterShape.isSelected())
        );

        List<JComponent> components = Arrays.asList(
                enablePointsButton, enablePolygonsButton, enableOuterShape, filtersButton
        );

        components.forEach(c -> add(c, "w 100%, wrap"));
    }
}
