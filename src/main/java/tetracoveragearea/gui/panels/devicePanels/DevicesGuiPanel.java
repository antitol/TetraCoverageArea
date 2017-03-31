package tetracoveragearea.gui.panels.devicePanels;

import tetracoveragearea.gui.panels.primitives.PrimaryPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Панель взаимодействия с устройствами
 * Created by anatoliy on 15.03.17.
 */
public class DevicesGuiPanel extends PrimaryPanel {

    private Map<String, JPanel> panelMap = new HashMap<String, JPanel>();

    private SerialGuiPanel serialGuiPanel = new SerialGuiPanel();
    private JPanel contentPanel = new JPanel();

    private final JLabel deviceTypeLabel = new JLabel("Тип интерфейса");
    private JComboBox deviceBox = new JComboBox();

    public DevicesGuiPanel() {

        // Складываем панельки здесь
        panelMap.put("RS-232", serialGuiPanel);

        // По умолчанию
        contentPanel.add(panelMap.get("RS-232"));

        deviceBox.setModel(new  DefaultComboBoxModel<String>(
                // Складывает ключи в ComboBox
                panelMap.keySet().toArray(new String[panelMap.size()]))
        );

        deviceBox.addActionListener(e -> {

            contentPanel.removeAll();
            contentPanel.add(panelMap.get(deviceBox.getSelectedItem()));
            revalidate();
        });

        add(deviceTypeLabel, "wrap, w 100%");
        add(deviceBox, "wrap, w 100%");
        add(contentPanel, "w 100%");
    }

    @Override
    public void onInvoke() {

    }

    @Override
    public void onRevoke() {

    }
}
