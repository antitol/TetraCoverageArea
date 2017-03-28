package tetracoveragearea.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.panels.MainContentPanel;
import tetracoveragearea.gui.panels.MenuPanel;
import tetracoveragearea.gui.panels.mapPanels.MapGuiPanel;

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
    private MenuPanel menuPanel = new MenuPanel();
    private MapGuiPanel mapGuiPanel = new MapGuiPanel();

    public MainFrame() {
        super("ComTest");

        log.info("Старт frame'a");

        mapApplet = MapApplet.getInstance();

        setLayout(new MigLayout("debug"));
        setSize(1200, 700);
        setResizable(false);

        JPanel appletPanel = new JPanel(new MigLayout("debug"));
        appletPanel.setPreferredSize(new Dimension(840, 620));

        mapApplet.frame = this;
        mapApplet.init();
        mapApplet.resize(new Dimension(800,600));

        mapApplet.setPreferredSize(new Dimension(800, 600));
        log.info("Инициализация applet'a");

        appletPanel.add(mapApplet);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(appletPanel, "span 1 2");
        add(menuPanel, "span 1 2");

        add(mapGuiPanel, "wrap, center");
        add(MainContentPanel.getInstance(), "wrap");


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
