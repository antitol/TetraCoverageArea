package gui.dialogs;

import gui.panels.filterPanels.TimeFilterPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 02.03.17.
 */
public class FilterDialog extends JDialog {

    private List<JPanel> filterPanels;
    private JTabbedPane tabbedPane;

    private TimeFilterPanel timeFilterPanel = new TimeFilterPanel();

    private JButton okButton;

    public static FilterDialog instance = new FilterDialog();

    public static FilterDialog getInstance() {
        return instance;
    }

    public FilterDialog() {

        super();
        this.filterPanels = Arrays.asList(timeFilterPanel);

        setTitle("Фильтры");
        setLayout(new MigLayout("debug"));

        tabbedPane = new JTabbedPane(SwingConstants.VERTICAL);

        filterPanels.forEach(jPanel ->
                tabbedPane.addTab(jPanel.getName(), jPanel)
        );

        okButton = new JButton("OK");
        okButton.addActionListener(e -> setVisible(false));

        add(tabbedPane, "span 2, center, wrap");

        add(okButton, "w 50px, right");

        pack();

        setVisible(false);
    }

    public TimeFilterPanel getTimeFilterPanel() {
        return timeFilterPanel;
    }
}
