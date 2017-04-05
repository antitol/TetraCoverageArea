package tetracoveragearea.common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by anatoliy on 03.04.17.
 */
public class OuterBoundingBoxMarker extends OuterShapeMarker {

    private double minLat = 0;
    private double minLon = 0;
    private double maxLat = 0;
    private double maxLon = 0;


    public OuterBoundingBoxMarker(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;

        setBounds(minLat, minLon, maxLat, maxLon);
    }

    public void setBounds(double minLat, double minLon, double maxLat, double maxLon) {

        this.locations = new ArrayList<>(Arrays.asList(
                new Location(minLat, minLon),
                new Location(minLat, maxLon),
                new Location(maxLat, maxLon),
                new Location(maxLat, minLon),
                new Location(minLat, minLon)
        ));
    }

    public void setLatitudeBounds(double minLat, double maxLat) {

        this.minLat = minLat;
        this.maxLat = maxLat;

        setBounds(minLat, minLon, maxLat, maxLon);
    }

    public void setLongitudeBounds(double minLon, double maxLon) {

        this.minLon = minLon;
        this.maxLon = maxLon;

        setBounds(minLat, minLon, maxLat, maxLon);
    }

    public OuterBoundingBoxMarker() {
    }
}
