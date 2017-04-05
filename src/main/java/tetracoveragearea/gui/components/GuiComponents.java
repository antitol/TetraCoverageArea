package tetracoveragearea.gui.components;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Сборник GUI элементов
 *
 * Created by anatoliy on 06.03.17.
 */
public class GuiComponents {

    private static DecimalFormat latFormatter = new DecimalFormat("00.0000");
    private static DecimalFormat lonFormatter = new DecimalFormat("#00.0000");
    private static DecimalFormat rssiFormatter = new DecimalFormat("###");

    public static MetalToggleButtonUI getToggleButtonGreenUI() {
        return new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return Color.green;
            }
        };
    }

    public GuiComponents() {

        DecimalFormatSymbols formatSymbols = latFormatter.getDecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        latFormatter.setDecimalFormatSymbols(formatSymbols);
        lonFormatter.setDecimalFormatSymbols(formatSymbols);
        rssiFormatter.setDecimalFormatSymbols(formatSymbols);
    }

    public static void showInformationPane(JComponent parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Внимание!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static DecimalFormat getLatFormatter() {
        return latFormatter;
    }

    public static DecimalFormat getLonFormatter() {
        return lonFormatter;
    }

    public static DecimalFormat getRssiFormatter() {
        return rssiFormatter;
    }
}
