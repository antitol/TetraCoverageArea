package tetracoveragearea.gui.panels.settingsPanels.gradient;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.gui.tools.MultilayerGradient;

import javax.swing.*;
import java.awt.*;

/**
 * Метка с цветовой палитрой профиля градиента
 * Created by anatoliy on 29.03.17.
 */
public class GradientLabel extends JLabel {

    private MultilayerGradient multilayerGradient;

    public GradientLabel(MultilayerGradient multilayerGradient) {

        this.multilayerGradient = multilayerGradient;

        setSize(200, 30);
        setPreferredSize(new Dimension(200, 30));
        setOpaque(true);
        setLayout(new MigLayout("insets 0, gap 0"));
        setMultilayerGradient(multilayerGradient);
    }

    /**
     * Генерация метки профиля мультиградиента
     * Цвета на метке повторяют порядок слоев профиля, но не зависят от их ширины (part)
     * @param multilayerGradient
     */
    public void setMultilayerGradient(MultilayerGradient multilayerGradient) {

        this.multilayerGradient = multilayerGradient;
        removeAll();

        for (int i = 0; i < multilayerGradient.getLayers().size(); i++) {
            JLabel layerLabel = new JLabel();
            layerLabel.setPreferredSize(new Dimension (this.getWidth() / multilayerGradient.getLayers().size(), 30));
            // Для отрисовки каждого пикселя метки
            layerLabel.setOpaque(true);
            layerLabel.setBackground(multilayerGradient.getLayer(i).getColor());
            add(layerLabel);
        }

        repaint();
        revalidate();
    }

    public MultilayerGradient getMultilayerGradient() {
        return multilayerGradient;
    }
}
