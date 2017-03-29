import org.junit.Test;
import tetracoveragearea.gui.panels.settingsPanels.gradient.LoadGradientDialog;
import tetracoveragearea.gui.tools.MultilayerGradient;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Created by anatoliy on 29.03.17.
 */
public class MultiGradientTest {

    public static final Logger log = Logger.getLogger(SerialTest.class.getName());

    @Test
    public void getWidthTest() {

        MultilayerGradient multilayerGradient = new MultilayerGradient(
                new ArrayList<>(Arrays.asList(
                        new MultilayerGradient.ColorLayer(Color.GREEN, 20f),
                        new MultilayerGradient.ColorLayer(Color.YELLOW, 40f),
                        new MultilayerGradient.ColorLayer(Color.YELLOW, 60f),
                        new MultilayerGradient.ColorLayer(Color.RED, 100f)
                ))
        );

        assertEquals(multilayerGradient.getWidth(0), 0.25d, 0);
        assertEquals(multilayerGradient.getWidth(1), 0.25d, 0);
        assertEquals(multilayerGradient.getWidth(2), 0.5d, 0);
    }

    @Test
    public void dialogTest() {

        LoadGradientDialog loadGradientDialog = new LoadGradientDialog(new ArrayList<>(
                Arrays.asList(
                        new MultilayerGradient(
                            Arrays.asList(new MultilayerGradient.ColorLayer(Color.GREEN, 10f),
                                    new MultilayerGradient.ColorLayer(Color.YELLOW, 20f),
                                    new MultilayerGradient.ColorLayer(Color.RED, 30f))),
                        new MultilayerGradient(
                                Arrays.asList(new MultilayerGradient.ColorLayer(Color.GREEN, 10f),
                                        new MultilayerGradient.ColorLayer(Color.YELLOW, 20f),
                                        new MultilayerGradient.ColorLayer(Color.RED, 30f)
                                ))
        )));

        loadGradientDialog.setVisible(true);

        while (true) {

        }
    }
}
