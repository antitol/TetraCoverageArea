package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.panels.databasePanels.DatabasePanel;
import tetracoveragearea.gui.panels.devicePanels.DevicesGuiPanel;
import tetracoveragearea.gui.panels.filterPanels.InterpolationPanel;
import tetracoveragearea.gui.panels.filterPanels.LocationFilterPanel;
import tetracoveragearea.gui.panels.filterPanels.TimeFilterPanel;
import tetracoveragearea.gui.panels.primitives.PrimaryPanel;
import tetracoveragearea.gui.panels.primitives.TabbedPanel;
import tetracoveragearea.gui.panels.settingsPanels.gradient.GradientPanel;
import tetracoveragearea.gui.panels.settingsPanels.tileProvider.ChangeTileProviderPanel;
import tetracoveragearea.gui.panels.settingsPanels.timers.TimersPanel;
import tetracoveragearea.gui.panels.testPanels.TestDataInputPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Регулирует отображением панелей, которые выбираются в меню
 * Created by anatoliy on 15.03.17.
 */
public class MainContentPanel extends JPanel {

    private static MainContentPanel instance = new MainContentPanel();

    public static MainContentPanel getInstance() {
        return instance;
    }

    private PrimaryPanel primaryPanel;

    private DevicesGuiPanel devicesGuiPanel = new DevicesGuiPanel();
    private DatabasePanel databasePanel = new DatabasePanel();
    private TabbedPanel filterPanel = new TabbedPanel(Arrays.asList(new TimeFilterPanel(), new InterpolationPanel(), new LocationFilterPanel()));
    private TabbedPanel settingsPanel = new TabbedPanel(Arrays.asList(new GradientPanel(), new TimersPanel(), new ChangeTileProviderPanel()));
    private TestDataInputPanel testDataInputPanel = new TestDataInputPanel();

    public MainContentPanel() {

        setLayout(new MigLayout("debug, center"));

        setPreferredSize(new Dimension(280, 450));
    }

    public void installPanel(PrimaryPanel primaryPanel) {
        // Стираем старую и рисуем новую панель
        removeAll();
        repaint();
        add(primaryPanel, "center");
        revalidate();

        // Сообщаем старой панели, что ее отозвали, новой, что ее вызвали

        try {
            getPrimaryPanel().onRevoke();
        } catch (NullPointerException ex) {}

        setPrimaryPanel(primaryPanel);
        primaryPanel.onInvoke();
    }

    public void setPrimaryPanel(PrimaryPanel primaryPanel) {
        this.primaryPanel = primaryPanel;
    }

    public PrimaryPanel getPrimaryPanel() {
        return primaryPanel;
    }

    public void swtichDevicePanel() {
        installPanel(devicesGuiPanel);
    }

    public void swtichTestPanel() {
        installPanel(testDataInputPanel);
    }

    public void swtichDatabasePanel() {
        installPanel(databasePanel);
    }

    public void swtichFilterPanel() {
        installPanel(filterPanel);
    }

    public void swtichSettingsPanel() { installPanel(settingsPanel); }

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
