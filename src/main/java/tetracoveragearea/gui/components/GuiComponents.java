package tetracoveragearea.gui.components;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.io.File;
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
    private static JFileChooser exportFileChooser = new JFileChooser("src/main/resources/export") {
        @Override
        public File getSelectedFile() {
            try {
                if (super.getSelectedFile().getName().indexOf('.') == -1) {
                    setSelectedFile(new File(super.getSelectedFile().getPath().concat(exportFileChooser.getFileFilter().getDescription())));
                }
            } catch (NullPointerException ex) {
                return null;
            }

            return super.getSelectedFile();
        }

        @Override
        public void approveSelection(){
            File f = getSelectedFile();
            if(f.exists() && getDialogType() == SAVE_DIALOG){
                int result = JOptionPane.showConfirmDialog(this,"Файл существует, перезаписать?","Подтвердите действие",JOptionPane.YES_NO_CANCEL_OPTION);
                switch(result){
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                }
            }
            super.approveSelection();
        }
    };

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

    public static JFileChooser getExportFileChooser() {
        return exportFileChooser;
    }
}
