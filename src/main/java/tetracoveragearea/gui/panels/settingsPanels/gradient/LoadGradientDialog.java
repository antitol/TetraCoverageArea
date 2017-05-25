package tetracoveragearea.gui.panels.settingsPanels.gradient;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.tools.MultilayerGradient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Диалоговое окно выбора градиента
 * Список градиентов загружается при старте программы и сохраняется между сессиями
 * Created by anatoliy on 29.03.17.
 */
public class LoadGradientDialog extends JDialog {

    private GradientPanel parentGradientPanel;

    private JTable gradientsTable;
    private ChooseGradientTableModel chooseGradientTableModel;

    private JButton applyButton = new JButton("Загрузить");
    private JButton deleteButton = new JButton("Удалить");
    private JButton cancelButton = new JButton("Отмена");

    public LoadGradientDialog(GradientPanel gradientPanel) {

        super();

        this.parentGradientPanel = gradientPanel;
        chooseGradientTableModel = ChooseGradientTableModel.getInstance();

        gradientsTable = new JTable(chooseGradientTableModel);

        setTitle("Загрузка градиента");
        setModal(true);
        setLocationRelativeTo(null);

        setMinimumSize(new Dimension(500, 500));
        setPreferredSize(new Dimension(500, 500));

        setLayout(new MigLayout());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);


        gradientsTable.setDefaultRenderer(String.class, centerRenderer);
        gradientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradientsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                return (JLabel) value;
            }
        });

        JScrollPane scrollPane = new JScrollPane(gradientsTable);

        applyButton.addActionListener(e -> {

            parentGradientPanel.setMultilayerGradient(
                    chooseGradientTableModel.getGradientList().get(gradientsTable.getSelectedRow())
            );

            setVisible(false);
        });

        deleteButton.addActionListener(e -> {

            chooseGradientTableModel.removeMultiGradient(gradientsTable.getSelectedRow());
            chooseGradientTableModel.fireTableDataChanged();
        });

        cancelButton.addActionListener(e -> setVisible(false));

        add(scrollPane, "span 3, w 100%, wrap");
        add(applyButton, "w 33%");
        add(deleteButton, "w 33%");
        add(cancelButton, "w 33%, wrap");

    }

    /**
     * Добавление профиля градиента в таблицу
     * @param multilayerGradient
     */
    public void addMultiGradient(MultilayerGradient multilayerGradient) {

        chooseGradientTableModel.addMultiGradient(multilayerGradient);
    }
}
