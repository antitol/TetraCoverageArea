package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;

import java.util.HashMap;

/**
 * Created by anatoliy on 17.02.17.
 */
public class PointMarkerRssi extends SimplePointMarker {

    private float rssi;

    public PointMarkerRssi(float rssi) {
        this.rssi = rssi;
    }

    public PointMarkerRssi(Location location, float rssi) {
        super(location);
        this.rssi = rssi;
    }

    public PointMarkerRssi(Location location, HashMap<String, Object> hashMap, float rssi) {
        super(location, hashMap);
        this.rssi = rssi;
    }

    public float getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }
}
