package tetracoveragearea.gui.panels.settingsPanels.tileProvider;

import de.fhpotsdam.unfolding.providers.*;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anatoliy on 05.04.17.
 */
public class ChangeTileProviderPanel extends SubPanel {

    private Map<String, AbstractMapProvider> providersMap = new HashMap<>();
    private JLabel providerLabel = new JLabel("Источник карт");
    private JComboBox<String> providerBox = new JComboBox<>();

    public ChangeTileProviderPanel() {

        setName("Подложка");

        providersMap.put("OpenStreetMap", new OpenStreetMap.OpenStreetMapProvider());
        providersMap.put("GoogleMaps", new Google.GoogleMapProvider());
        providersMap.put("Esri", new EsriProvider.WorldStreetMap());
        providersMap.put("EsriTopoMap", new EsriProvider.WorldTopoMap());


        providerBox.setModel(new  DefaultComboBoxModel<String>(
                // Складывает ключи в ComboBox
                providersMap.keySet().toArray(new String[providersMap.size()]))
        );

        providerBox.addActionListener(e -> {

            MapApplet.getInstance().getMap().setMapProvider(
                    providersMap.get(providerBox.getSelectedItem())
            );
        });

        add(providerLabel, "wrap");
        add(providerBox, "wrap, w 100%");
    }
}
