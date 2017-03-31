package tetracoveragearea.gui.panels.filterPanels;

import com.jidesoft.swing.RangeSlider;
import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by anatoliy on 31.03.17.
 */
public class LocationFilterPanel extends SubPanel {

    private final int scale = (int) Math.pow(10, 4);

    private JButton confirmButton;
    private JButton resetButton;

    public LocationFilterPanel() {

        setName("Область");

        JPanel xPanel = getDimensionPanel("Широта", 54, 55);
        JPanel yPanel = getDimensionPanel("Долгота", 73, 74);
        JPanel rssiPanel = getDimensionPanel("Уровень сигнала", 0, 120);

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {});

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {});

        Arrays.asList(xPanel, yPanel, rssiPanel).forEach(component -> add(component, "span 2, wrap"));

        add(confirmButton);
        add(resetButton, "wrap");
    }

    public JPanel getDimensionPanel(String dimensionName, double from, double to) {

        JPanel dimensionPanel = new JPanel(new MigLayout("debug, insets 0, w 260"));
        JPanel activatingPanel = new JPanel(new MigLayout("debug, insets 0, w 260"));


        JCheckBox checkEnableDimensionBox = new JCheckBox(dimensionName);

        checkEnableDimensionBox.addActionListener(e -> {

            for (Component component : activatingPanel.getComponents()) {
                component.setEnabled(checkEnableDimensionBox.isSelected());
            }

        });

        JLabel fromToLabel = new JLabel("От/до: ");

        JTextField fromValueField = new JTextField();
        JTextField toValueField = new JTextField();

        fromValueField.setText(String.format("%.4f", from));
        toValueField.setText(String.format("%.4f", to));

        RangeSlider valueSlider = new RangeSlider((int) (from * scale), (int) (to * scale));

        valueSlider.setLowValue(valueSlider.getMinimum());
        valueSlider.setHighValue(valueSlider.getMaximum());

        valueSlider.addChangeListener(e -> {
            fromValueField.setText(String.format("%d.%04d", valueSlider.getLowValue() / scale, valueSlider.getLowValue() % scale));
            toValueField.setText(String.format("%d.%04d", valueSlider.getHighValue() / scale, valueSlider.getHighValue() % scale));
        });

        Arrays.asList(fromToLabel, fromValueField, toValueField, valueSlider)
                .forEach(component -> {
                    activatingPanel.add(component, "w 100%, wrap");
                    component.setEnabled(false);
                });

        activatingPanel.setEnabled(false);

        dimensionPanel.add(checkEnableDimensionBox, "wrap");
        dimensionPanel.add(activatingPanel, "wrap");

        return dimensionPanel;
    }
}
