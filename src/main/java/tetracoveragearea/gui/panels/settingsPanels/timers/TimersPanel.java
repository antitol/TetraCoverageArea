package tetracoveragearea.gui.panels.settingsPanels.timers;

import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 30.03.17.
 */
public class TimersPanel extends SubPanel {

    private static double gpsQueryTime = 5;
    private static double rssiQueryTime = 5;
    private static double dumpPointTime = 6;

    private JLabel queryGpsTimerLabel = new JLabel("Запроса координат, сек:");
    private JLabel queryRssiTimerLabel = new JLabel("Запрос RSSI, сек:");
    private JLabel dumpPointTimerLabel = new JLabel("Добавление точки, сек:");

    private JTextField queryGpsTimeField = new JTextField("5");
    private JTextField queryRssiTimeField = new JTextField("5");
    private JTextField dumpPointTimeField = new JTextField("5");

    private JButton applyButton = new JButton("Применить");
    private JButton defaultButton = new JButton("Сбросить");

    public TimersPanel() {

        setName("Таймеры");

        applyButton.addActionListener(e -> {
            try {
                double gpsTime = Double.parseDouble(queryGpsTimeField.getText());
                double rssiTime = Double.parseDouble(queryRssiTimeField.getText());
                double pointTime = Double.parseDouble(dumpPointTimeField.getText());

                if (pointTime < gpsTime || pointTime < rssiTime) {
                    GuiComponents.showInformationPane(this, "Таймаут добавления точки не может быть меньше таймаутов запроса");
                } else {
                    setTimers(gpsTime, rssiTime, pointTime);
                }

                setRssiQueryTime(Double.parseDouble(queryRssiTimeField.getText()));
            } catch (NumberFormatException ex) {
                GuiComponents.showInformationPane(this, "Проверьте правильность введенных параметров");
            }
        });

        defaultButton.addActionListener(e -> {
            setTimers(5,5,5);
        });

        List<JComponent> verticalComponents = new ArrayList<>(Arrays.asList(
                queryGpsTimerLabel, queryGpsTimeField, queryRssiTimerLabel, queryRssiTimeField, dumpPointTimerLabel, dumpPointTimeField
        ));

        verticalComponents.forEach(component -> add(component, "span 2, w 100%, wrap"));

        add(applyButton);
        add(defaultButton, "wrap");
    }


    public static double getGpsQueryTime() {
        return gpsQueryTime;
    }

    public static double getRssiQueryTime() {
        return rssiQueryTime;
    }

    public static double getDumpPointTime() {
        return dumpPointTime;
    }

    public static void setGpsQueryTime(double gpsQueryTime) {
        TimersPanel.gpsQueryTime = gpsQueryTime;
    }

    public static void setRssiQueryTime(double rssiQueryTime) {
        TimersPanel.rssiQueryTime = rssiQueryTime;
    }

    public static void setDumpPointTime(double dumpPointTime) {
        TimersPanel.dumpPointTime = dumpPointTime;
    }

    public void setTimers(double gpsTime, double rssiTime, double dumpPointTime) {
        setRssiQueryTime(rssiTime);
        setGpsQueryTime(gpsTime);
        setDumpPointTime(dumpPointTime);
    }
}
