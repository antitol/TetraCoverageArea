package tetracoveragearea.gui.panels.primitives;

import tetracoveragearea.gui.panels.WithInvoking;

import javax.swing.*;
import java.util.List;

/**
 * Created by anatoliy on 28.03.17.
 */
public class TabbedPanel extends PrimaryPanel {

    private List<SubPanel> panels;
    private JTabbedPane tabbedPane;

    public TabbedPanel(List<SubPanel> panels) {

        this.panels = panels;

        tabbedPane = new JTabbedPane(SwingConstants.VERTICAL);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        panels.forEach(jPanel ->
                tabbedPane.addTab(jPanel.getName(), jPanel)
        );

        tabbedPane.addChangeListener(e ->
                ((WithInvoking) tabbedPane.getSelectedComponent()).onInvoke()
        );

        add(tabbedPane, "center, wrap, w 100%");
    }

    public List<SubPanel> getPanels() {
        return panels;
    }

    @Override
    public void onInvoke() {
        panels.forEach(panel -> panel.onInvoke());
    }

    @Override
    public void onRevoke() {
        panels.forEach(panel -> panel.onRevoke());
    }
}
