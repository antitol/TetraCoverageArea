package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapPosition;
import gui.applet.CoverageMap;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMarkerRssi extends PolygonMarkerRssi {

    private PointMarkerRssi pointA;
    private PointMarkerRssi pointB;
    private PointMarkerRssi pointC;

    List<Float> rssiValues;

    public TriangleMarkerRssi(PointMarkerRssi pointA, PointMarkerRssi pointB, PointMarkerRssi pointC) {

        super();

        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;

        setLocations(
                Arrays.asList(
                        pointA.getLocation(),
                        pointB.getLocation(),
                        pointC.getLocation(),
                        pointA.getLocation()
                )
        );

        rssiValues = Arrays.asList(pointA.getRssi(), pointB.getRssi(), pointC.getRssi(), pointA.getRssi());

        System.out.println(GeoUtils.getArea(this));
    }

    public TriangleMarkerRssi(Location p1, Location p2, Location p3, int rssi1, int rssi2, int rssi3) {

        super(Arrays.asList(p1, p2, p3, p1), (rssi1 + rssi2 + rssi3)/3);

        this.pointA = new PointMarkerRssi(p1, rssi1);
        this.pointB = new PointMarkerRssi(p2, rssi2);
        this.pointC = new PointMarkerRssi(p3, rssi3);
    }


    public float getAvgRssi() {
        return (pointA.getRssi() + pointB.getRssi() + pointC.getRssi())/3;
    }

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

        PointMarkerRssi centerPoint = new PointMarkerRssi(
                new Location(centLat, centLong), getAvgRssi());

        return Arrays.asList(
                new TriangleMarkerRssi(pointA, pointB, centerPoint),
                new TriangleMarkerRssi(pointA, pointC, centerPoint),
                new TriangleMarkerRssi(pointB, pointC, centerPoint)
        );
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
        pg.noStroke();
        pg.beginShape();

        for (int i = 0; i < mapPositions.size(); i++) {

            // Определяем вес точки по уровню сигнала
            float rssi = rssiValues.get(i) > max ? 1 : (rssiValues.get(i) - min)/(max - min);


            if (rssi < 0.5) {

                setColor(pg.lerpColor(Color.GREEN.getRGB(), Color.YELLOW.getRGB(), rssi*2));
            } else {
                setColor(pg.lerpColor(Color.YELLOW.getRGB(), Color.RED.getRGB(), (rssi - 0.5f)*2));
            }

            pg.fill(color, 0.5f);

            pg.vertex(mapPositions.get(i).x, mapPositions.get(i).y);

                // Заполняем цветом


        }

        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }

}
