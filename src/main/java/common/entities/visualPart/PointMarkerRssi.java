package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import gui.applet.map.CoverageMap;
import processing.core.PGraphics;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by anatoliy on 17.02.17.
 */
public class PointMarkerRssi extends SimplePointMarker {

    private double rssi;

    public PointMarkerRssi(float rssi) {
        this.rssi = rssi;
    }

    public PointMarkerRssi(Location location, double rssi) {
        super(location);
        this.rssi = rssi;
    }

    public PointMarkerRssi(Location location, HashMap<String, Object> hashMap, float rssi) {
        super(location, hashMap);
        this.rssi = rssi;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    @Override
    public void draw(PGraphics pg, float x, float y) {
        if (isHidden())
            return;

        pg.pushStyle();
        pg.strokeWeight(strokeWeight);

        int min = CoverageMap.getMinRssi();
        int max = CoverageMap.getMaxRssi();

        float rssi = this.rssi > max ? 1 : (float) (this.rssi - min)/(max - min);

        if (rssi < 0.5) {

            setColor(pg.lerpColor(Color.GREEN.getRGB(), Color.YELLOW.getRGB(), rssi*2));
        } else {
            setColor(pg.lerpColor(Color.YELLOW.getRGB(), Color.RED.getRGB(), (rssi - 0.5f)*2));
        }

        pg.fill(color);
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
