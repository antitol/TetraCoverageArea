package tetracoveragearea.gui.panels.filterPanels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.panels.SetDefaultState;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 24.03.17.
 */
public class InterpolationPanel extends JPanel implements SetDefaultState {


    private final JLabel areaLabel = new JLabel("Площадь в кв.км");

    private JSpinner areaSpinner;
    private JButton confirmButton;
    private JButton resetButton;

    private boolean userSetted = false;

    public InterpolationPanel() {

        setLayout(new MigLayout("debug"));

        setName("Интерполяция");

        SpinnerNumberModel areaSpinnerModel = new SpinnerNumberModel(1, 0.1, Math.pow(10, 10), 0.1);
        areaSpinner = new JSpinner(areaSpinnerModel);

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {

            GeometryStore.getInstance().interpolateFilter(
                    (double) areaSpinner.getValue()
            );

            userSetted = true;

        });

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {

            GeometryStore.getInstance().resetFilters();

            userSetted = false;
        });

        List<JComponent> components = Arrays.asList(
                areaLabel, areaSpinner
        );

        components.forEach(c -> add(c, "span 2, w 100%, wrap"));

        add(confirmButton, "center");
        add(resetButton, "center, wrap");
    }

    @Override
    public void setDefaultState() {

    }
}
