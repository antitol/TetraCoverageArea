package gui.applet;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.DebugDisplay;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

/**
 *
 *
 * Главный класс processing апплета
 */
public class MapApplet extends PApplet{

    private static CoverageMap map;
    private DebugDisplay debugDisplay;

    public void setup() {

        size(800, 600, OPENGL);
        smooth();

        map = new CoverageMap(this, new OpenStreetMap.OpenStreetMapProvider());

        MapUtils.createDefaultEventDispatcher(this, map);
        debugDisplay = new DebugDisplay(this, map);

    }

    public void draw() {
        background(color(0,0,0));

            map.draw();

            debugDisplay.draw();
    }

    public void mouseClicked() {
        Marker marker = map.getFirstHitMarker(mouseX, mouseY);
        if (marker != null) {
            map.zoomAndPanToFit(GeoUtils.getLocations(marker));
        } else {
            map.zoomAndPanTo(2, new Location(0, 0));
        }
    }

    // Проверить что это делает по нажатию пробела
    public void keyPressed() {
        if (key == ' ') {
            map.getDefaultMarkerManager().toggleDrawing();
        }
    }

    public void setMap(CoverageMap map) {
        this.map = map;
    }

    public static CoverageMap getMap() {

        return map;
    }


}
