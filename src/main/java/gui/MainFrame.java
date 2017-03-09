package gui;

import gui.applet.MapApplet;
import gui.panels.MapGuiPanel;
import gui.panels.SerialGuiPanel;
import gui.panels.TableGuiPanel;
import gui.panels.TestDataInputPanel;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by anatoliy on 16.01.17.
 */
public class MainFrame extends JFrame {

    public static final Logger log = Logger.getLogger(MainFrame.class);

    private final MapApplet mapApplet;

    // Панели главного окна
    private MapGuiPanel mapGuiPanel = new MapGuiPanel();
    private SerialGuiPanel serialGuiPanel = new SerialGuiPanel();
    private TableGuiPanel tableGuiPanel = new TableGuiPanel();
    private TestDataInputPanel testDataInputPanel = new TestDataInputPanel();

    public MainFrame() {
        super("ComTest");

        log.info("Старт frame'a");

        mapApplet = new MapApplet();

        setLayout(new MigLayout("debug"));
        setSize(1100, 750);

        JPanel appletPanel = new JPanel(new MigLayout());
        appletPanel.setPreferredSize(new Dimension(840, 620));

        mapApplet.init();

        mapApplet.setPreferredSize(new Dimension(800, 600));
        log.info("Инициализация applet'a");

        appletPanel.add(mapApplet);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(appletPanel, "span 1 3");

        add(mapGuiPanel, "wrap");
        add(serialGuiPanel, "wrap");
        add(testDataInputPanel);

        setVisible(true);

    }

    /**
     * Симулятор VerticalBox
     * @param components
     * @return
     */
    private JPanel packGuiPanel(List<JComponent> components) {

        JPanel guiPanel = new JPanel(new MigLayout());

        components.forEach(c -> guiPanel.add(c, "w 100%, wrap"));
        return guiPanel;
    }
}
