package tetracoveragearea.gui.panels.settingsPanels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.SetDefaultState;

import javax.swing.*;

/**
 * Created by anatoliy on 28.03.17.
 */
public class GradientPanel extends JPanel implements SetDefaultState {

    private JTable gradientTable = new JTable(GradientTableModel.getInstance());

    private JButton addLayerButton;
    private JButton removeLayerButton;
    private JButton saveLayerButton;
    private JButton loadLayerButton;

    public GradientPanel() {

        setLayout(new MigLayout("debug"));

        gradientTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        gradientTable.getColumnModel().getColumn(1).setPreferredWidth(150);

        addLayerButton = new JButton();
        removeLayerButton = new JButton();
        saveLayerButton = new JButton();
        loadLayerButton = new JButton();

        JScrollPane scrollPane = new JScrollPane(gradientTable);

        add(scrollPane, "span 2, wrap");

    }

    @Override
    public void setDefaultState() {

    }
}
