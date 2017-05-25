package tetracoveragearea.common.entities.visualPart;

import tetracoveragearea.common.delaunay.Point;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * Полигон-маркер, хранит набор маркеров-точек
 *
 * Created by anatoliy on 17.02.17.
 */
public class PolygonMarkerRssi extends SimplePolygonMarker {

    protected List<Double> rssiValues = new ArrayList<Double>();

    public PolygonMarkerRssi(List<Location> list, List<Double> rssiValues) {

        super(list);
        this.rssiValues = rssiValues;
    }

    public PolygonMarkerRssi(List<Point> points) {

        super();

        locations = new ArrayList<Location>();

        for (Point point : points) {
            addLocation((float) point.getX(), (float) point.getY());
            rssiValues.add(point.getZ());
        }
    }

    public List<Double> getRssiValues() {
        return rssiValues;
    }

    public void setRssiValues(List<Double> rssiValues) {
        this.rssiValues = rssiValues;
    }
}
