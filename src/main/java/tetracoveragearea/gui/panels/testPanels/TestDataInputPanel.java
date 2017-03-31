package tetracoveragearea.gui.panels.testPanels;

import com.jidesoft.swing.RangeSlider;
import de.fhpotsdam.unfolding.geo.Location;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.distribution.NormalDistribution;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.panels.primitives.PrimaryPanel;
import tetracoveragearea.serialDao.SerialTestDao;
import tetracoveragearea.testValues.RandomPointGenerator;

import javax.swing.*;
import java.util.Arrays;

/**
 * Панель для ввода тестовых данных в базу данных
 *
 * Created by anatoliy on 17.02.17.
 */
public class TestDataInputPanel extends PrimaryPanel {

    private JComboBox pointDistributionBox;
    private JComboBox rssiDistributionBox;

    private JComboBox typeInputPointBox;

    private JPanel manualInputPanel;
    private JPanel distributionPanel;

    private JSlider rssiValueSlider;

    private JLabel amountPointLabel;
    private JLabel firstParameterLabel;
    private JTextField firstParameterField;
    private JTextField secondParameterField;
    private JTextField amountPointsField;
    private RangeSlider rssiRangeSlider;
    private JButton fillRandomButton;
    private JButton addPointsButton;
    private JButton clearTableButton;

    private double rssiValue = 20;
    private boolean manualInput = false;

    public TestDataInputPanel() {

        setLayout(new MigLayout());

        manualInputPanel = new JPanel(new MigLayout("insets 0, w 260"));
        distributionPanel = new JPanel(new MigLayout("insets 0, w 260"));

        typeInputPointBox = new JComboBox(new String[] {"Ручной ввод", "Выборка"});

        typeInputPointBox.addActionListener(e -> {

            try {

                manualInput = typeInputPointBox.getSelectedIndex() == 0;

                MapApplet.getInstance().setManualPointInput(manualInput);

                if (manualInput) {

                    remove(distributionPanel);
                    add(manualInputPanel, "w 100%, wrap");
                    repaint();
                } else {

                    remove(manualInputPanel);
                    add(distributionPanel, "w 100%, wrap");
                    repaint();
                }

                revalidate();
                repaint();
            } catch (NullPointerException ex) {}
        });

        amountPointLabel = new JLabel("Количество точек");

        firstParameterLabel = new JLabel("Девиация, град");
        firstParameterField = new JTextField("0");

        rssiValueSlider = new JSlider(20, 120, 50);
        rssiValueSlider.setPaintLabels(true);
        rssiValueSlider.setMajorTickSpacing(20);

        rssiValueSlider.addChangeListener(e -> rssiValue = rssiValueSlider.getValue());

        amountPointsField = new JTextField("1000");
        rssiRangeSlider = new RangeSlider(20, 120, 20, 120);
        rssiRangeSlider.setPaintLabels(true);
        rssiRangeSlider.setMajorTickSpacing(20);

        addPointsButton = new JButton("Добавить точки");
        addPointsButton.addActionListener(e ->
        {
            try {

                /* Получаем координаты выбранной на карте точки и девиацию из текстового поля */
                Location centerLocation = MapApplet.getInstance().getMap().getMouseClickedMarker().getLocation();
                double sed = Double.parseDouble(firstParameterField.getText());

                // Создаем новый генератор точек с указанным распределением
                RandomPointGenerator randomPoints = new RandomPointGenerator(
                        new NormalDistribution(centerLocation.getLat(), sed),
                        new NormalDistribution(centerLocation.getLon(), sed),
                        rssiRangeSlider.getLowValue(),
                        rssiRangeSlider.getHighValue()
                );

                // Загружаем точки в главное хранилище
                GeometryStore.getInstance().addPoints(randomPoints.getRandomPoints(Integer.parseInt(amountPointsField.getText())));

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Некорректно заполнены поля");

            }
        });

        fillRandomButton = new JButton("Заполнить случайными данными");
        fillRandomButton.addActionListener(e -> {

                    amountPointsField.setText(String.valueOf((int) (1000*Math.random())));
                }
        );

        clearTableButton = new JButton("Очистить таблицы");
        clearTableButton.addActionListener(e -> {

            SerialTestDao.getInstance().clearTables();
        });

        Arrays.asList(
                amountPointLabel, amountPointsField,  firstParameterLabel, firstParameterField, rssiRangeSlider,
                fillRandomButton, addPointsButton, clearTableButton)
                .forEach(component -> distributionPanel.add(component, "w 100%, wrap")
        );


        Arrays.asList(
                rssiValueSlider
        )
                .forEach(component -> manualInputPanel.add(component, "w 100%, wrap"));

        add(typeInputPointBox, "w 250, wrap");
        add(manualInputPanel, "w 100%, wrap");
        manualInput = true;
    }

    public double getRssiValue() {
        return rssiValue;
    }

    @Override
    public void onInvoke() {
        MapApplet.getInstance().setManualPointInput(manualInput);
    }

    @Override
    public void onRevoke() {
        MapApplet.getInstance().setManualPointInput(false);
    }
}
