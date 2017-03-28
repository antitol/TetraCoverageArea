package tetracoveragearea.common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PConstants;
import processing.core.PGraphics;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.gui.applet.map.CoverageMap;
import tetracoveragearea.gui.tools.MultilayerGradient;

import java.util.Collections;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMarkerRssi extends PolygonMarkerRssi {

    private Triangle triangle;
    private Location centerPosition;

    public TriangleMarkerRssi(List<Location> locations, List<Double> rssiValues) {
        super(locations, rssiValues);
    }

    public TriangleMarkerRssi(Triangle triangle) {
        super(triangle.getRingPoints());
        this.triangle = triangle;
        Point centerPoint = triangle.getCentroid();
        centerPosition = new Location(centerPoint.getX(), centerPoint.getY());
    }

    /**
     * Среднее Rssi трех точек
     * @return
     */
    public double getAvgRssi() {
        return getRssiValues().stream().distinct().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    /**
     * Возвращает массив из 3х треугольников, разделенных в геометрическом центре данного треугольника
     * @return
     */
    public List<TriangleMarkerRssi> centropolate() {

        float sumLat = 0;
        float sumLong = 0;
        int size = getLocations().size() - 1;

        for (int i = 0; i < size; i++) {
            sumLat += getLocation(i).getLat();
            sumLong += getLocation(i).getLon();
        }

        float centLat = sumLat / size;
        float centLong = sumLong / size;

        Location centerLocation = new Location(centLat, centLong);

        Double centerRssi = getAvgRssi();

        return Collections.emptyList();

    }

    public Triangle getTriangle() {
        return triangle;
    }

    @Override
    public void draw(PGraphics pg, List<MapPosition> mapPositions) {

        float lastRssi = 0;
        MapPosition lastPosition = new MapPosition();
        int min = CoverageMap.getMinRssi();
        int max = CoverageMap.getMaxRssi();

        if (mapPositions.isEmpty() || isHidden())
            return;

        pg.pushStyle();
        pg.colorMode(PConstants.RGB, 1);

        // Без рамки
        pg.stroke(0,0,0, 127);
        pg.beginShape();

        for (int i = 0; i < mapPositions.size(); i++) {

            // Определяем вес точки по уровню сигнала
            Float rssi;

            rssi = (float) (getRssiValues().get(i) - min)/(max - min);

            if (i > 0) {
                Float halfRssi = (rssi + lastRssi) / 2f;
                mapPositions.get(i - 1).add(mapPositions.get(i));
                mapPositions.get(i - 1).div(2);

                pg.fill(MultilayerGradient.getInstance().getColor(halfRssi).getRGB(), 0.5f);
                pg.vertex(mapPositions.get(i - 1).x, mapPositions.get(i - 1).y);

            }

            // Заполняем цветом
            pg.fill(MultilayerGradient.getInstance().getColor(rssi).getRGB(), 0.5f);

            pg.vertex(mapPositions.get(i).x, mapPositions.get(i).y);



            lastRssi = rssi;
        }

        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }
}
