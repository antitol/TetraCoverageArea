package tetracoveragearea.gui.panels.filterPanels;

import com.jidesoft.swing.RangeSlider;
import net.miginfocom.swing.MigLayout;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;

/**
 * Created by anatoliy on 31.03.17.
 */
public class LocationFilterPanel extends SubPanel {

    private final int scale = (int) Math.pow(10, 4);
    private boolean userSetted = false;

    private JButton confirmButton;
    private JButton resetButton;

    private DimensionalPanel xPanel = new DimensionalPanel(
            "Широта",
            GuiComponents.getLatFormatter(),
            GeometryStore.getInstance().getMinLatitude(),
            GeometryStore.getInstance().getMaxLatitude()
    );

    private DimensionalPanel yPanel = new DimensionalPanel(
            "Долгота",
            GuiComponents.getLonFormatter(),
            GeometryStore.getInstance().getMinLongitude(),
            GeometryStore.getInstance().getMaxLongitude()
    );

     private DimensionalPanel rssiPanel = new DimensionalPanel(
            "Уровень сигнала",
            GuiComponents.getRssiFormatter(),
            GeometryStore.getInstance().getMinRssi(),
            GeometryStore.getInstance().getMaxRssi()
    );

    public LocationFilterPanel() {

        setName("Область");

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {

            try {
                if (xPanel.isSwitchOn()) {
                    Filter.setMinLat(OptionalDouble.of(xPanel.getMinValue()));
                    Filter.setMaxLat(OptionalDouble.of(xPanel.getMaxValue()));
                }

                if (yPanel.isSwitchOn()) {
                    Filter.setMinLong(OptionalDouble.of(yPanel.getMinValue()));
                    Filter.setMaxLong(OptionalDouble.of(yPanel.getMaxValue()));
                }

                if (rssiPanel.isSwitchOn()) {
                    Filter.setMinRssi(OptionalDouble.of(rssiPanel.getMinValue()));
                    Filter.setMaxRssi(OptionalDouble.of(rssiPanel.getMaxValue()));
                }
            } catch (NumberFormatException ex) {
                GuiComponents.showInformationPane(this, "Неправильно заполнены поля");
            }

            GeometryStore.getInstance().filter();
            userSetted = true;
        });

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {

            // Сбрасываются слайдеры и значения
            xPanel.resetPanel();
            yPanel.resetPanel();
            // Сбрасываем фильтры
            Filter.setMinLat(OptionalDouble.empty());
            Filter.setMaxLat(OptionalDouble.empty());
            Filter.setMinLong(OptionalDouble.empty());
            Filter.setMaxLong(OptionalDouble.empty());
            Filter.setMinRssi(OptionalDouble.empty());
            Filter.setMaxRssi(OptionalDouble.empty());
            // Говорим хранилищу построить новый массив точек по фильтрам и отобразить его
            GeometryStore.getInstance().filter();
            userSetted = false;
        });

        Arrays.asList(xPanel, yPanel, rssiPanel).forEach(component -> add(component, "span 2, wrap"));

        add(confirmButton);
        add(resetButton, "wrap");
    }

    @Override
    public void onInvoke() {

        MapApplet.getInstance().getMap().showBoundingBox(true);
        xPanel.setBoundingBoxConsumer(MapApplet.getInstance().getMap()::setLatitudeBounds);
        yPanel.setBoundingBoxConsumer(MapApplet.getInstance().getMap()::setLongitudeBounds);

        xPanel.setMinValue(GeometryStore.getInstance().getMinLatitude());
        xPanel.setMaxValue(GeometryStore.getInstance().getMaxLatitude());
        yPanel.setMinValue(GeometryStore.getInstance().getMinLongitude());
        yPanel.setMaxValue(GeometryStore.getInstance().getMaxLongitude());
        rssiPanel.setMinValue(GeometryStore.getInstance().getMinRssi());
        rssiPanel.setMaxValue(GeometryStore.getInstance().getMaxRssi());
    }

    @Override
    public void onRevoke() {

        MapApplet.getInstance().getMap().showBoundingBox(false);
        GeometryStore.getInstance().setFilterPoints(new ArrayList<>());
    }

    private class DimensionalPanel extends JPanel  {

        private String dimensionName;
        private DecimalFormat formatter;
        private double from = 0;
        private double to = 0;

        private JPanel activatingPanel = new JPanel(new MigLayout("insets 0, w 260"));

        private JTextField fromValueField = new JTextField();
        private JTextField toValueField = new JTextField();

        private JCheckBox checkEnableDimensionBox;
        private RangeSlider valueSlider;

        /** Функциональный интерфейс - ссылка на метод установки границ маркера
         * Логика работы - при взаимодействии пользователем области фильтрации
         * автоматически вызывается метод, передаваемый данным интерфейсом */
        private BiConsumer<Double, Double> boundingBoxConsumer;

        public DimensionalPanel(String dimensionName, DecimalFormat formatter, double from, double to) {

            this.dimensionName = dimensionName;
            this.formatter = formatter;
            this.from = from;
            this.to = to;

            setLayout(new MigLayout("debug, insets 0, w 260"));

            checkEnableDimensionBox = new JCheckBox(dimensionName);

            checkEnableDimensionBox.addActionListener(e -> {

                for (Component component : activatingPanel.getComponents()) {
                    component.setEnabled(checkEnableDimensionBox.isSelected());
                }
            });

            JLabel fromToLabel = new JLabel("От/до: ");

            fromValueField.setText(formatter.format(from));
            toValueField.setText(formatter.format(to));

            valueSlider = new RangeSlider((int) Math.floor(from * scale), (int) Math.ceil(to * scale));

            valueSlider.setLowValue(valueSlider.getMinimum());
            valueSlider.setHighValue(valueSlider.getMaximum());

            valueSlider.addChangeListener(e -> {

                double newFromValue = valueSlider.getLowValue() / (double) scale;
                double newToValue = valueSlider.getHighValue() / (double) scale;

                fromValueField.setText(formatter.format(newFromValue));
                toValueField.setText(formatter.format(newToValue));

                try {
                    boundingBoxConsumer.accept(newFromValue, newToValue);
                } catch (NullPointerException ex) {}
            });

            Arrays.asList(fromToLabel, fromValueField, toValueField, valueSlider)
                    .forEach(component -> {
                        activatingPanel.add(component, "w 100%, wrap");
                        component.setEnabled(false);
                    });

            add(checkEnableDimensionBox, "wrap");
            add(activatingPanel, "wrap");
        }

        /**
         * @return - значение поля минимум
         * @throws NumberFormatException
         */
        public double getMinValue() throws NumberFormatException {
            return Double.parseDouble(fromValueField.getText());
        }

        /**
         * @return - значение поля максимум
         * @throws NumberFormatException
         */
        public double getMaxValue() throws NumberFormatException {
            return Double.parseDouble(toValueField.getText());
        }

        public void setMinValue(double value) {
            from = value;
            fromValueField.setText(formatter.format(from));
            valueSlider.setMinimum((int) (from * scale));

            if (!userSetted) {
                valueSlider.setLowValue(valueSlider.getMinimum());
            }
        }

        public void setMaxValue(double value) {
            // Добавляет единичку к последнему значащему разряду, иначе форматтер обрезает остаток
            // и при применении фильтра максимальная точка не попадает в диапазон
            to = value + 1 / (double) scale;
            toValueField.setText(formatter.format(to));
            valueSlider.setMaximum((int) Math.ceil(to * scale));

            if (!userSetted) {
                valueSlider.setHighValue(valueSlider.getMaximum());
            }
        }

        public void resetPanel() {
            valueSlider.setLowValue(valueSlider.getMinimum());
            valueSlider.setHighValue(valueSlider.getMaximum());
        }

        public void setBoundingBoxConsumer(BiConsumer<Double, Double> boundingBoxConsumer) {
            this.boundingBoxConsumer = boundingBoxConsumer;
        }

        /**
         * @return - включен ли checkBox на панели
         */
        public boolean isSwitchOn() {
            return checkEnableDimensionBox.isSelected();
        }
    }
}
