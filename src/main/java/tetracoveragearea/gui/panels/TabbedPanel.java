package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

/**
 * Created by anatoliy on 28.03.17.
 */
public class TabbedPanel extends JPanel {

    private List<JPanel> panels;
    private JTabbedPane tabbedPane;

    public TabbedPanel(List<JPanel> panels) {

        this.panels = panels;

        setLayout(new MigLayout("debug"));

        tabbedPane = new JTabbedPane(SwingConstants.VERTICAL);

        panels.forEach(jPanel ->
                tabbedPane.addTab(jPanel.getName(), jPanel)
        );

        tabbedPane.addChangeListener(e ->
                ((SetDefaultState) tabbedPane.getSelectedComponent()).setDefaultState()
        );

        add(tabbedPane, "center, wrap, w 100%");
    }

    public List<JPanel> getPanels() {
        return panels;
    }
}
