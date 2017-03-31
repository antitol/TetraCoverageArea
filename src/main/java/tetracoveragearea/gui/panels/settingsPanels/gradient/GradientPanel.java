package tetracoveragearea.gui.panels.settingsPanels.gradient;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.primitives.SubPanel;
import tetracoveragearea.gui.tools.MultilayerGradient;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anatoliy on 28.03.17.
 */
public class GradientPanel extends SubPanel {

    private JTable gradientTable = new JTable(GradientTableModel.getInstance());

    private JButton addLayerButton;
    private JButton removeLayerButton;
    private JButton saveLayerButton;
    private JButton loadLayerButton;

    private MultilayerGradient multilayerGradientLink;

    private JLabel gradientNameLabel = new JLabel("Имя профиля: ");
    private JTextField gradientNameField = new JTextField("SampleName");

    private LoadGradientDialog loadGradientDialog = new LoadGradientDialog(this);

    public GradientPanel() {

        setName("Градиент");

        setLayout(new MigLayout("debug"));
        setPreferredSize(new Dimension(300, 450));

        gradientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradientTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        gradientTable.getColumnModel().getColumn(1).setPreferredWidth(150);

        gradientTable.getColumnModel().getColumn(0).setCellRenderer(new ColorRenderer());
        gradientTable.getColumnModel().getColumn(0).setCellEditor(new ColorEditor());

        addLayerButton = new JButton("Добавить");
        removeLayerButton = new JButton("Удалить");
        saveLayerButton = new JButton("Сохранить");
        loadLayerButton = new JButton("Загрузить");

        multilayerGradientLink = GradientTableModel.getInstance().getMultilayerGradient();

        // Добавление слоя
        addLayerButton.addActionListener(e -> {

            multilayerGradientLink.addLayer(Color.WHITE, (float) multilayerGradientLink.getMaxPart());
            GradientTableModel.getInstance().fireTableDataChanged();

            int index = multilayerGradientLink.getLayers().size();
            gradientTable.setRowSelectionInterval(index-1, index-1);
        });

        // Удаление выделенного слоя
        removeLayerButton.addActionListener(e -> {

            if (multilayerGradientLink.getLayers().size() != 0) {

                multilayerGradientLink.removeLayer(gradientTable.getSelectedRow());

                int index = gradientTable.getSelectedRow();

                if (index > 0) {
                    gradientTable.setRowSelectionInterval(index-1, index-1);
                } else {
                    gradientTable.clearSelection();
                }

            }

            repaint();
            revalidate();
        });

        // Сохранения профиля градиента
        saveLayerButton.addActionListener(e -> {
            // Создаем копию профиля, чтобы профиль, находящийся в диалоговом окне не зависел от манипуляций с этой панелью
            MultilayerGradient savingProfile = MultilayerGradient.getCopy(
                    multilayerGradientLink
            );
            savingProfile.setName(gradientNameField.getText());
            loadGradientDialog.addMultiGradient(savingProfile);
        });

        // Вызов диалогового окна для загрузки градиента
        loadLayerButton.addActionListener(e -> {
            loadGradientDialog.setVisible(true);
        });

        JScrollPane scrollPane = new JScrollPane(gradientTable);

        add(scrollPane, "span 2, wrap");

        add(gradientNameLabel, "span 2, w 100%, wrap");
        add(gradientNameField, "span 2, w 100%, wrap");
        add(addLayerButton, "w 50%");
        add(removeLayerButton, "wrap, w 50%");
        add(saveLayerButton, "w 50%");
        add(loadLayerButton, "wrap, w 50%");

    }

    /**
     * Загрузка профиля градиента для таблицы и панели
     * @param multilayerGradient
     */
    public void setMultilayerGradient(MultilayerGradient multilayerGradient) {

        MultilayerGradient loadedProfile = MultilayerGradient.getCopy(multilayerGradient);
        GradientTableModel.getInstance().setMultilayerGradient(loadedProfile);
        multilayerGradientLink = loadedProfile;
        gradientNameField.setText(multilayerGradient.getName());
        GradientTableModel.getInstance().fireTableDataChanged();
    }

    @Override
    public void onInvoke() {

    }
}
