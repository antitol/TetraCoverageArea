package tetracoveragearea.gui.components;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;

/**
 * Сборник GUI элементов
 *
 * Created by anatoliy on 06.03.17.
 */
public class GuiComponents {

    public static MetalToggleButtonUI getToggleButtonGreenUI() {
        return new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return Color.green;
            }
        };
    }

    public static void showInformationPane(JComponent parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Внимание!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
