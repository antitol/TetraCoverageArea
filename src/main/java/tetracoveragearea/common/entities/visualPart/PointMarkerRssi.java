package tetracoveragearea.common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.gui.applet.map.CoverageMap;
import tetracoveragearea.gui.tools.MultilayerGradient;

/**
 * Маркер-точка
 * Created by anatoliy on 17.02.17.
 */
public class PointMarkerRssi extends SimplePointMarker {

    private Point point;
    private double rssi;

    public PointMarkerRssi(float rssi) {
        this.rssi = rssi;
    }


    public PointMarkerRssi(Point point) {
        super(new Location(point.getX(), point.getY()));
        this.rssi = point.getZ();
        this.point = point;
    }

    public double getRssi() {
        return rssi;
    }

    // Ссылка на точку для обратного преобразования
    public Point getPoint() {
        return point;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    /**
     * Рисование точки на карте
     * @param pg
     * @param x
     * @param y
     */
    @Override
    public void draw(PGraphics pg, float x, float y) {

        // Не рисовать скрытые
        if (isHidden())
            return;

        pg.pushStyle();
        pg.strokeWeight(strokeWeight);

        // Получаем диапазон значений rssi для расчета цвета
        int min = CoverageMap.getMinRssi();
        int max = CoverageMap.getMaxRssi();

        float rssi = this.rssi > max ? 1 : (float) (this.rssi - min)/(max - min);

        // Забираем цвет из класса градиента
        pg.fill(MultilayerGradient.getInstance().getColor(rssi).getRGB());
        pg.ellipse((int) x, (int) y, radius, radius);
        pg.popStyle();
    }

    public double getLatitude() {
        return location.getLat();
    }

    public double getLongitude() {
        return location.getLon();
    }
}
