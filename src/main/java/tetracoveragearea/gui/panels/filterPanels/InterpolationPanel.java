package tetracoveragearea.gui.panels.filterPanels;

import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Панель управления интерполяцией точек
 * Created by anatoliy on 24.03.17.
 */
public class InterpolationPanel extends SubPanel {


    private final JLabel areaLabel = new JLabel("Число ячеек");
    private final JLabel cellSizeLabel = new JLabel("Размер ячейки");

    private JSpinner areaSpinner;
    private JSpinner cellSizeSpinner;
    private JButton confirmButton;
    private JButton resetButton;

    private boolean userSetted = false;

    public InterpolationPanel() {

        setName("Интерполяция");

        SpinnerNumberModel areaSpinnerModel = new SpinnerNumberModel(64, 1, 1000000, 1);
        SpinnerNumberModel cellSizeModel = new SpinnerNumberModel(1, 1, 100, 1);
        areaSpinner = new JSpinner(areaSpinnerModel);
        cellSizeSpinner = new JSpinner(cellSizeModel);

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {


            GeometryStore.getInstance().interpolateFilter(
                    (int) areaSpinner.getValue(),
                    (int) cellSizeSpinner.getValue()
            );

            userSetted = true;

        });

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {

            GeometryStore.getInstance().filter();

            userSetted = false;
        });

        List<JComponent> components = Arrays.asList(
                areaLabel, areaSpinner, cellSizeLabel, cellSizeSpinner
        );

        components.forEach(c -> add(c, "span 2, w 100%, wrap"));

        add(confirmButton, "center");
        add(resetButton, "center, wrap");
    }
}
