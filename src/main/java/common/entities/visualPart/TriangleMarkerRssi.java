package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapPosition;
import gui.applet.map.CoverageMap;
import gui.tools.MultilayerGradient;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Collections;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMarkerRssi extends PolygonMarkerRssi {

    private PointMarkerRssi pointA;
    private PointMarkerRssi pointB;
    private PointMarkerRssi pointC;


    public TriangleMarkerRssi(List<Location> locations, List<Double> rssiValues) {
        super(locations, rssiValues);
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

    @Override
    public void draw(PGraphics pg, List<MapPosition> mapPositions) {

        int min = CoverageMap.getMinRssi();
        int max = CoverageMap.getMaxRssi();

        if (mapPositions.isEmpty() || isHidden())
            return;

        pg.pushStyle();
        pg.colorMode(PConstants.RGB, 1);

        // Без рамки
        pg.stroke(0,0,0);
        pg.beginShape();

        for (int i = 0; i < mapPositions.size(); i++) {

            // Определяем вес точки по уровню сигнала
            Float rssi;

            rssi = (float) (getRssiValues().get(i) - min)/(max - min);

            pg.fill(MultilayerGradient.getInstance().getColor(rssi).getRGB(), 0.5f);

            pg.vertex(mapPositions.get(i).x, mapPositions.get(i).y);

                // Заполняем цветом
        }

        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }
}
