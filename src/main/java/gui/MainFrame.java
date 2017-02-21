package gui;

import gui.applet.MapApplet;
import gui.panels.MapGuiPanel;
import gui.panels.SerialGuiPanel;
import gui.panels.TableGuiPanel;
import gui.panels.TestDataInputPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by anatoliy on 16.01.17.
 */
public class MainFrame extends JFrame {

    private final MapApplet mapApplet = new MapApplet();

    private MapGuiPanel mapGuiPanel = new MapGuiPanel();
    private SerialGuiPanel serialGuiPanel = new SerialGuiPanel();
    private TableGuiPanel tableGuiPanel = new TableGuiPanel();
    private TestDataInputPanel testDataInputPanel = new TestDataInputPanel();


    public MainFrame() {

        super("ComTest");

        JPanel appletPanel = new JPanel(new MigLayout());
        appletPanel.setSize(800, 600);

        mapApplet.init();
        mapApplet.setSize(800,600);

        appletPanel.add(mapApplet);



        LayoutManager layout = new MigLayout();
        setLayout(layout);
        setSize(1100, 650);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(appletPanel);


        add(mapGuiPanel);

        setVisible(true);
    }

    private JPanel packGuiPanel(List<JComponent> components) {

        JPanel guiPanel = new JPanel(new MigLayout());

        components.forEach(c -> guiPanel.add(c, "w 100% ,wrap"));
        return guiPanel;
    }
}
