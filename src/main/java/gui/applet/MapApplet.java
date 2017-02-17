package gui.applet;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapPosition;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import serialDao.SerialTestDao;

import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * Главный класс processing апплета
 */
public class MapApplet extends PApplet{

    UnfoldingMap map;
    SimplePolygonMarker marker1;

    public void setup() {

        size(800, 600, OPENGL);
        smooth();

        map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());

        MapUtils.createDefaultEventDispatcher(this, map);

        SerialTestDao.getInstance().getTriangles().forEach(p -> map.addMarkers(p));

        map.addMarker(new SimplePolygonMarker(
                Arrays.asList(new Location(20, 30),
                        new Location(30, 40),
                        new Location(30, 30),
                        new Location(20, 30))
        ));
    }

    public void draw() {
        background(160);
        map.draw();
    }

    public void keyPressed() {
        if (key == ' ') {
            map.getDefaultMarkerManager().toggleDrawing();
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[] {MapApplet.class.getName()});
    }

    public class JsonPolygonMarker extends SimplePolygonMarker {

        private Float rssi;

        JsonPolygonMarker(SimplePolygonMarker marker, Float rssi) {
            super(marker.getLocations());

            this.rssi = rssi;
        }

        @Override
        public void draw(PGraphics pg, List<MapPosition> mapPositions) {
            if (mapPositions.isEmpty() || isHidden())
                return;

            pg.pushStyle();
            pg.strokeWeight(strokeWeight);
            if (isSelected()) {
                pg.fill(highlightColor);
                pg.stroke(highlightStrokeColor);
            } else {
                pg.fill(255*rssi, 255 - 255*rssi, 50,90);
                pg.stroke(strokeColor);
            }


            pg.beginShape();
            for (MapPosition pos : mapPositions) {
                pg.vertex(pos.x, pos.y);
            }

            pg.textAlign(PConstants.CENTER);
            pg.endShape(PConstants.CLOSE);
            pg.popStyle();
        }
    }
}
