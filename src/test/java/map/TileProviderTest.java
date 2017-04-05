package map;

import de.fhpotsdam.unfolding.providers.EsriProvider;
import org.junit.Test;
import tetracoveragearea.gui.MainFrame;
import tetracoveragearea.gui.applet.MapApplet;

import javax.swing.*;

/**
 * Created by anatoliy on 05.04.17.
 */
public class TileProviderTest {

    @Test
    public void test() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });



        try {
            Thread.sleep(2000);
            MapApplet.getInstance().getMap().setMapProvider(new EsriProvider.WorldStreetMap());
            Thread.sleep(5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
