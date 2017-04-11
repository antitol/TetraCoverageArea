package tetracoveragearea.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.MainContentPanel;
import tetracoveragearea.gui.panels.MenuPanel;
import tetracoveragearea.gui.panels.mapPanels.MapGuiPanel;
import tetracoveragearea.gui.panels.settingsPanels.gradient.ChooseGradientTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by anatoliy on 16.01.17.
 */
public class MainFrame extends JFrame {



    public static final Logger log = Logger.getLogger(MainFrame.class);

    private final MapApplet mapApplet;

    // Панели главного окна
    private MenuPanel menuPanel = new MenuPanel();
    private MapGuiPanel mapGuiPanel = new MapGuiPanel();

    GuiComponents guiComponents = new GuiComponents();

    public MainFrame() {


        super("TetraCoverageArea");
        Locale.setDefault(new Locale.Builder().setLanguage("ru").build());

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

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                try {

                    FileOutputStream fileOutputStream = new FileOutputStream(
                            ChooseGradientTableModel.getInstance().getGradientFile()
                    );

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(ChooseGradientTableModel.getInstance().getGradientList());

                    objectOutputStream.close();
                    fileOutputStream.close();

                } catch (FileNotFoundException ex) {

                    log.info("Файл для сохранения профилей градиента не найден");
                } catch (IOException ex) {

                    log.info("Ошибка ввода-вывода");
                }
            }
        });

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
