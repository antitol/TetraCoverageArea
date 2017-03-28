package tetracoveragearea.gui.tools;

/**
 * Created by anatoliy on 04.03.17.
 */

import org.apache.log4j.Logger;
import processing.core.PApplet;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Многослойный градиент (занимается выдачей цвета маркерам, в зависимости от значения уровня сигнала)
 */
public class MultilayerGradient {

    public static final Logger log = Logger.getLogger(MultilayerGradient.class);

    ColorLayerComparator colorLayerComparator = new ColorLayerComparator();

    private List<ColorLayer> layers;


    private static MultilayerGradient instance = new MultilayerGradient(
            Arrays.asList(
                        new ColorLayer(Color.GREEN, 0f),
                        new ColorLayer(Color.YELLOW, 0.5f),
                        new ColorLayer(Color.RED, 1f)
                    )
    );

    public static MultilayerGradient getInstance() {
        return instance;
    }

    private double max;
    private double min;

    public MultilayerGradient(List<ColorLayer> layers) {
        this.layers = layers;
        Collections.sort(layers, colorLayerComparator);
        max = layers.stream().mapToDouble(layer -> layer.part).max().getAsDouble();
        min = layers.stream().mapToDouble(layer -> layer.part).min().getAsDouble();
    }

    public Color getColor(float part) {

        if (part >= max) return layers.get(layers.size() - 1).color;
        if (part <= min) return layers.get(0).color;

        int index = getNearestLayer(part);

        return mixColors(
                layers.get(index).color, layers.get(index + 1).color,
                (part - layers.get(index).part) / (layers.get(index + 1).part - layers.get(index).part));
    }

    public int getNearestLayer(float part) {
        int index;
        for (index = 0; (part - layers.get(index).part) > 0; index++);
        return index -1;
    }

    public Color mixColors(Color color1, Color color2, float part) {
        if (part > 1) { return color2;}
        else if (part < 0) {return color1;}

        int c1 = color1.getRGB();
        int c2 = color2.getRGB();

        float a1 = ((c1 >> 24) & 0xff);
        float r1 = (c1 >> 16) & 0xff;
        float g1 = (c1 >> 8) & 0xff;
        float b1 = c1 & 0xff;
        float a2 = (c2 >> 24) & 0xff;
        float r2 = (c2 >> 16) & 0xff;
        float g2 = (c2 >> 8) & 0xff;
        float b2 = c2 & 0xff;

        return new Color((PApplet.round(a1 + (a2-a1)*part) << 24) |
                (PApplet.round(r1 + (r2-r1)*part) << 16) |
                (PApplet.round(g1 + (g2-g1)*part) << 8) |
                (PApplet.round(b1 + (b2-b1)*part)));
    }

    public void addLayer(Color color, float part) {
        layers.add(new ColorLayer(color, part));
        Collections.sort(layers, colorLayerComparator);
    }

    private static class ColorLayer {
        Color color;
        float part;

        public ColorLayer(Color color, Float part) {
            this.color = color;
            this.part = part;
        }
    }

    private class ColorLayerComparator implements Comparator<ColorLayer> {
        @Override
        public int compare(ColorLayer o1, ColorLayer o2) {
            return Float.compare(o1.part, o2.part);
        }
    }


}
