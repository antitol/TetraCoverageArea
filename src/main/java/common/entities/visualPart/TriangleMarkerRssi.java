package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMarkerRssi extends SimplePolygonMarker {

    private static int maxRssi;
    private static int minRssi;

    private PointMarkerRssi pointA;
    private PointMarkerRssi pointB;
    private PointMarkerRssi pointC;

    private int id;

    public TriangleMarkerRssi(List<PointMarkerRssi> points) {

        /* Загружаем в simplepolygon координаты всех точек */
        super(
                points.stream()
                        .map(p -> p.getLocation())
                        .collect(Collectors.toList())
        );

        this.pointA = points.get(0);
        this.pointB = points.get(1);
        this.pointC = points.get(2);
    }

    public TriangleMarkerRssi(PointMarkerRssi pointA, PointMarkerRssi pointB, PointMarkerRssi pointC) {

        super(Arrays.asList(pointA.getLocation(), pointB.getLocation(), pointC.getLocation()));

        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
    }

    public TriangleMarkerRssi(Location p1, Location p2, Location p3, int rssi1, int rssi2, int rssi3) {

        super(Arrays.asList(p1, p2, p3, p1));

        this.pointA = new PointMarkerRssi(p1, rssi1);
        this.pointB = new PointMarkerRssi(p2, rssi2);
        this.pointC = new PointMarkerRssi(p3, rssi3);
    }

    public static int getMaxRssi() {
        return maxRssi;
    }

    public static int getMinRssi() {
        return minRssi;
    }

    public static void setMaxRssi(int maxRssi) {
        TriangleMarkerRssi.maxRssi = maxRssi;
    }

    public static void setMinRssi(int minRssi) {
        TriangleMarkerRssi.minRssi = minRssi;
    }

    public float getAvgRssi() {
        return (pointA.getRssi() + pointB.getRssi() + pointC.getRssi())/3;
    }

    public List<TriangleMarkerRssi> centropolate() {

        float centLat = 0;
        float centLong = 0;
        int size = getLocations().size();

        for (Location l : getLocations()) {
            centLat += l.getLat();
            centLong += l.getLon();
        }

        centLat /= size;
        centLong /= size;


        PointMarkerRssi centerPoint = new PointMarkerRssi(
                new Location(centLat, centLong), getAvgRssi());

        return Arrays.asList(
                new TriangleMarkerRssi(pointA, pointB, centerPoint),
                new TriangleMarkerRssi(pointA, pointC, centerPoint),
                new TriangleMarkerRssi(pointB, pointC, centerPoint)
        );
    }

    public void draw(PGraphics pg, List<MapPosition> mapPositions) {

        float rssi = getAvgRssi() > 80 ? 1 : (getAvgRssi() - 20)/60;

        if (mapPositions.isEmpty() || isHidden())
            return;

        pg.pushStyle();
        pg.strokeWeight(strokeWeight);
        if (isSelected()) {
            pg.fill(highlightColor);
            pg.stroke(highlightStrokeColor);
        } else {
            pg.fill(255*rssi, 255 - 255*rssi, 0,90);
            pg.stroke(strokeColor);
        }


        pg.beginShape();
        for (MapPosition pos : mapPositions) {
            pg.vertex(pos.x, pos.y);
        }
        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }

}
