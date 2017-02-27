package gui.panels;

import gui.applet.MapApplet;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import serialDao.SerialTestDao;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Панель взаимодействия с картой
 *
 * Created by anatoliy on 17.02.17.
 */
public class MapGuiPanel extends JPanel {

    public static final Logger log = Logger.getLogger(SerialTestDao.class);

    private JToggleButton enablePoints;
    private JToggleButton enablePolygons;

    private JLabel interpolationLabel;
    private JTextField interpolationAreaField;
    private JButton updatePolygons;

    private MetalToggleButtonUI greenSelectedUI = new MetalToggleButtonUI() {
        @Override
        protected Color getSelectColor() {
            return Color.green;
        }
    };

    public MapGuiPanel() {
        log.info("Я живой");

        setLayout(new MigLayout());

        enablePoints = new JToggleButton("Точки покрытия");
        enablePoints.setUI(greenSelectedUI);
        enablePoints.addActionListener(
                e -> {
                    if (enablePoints.isSelected()) {
                        MapApplet.getMap().setPoints(SerialTestDao.getInstance().getPoints());
                        MapApplet.getMap().enablePoints(true);
                    } else {
                        MapApplet.getMap().enablePoints(false);
                    }
                }
        );

        enablePolygons = new JToggleButton("Карта покрытия");
        enablePolygons.setUI(greenSelectedUI);
        enablePolygons.addActionListener(

                e -> {

                    if (enablePolygons.isSelected()) {
                        try {
                            MapApplet.getMap().getDelauneyTriangles();
                            log.info(MapApplet.getMap().getDelauneyTriangles().size());
                        } catch (Exception ex) {
                            MapApplet.getMap().setDelauneyTriangles(SerialTestDao.getInstance().getDelauneyTriangles());
                        }
                        MapApplet.getMap().enableDelaunayTriangles(true);
                    } else {
                        MapApplet.getMap().enableDelaunayTriangles(false);
                    }
                }
        );

        updatePolygons = new JButton("Обновить карту покрытия");
        updatePolygons.addActionListener(e -> {
                    MapApplet.getMap().enableDelaunayTriangles(false);
                    MapApplet.getMap().setDelauneyTriangles(
                        SerialTestDao.getInstance().getDelauneyTriangles());
                }
        );


        List<JComponent> components = Arrays.asList(
                enablePoints, enablePolygons, updatePolygons
        );

        components.forEach(c -> add(c, "w 100%, wrap"));

        log.info("Я закончил");
    }
}
