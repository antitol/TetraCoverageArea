package unfolding;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.util.Arrays;

/**
 * Displays countries of the world as simple polygons.
 *
 * Reads from a GeoJSON file, and uses default marker creation. Features are polygons.
 *
 * Press SPACE to toggle visibility of the polygons.
 */
public class SimpleMapApp extends PApplet {

    UnfoldingMap map;
    Marker marker1;

    public void setup() {

        map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        marker1 = new SimplePolygonMarker(Arrays.asList(
                new Location(13, 15),
                new Location(13, 20),
                new Location(17, 20),
                new Location(17, 15),
                new Location(13, 15)));
        map.addMarkers(marker1);
    }

    public void draw() {
        background(160);
        map.draw();
        marker1.draw(map);
    }

    public void keyPressed() {
        if (key == ' ') {
            map.getDefaultMarkerManager().toggleDrawing();
        }
    }

    public void settings() {
        size(800, 600);
    }
}
