package tetracoveragearea.common.entities.visualPart;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Маркер внешней области зоны покрытия (в которой нет данных)
 *
 * Created by anatoliy on 28.02.17.
 */
public class OuterShapeMarker extends SimplePolygonMarker {

    public static List<Location> getMapBounds() {
        return mapBounds;
    }

    private static final List<Location> mapBounds = Arrays.asList(
            new Location(-85, -180),
            new Location(85, -180),
            new Location(85, 180),
            new Location(-85, 180),
            new Location(-85, -180)
    );

    public OuterShapeMarker(List<Location> list) {
        super(list);
    }

    public OuterShapeMarker() {
        super();
    }

    @Override
    protected void draw(PGraphics pg, List<MapPosition> list, HashMap<String, Object> hashMap, UnfoldingMap map) {

        pg.pushStyle();
        pg.beginShape();

        Image image;



        pg.fill(255, 0,0, 127);

        // Внешние границы карты
        for (Location loc : getMapBounds()) {
            float[] xy = map.mapDisplay.getObjectFromLocation(loc);
            pg.vertex(xy[0], xy[1]);
        }

        // Границы указанного полигона
        pg.beginContour();
        for (int i = 0; i < list.size(); i++) {

            pg.vertex(list.get(i).x, list.get(i).y);
        }
        pg.endContour();
        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }

    @Override
    public void draw(PGraphics pg, List<MapPosition> list) {



    }
}
