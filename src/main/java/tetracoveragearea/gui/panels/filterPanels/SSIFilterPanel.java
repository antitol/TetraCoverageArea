package tetracoveragearea.gui.panels.filterPanels;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 25.05.17.
 */
public class SSIFilterPanel extends SubPanel {

    private JComboBox<Integer> ssiComboBox;

    private JButton confirmButton = new JButton("Применить");
    private JButton resetButton = new JButton("Сбросить");

    public SSIFilterPanel() {
        setName("SSI");

        ssiComboBox = new JComboBox<>();

        confirmButton.addActionListener(e -> {
                Filter.setSsi(OptionalInt.of((int) ssiComboBox.getSelectedItem()));
                GeometryStore.getInstance().filter();
        });

        resetButton.addActionListener(e -> {
            Filter.setSsi(OptionalInt.empty());
            GeometryStore.getInstance().filter();
        });

        add(ssiComboBox, "wrap, w 100%");
        add(confirmButton);
        add(resetButton, "wrap");
    }

    @Override
    public void onInvoke() {
        Set<Integer> integerSet = GeometryStore.getInstance().getPoints().parallelStream().collect(Collectors.groupingBy(Point::getSsi)).keySet();

        ArrayList<Integer> ssis = new ArrayList<>(integerSet);
        Collections.sort(ssis, Integer::compareTo);

        ssiComboBox.removeAllItems();
        for (Integer ssi : ssis) {
            ssiComboBox.addItem(ssi);
        }

        revalidate();
        repaint();
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
    }
}
