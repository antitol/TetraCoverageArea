package gui.applet;

import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.DebugDisplay;
import de.fhpotsdam.unfolding.utils.MapUtils;
import gui.applet.map.CoverageMap;
import gui.applet.map.ToolMarkers;
import org.apache.log4j.Logger;
import processing.core.PApplet;

/**
 *
 * Главный класс processing апплета
 */
public class MapApplet extends PApplet{

    public static final Logger log = Logger.getLogger(MapApplet.class);

    private static CoverageMap map;
    private DebugDisplay debugDisplay;

    private int stampFrameCount = 0;

    public void setup() {

        size(800, 600, P2D);
        map = new CoverageMap(this, new OpenStreetMap.OpenStreetMapProvider());

        MapUtils.createDefaultEventDispatcher(this, map);
        // Устанавливает FPS
        frameRate(30);
    }

    public void draw() {
        background(color(0,0,0));

        map.draw();
        log.info(frameCount);

        // Останавливает перерисовку
        if (looping && frameCount - stampFrameCount > 30) {
            noLoop();
        }
    }

    /**
     * Аналог KeyEventListener
     */
    public void keyPressed() {
        if (key == ' ') {
            map.getDefaultMarkerManager().toggleDrawing();
        }
    }

    public void mousePressed() {
        try {
            map.enableMarker(false,
                    ToolMarkers.getInstence().getMouseClicked()
            );
        } catch (NullPointerException ex) {}
        ToolMarkers.getInstence().setMouseClicked(
                new SimplePointMarker(map.getLocation(mouseX, mouseY))
        );
        map.enableMarker(true,
                ToolMarkers.getInstence().getMouseClicked()
        );
    }

    /**
     * Метод вызывается при возникновении события MapEvent
     * Аналог реализации MapEventListener
     * @param mapEvent
     */
    // TODO: разобраться, почему не проходит MapEvent при вызове точек или полигонов
    public void mapChanged(MapEvent mapEvent) {

        // При изменении масштаба тайлы не успевают прогрузиться за одну отрисовку, выделяется 30 кадров
        if (mapEvent.getType().equals(ZoomMapEvent.TYPE_ZOOM)) {

            loop();
        }

        redraw();
        stampFrameCount = frameCount;
    }

    public static CoverageMap getMap() {

        return map;
    }




}
