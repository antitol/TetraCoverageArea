package tetracoveragearea.gui.applet.map;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;
import processing.core.PFont;
import tetracoveragearea.common.entities.visualPart.TriangleMarkerRssi;
import tetracoveragearea.gui.components.GuiComponents;

/**
 * Created by anatoliy on 05.04.17.
 */
public class InformationDisplay {

    float width = 200;
    float height = 150;

    float markWidth = 15;
    float markHeight = 15;

    int textSize = 12;
    int textColor = 0xFFFFFFFF;

    int backgroundColor = 0x77222222;
    int swithOffColor = 0x77FF2222;
    int swithOnColor = 0x7722FF22;

    int rowSpace = 18;

    int x = 0;
    int y = 0;

    int margin = 5;
    int padding = 10;

    float xText = x + margin;
    float xValue = xText + 100;
    float xMarker = x + width - margin - markWidth;

    PFont font;

    PApplet p;
    CoverageMap map;

    public InformationDisplay(PApplet pApplet, CoverageMap map) {
        this.p = pApplet;
        this.map = map;

        font = p.createFont("SansSerif.bold", 12);
    }

    public void draw() {

        p.noStroke();
        p.fill(backgroundColor);
        p.rect(x, y, width, height);

        p.textSize(textSize);
        p.textFont(font);

        p.fill(textColor);
        p.text("Точки:", xText, getRowY(1));
        p.text("Полигоны:", xText, getRowY(2));
        p.text(map.getPoints().size(), xValue, getRowY(1));
        p.text(map.getDelauneyTriangles().size(), xValue, getRowY(2));

        p.fill(map.getPointsManager().isDrawingEnabled() ? swithOnColor : swithOffColor);
        p.ellipse(xMarker, getMarkerRowY(1) , markWidth, markHeight);

        p.fill(map.getDelauneyTrianglesManager().isDrawingEnabled() ? swithOnColor : swithOffColor);
        p.ellipse(xMarker, getMarkerRowY(2), markWidth, markHeight);

        p.fill(textColor);
        p.textSize(11);
        p.text("Указанные координаты:", xText, getRowY(3));
        Location mouseClickedLocation = map.getMouseClickedMarker().getLocation();
        ScreenPosition screenPosition = map.getScreenPosition(mouseClickedLocation);

        TriangleMarkerRssi markerRssi = (TriangleMarkerRssi) map.getDelauneyTrianglesManager().getFirstHitMarker(screenPosition.x, screenPosition.y);

        p.textSize(12);
        p.text("Широта:", xText + 10, getRowY(4));
        p.text("Долгота:", xText + 10, getRowY(5));
        p.text(mouseClickedLocation.getLat(), xText + 90, getRowY(4));
        p.text(mouseClickedLocation.getLon(), xText + 90, getRowY(5));
        p.text("Уровень сигнала:", xText, getRowY(6));
        p.text(
                markerRssi == null ?
                        "Нет данных" :
                        "-" + GuiComponents.getRssiFormatter().format(markerRssi.getAvgRssi()) + " дБм",
                xText, getRowY(7));
    }

    public float getRowY(int row) {
        return x + margin + row*rowSpace;
    }

    public float getMarkerRowY(int row) {
        return getRowY(row) - 5;
    }
}
