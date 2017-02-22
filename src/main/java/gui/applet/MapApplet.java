package gui.applet;

import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.DebugDisplay;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

/**
 *
 * Главный класс processing апплета
 */
public class MapApplet extends PApplet{

    private static CoverageMap map;
    private DebugDisplay debugDisplay;

    private int stampFrameCount = 0;

    public void setup() {

        size(800, 600, OPENGL);
        smooth();

        map = new CoverageMap(this, new OpenStreetMap.OpenStreetMapProvider());

        MapUtils.createDefaultEventDispatcher(this, map);
        // Устанавливает FPS
        frameRate(30);
    }

    public void draw() {
        background(color(0,0,0));

            map.draw();

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

    /**
     * Метод вызывается при возникновении события MapEvent
     * Аналог реализации MapEventListener
     * TODO: вызывать тоже самое по добавлению / удалению маркеров
     * @param mapEvent
     */
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
