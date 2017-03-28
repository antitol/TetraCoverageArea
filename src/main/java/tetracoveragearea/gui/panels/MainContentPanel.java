package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.devicePanels.DevicesGuiPanel;
import tetracoveragearea.gui.panels.filterPanels.InterpolationPanel;
import tetracoveragearea.gui.panels.filterPanels.TimeFilterPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by anatoliy on 15.03.17.
 */
public class MainContentPanel extends JPanel {

    private static MainContentPanel instance = new MainContentPanel();

    public static MainContentPanel getInstance() {
        return instance;
    }

    private DevicesGuiPanel devicesGuiPanel = new DevicesGuiPanel();
    private DatabasePanel databasePanel = new DatabasePanel();
    private TabbedPanel filterPanel = new TabbedPanel(Arrays.asList(new TimeFilterPanel(), new InterpolationPanel()));
    private TabbedPanel settingsPanel = new TabbedPanel(Arrays.asList());
    private TestDataInputPanel testDataInputPanel = new TestDataInputPanel();

    public MainContentPanel() {

        setLayout(new MigLayout("debug, center"));

        setPreferredSize(new Dimension(280, 450));
    }

    public void setPanel(JPanel panel) {
        removeAll();
        repaint();
        add(panel, "center");
        revalidate();
    }

    public void swtichDevicePanel() {
        setPanel(devicesGuiPanel);
    }

    public void swtichTestPanel() {
        setPanel(testDataInputPanel);
    }

    public void swtichDatabasePanel() {
        setPanel(databasePanel);
    }

    public void swtichFilterPanel() {
        setPanel(filterPanel);
    }
    
    public void swtichSettingsPanel() { setPanel(settingsPanel); }

    public DevicesGuiPanel getDevicesGuiPanel() {
        return devicesGuiPanel;
    }

    public DatabasePanel getDatabasePanel() {
        return databasePanel;
    }

    public TabbedPanel getFilterPanel() {
        return filterPanel;
    }
    
    public TabbedPanel getSettingsPanel() {return settingsPanel;}

    public TestDataInputPanel getTestDataInputPanel() {
        return testDataInputPanel;
    }
}
