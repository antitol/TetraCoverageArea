package tetracoveragearea.gui.tools;

/**
 * Created by anatoliy on 04.03.17.
 */

import org.apache.log4j.Logger;
import processing.core.PApplet;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Многослойный градиент (занимается выдачей цвета маркерам, в зависимости от значения уровня сигнала)
 */
public class MultilayerGradient implements Serializable {

    public static final Logger log = Logger.getLogger(MultilayerGradient.class);

    ColorLayerComparator colorLayerComparator = new ColorLayerComparator();

    // Список слоев обязательно должен быть отсортирован по возрастанию уровней сигнала
    private List<ColorLayer> layers;

    // Максимальное и минимальное значение, среди существующих слоев градиента
    private double maxPart;
    private double minPart;

    private String name = "";

    public MultilayerGradient() {
        this.layers = new ArrayList<>();
    }

    public MultilayerGradient(List<ColorLayer> layers) {
        this.layers = layers;
        sortLayers();
    }

    public MultilayerGradient(List<ColorLayer> layers, String name) {
        this.layers = layers;
        this.name = name;
        sortLayers();
    }

    /**
     * Возвращает цвет для определенного уровня сигнала
     * @param value
     * @return
     */
    public Color getColor(double value) {

        if (value >= maxPart) return layers.get(layers.size() - 1).color;
        if (value <= minPart) return layers.get(0).color;

        int index = getNearestLayer(value);

        return mixColors(
                layers.get(index).color, layers.get(index + 1).color,
                (float) (value - layers.get(index).part) / (layers.get(index + 1).part - layers.get(index).part));
    }

    /**
     * Ищет номер ближайшего слоя градиента в зависимости от значения
     * @param value - значение rssi
     * @return
     */
    public int getNearestLayer(double value) {
        int index;
        for (index = 0; (value - layers.get(index).part) > 0; index++);
        return index -1;
    }

    /**
     * Возвращает результат смешения двух цветов в определенной пропорции, линейная шкала изменения цвета
     * @param color1
     * @param color2
     * @param part - значение на линейной шкале от 0 до 1 (0 - полностью color1, 1 - полностью color2)
     * @return
     */
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

    /**
     * Добавляет слой к профилю градиента
     * @param color
     * @param part
     */
    public void addLayer(Color color, float part) {
        layers.add(new ColorLayer(color, part));
        sortLayers();
    }

    public ColorLayer getLayer(int layer) {
        return layers.get(layer);
    }

    public void setLayer(int index, ColorLayer colorLayer) {
        layers.set(index, colorLayer);
        sortLayers();
    }

    public void removeLayer(int index) {
        layers.remove(layers.get(index));
        sortLayers();
    }

    public List<ColorLayer> getLayers() {
        return layers;
    }

    /**
     * Сортировка слоев по уровню сигнала
     */
    public void sortLayers() {
        Collections.sort(layers, colorLayerComparator);
        if (layers.size() > 0) {
            minPart = layers.get(0).getPart();
            maxPart = layers.get(layers.size() - 1).getPart();
        }
    }

    public double getWidth(int index) {
        return (layers.get(index + 1).part - layers.get(index).part) / (maxPart - minPart);
    }

    public double getMaxPart() {
        return maxPart;
    }

    public double getMinPart() {
        return minPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает копию профиля градиента
     * @param multilayerGradient
     * @return
     */
    public static MultilayerGradient getCopy(MultilayerGradient multilayerGradient) {

        MultilayerGradient copiedMultilayerGradient = new MultilayerGradient();

        for (ColorLayer colorLayer : multilayerGradient.getLayers()) {
            copiedMultilayerGradient.addLayer(colorLayer.getColor(), colorLayer.getPart());
        }
        copiedMultilayerGradient.sortLayers();

        return copiedMultilayerGradient;
    }

    /**
     * Класс слоя градиента. Содержит цвет и соответсвующее этому цвету значению
     */
    public static class ColorLayer implements Serializable {
        Color color;
        float part;

        public ColorLayer(Color color, Float part) {
            this.color = color;
            this.part = part;
        }

        public Color getColor() {
            return color;
        }

        public float getPart() {
            return part;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setPart(float part) {
            this.part = part;
        }
    }

    /**
     * Компаратор для слоев
     */
    private class ColorLayerComparator implements Comparator<ColorLayer>, Serializable {
        @Override
        public int compare(ColorLayer o1, ColorLayer o2) {
            return Float.compare(o1.part, o2.part);
        }
    }


}
