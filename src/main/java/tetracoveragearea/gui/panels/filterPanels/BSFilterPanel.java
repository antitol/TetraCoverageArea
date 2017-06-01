package tetracoveragearea.gui.panels.filterPanels;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.telnet.BStation;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 23.05.17.
 */
public class BSFilterPanel extends SubPanel {

    private JComboBox<BStation> bsComboBox;

    private JButton confirmButton = new JButton("Применить");
    private JButton resetButton = new JButton("Сбросить");


    public BSFilterPanel() {

        setName("Базовая станция");

        bsComboBox = new JComboBox<>();

        confirmButton.addActionListener(e -> {
            Filter.setBStation(Optional.of((BStation) bsComboBox.getSelectedItem()));
            GeometryStore.getInstance().filter();
        });

        resetButton.addActionListener(e -> {
            Filter.setBStation(Optional.empty());
            GeometryStore.getInstance().filter();
        });

        add(bsComboBox, "wrap, w 100%");
        add(confirmButton);
        add(resetButton, "wrap");
    }

    @Override
    public void onInvoke() {

        Set<BStation> integerSet = GeometryStore.getInstance().getPoints().parallelStream().collect(Collectors.groupingBy(Point::getBStation)).keySet();

        bsComboBox.removeAllItems();
        for (BStation bStation : integerSet) {
            bsComboBox.addItem(bStation);
        }

        revalidate();
        repaint();
    }
}
