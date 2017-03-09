package gui.applet;

import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.DebugDisplay;
import de.fhpotsdam.unfolding.utils.MapUtils;
import gui.applet.map.CoverageMap;
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

        map = new CoverageMap(this, new OpenStreetMap.OpenStreetMapProvider());
        size(800, 600, P2D);

        log.info("Создана карта");
        log.info(getSize().getHeight() + "x" + getSize().getWidth());
        MapUtils.createDefaultEventDispatcher(this, map);
        // Устанавливает FPS
        frameRate(30);
    }

    public void draw() {
        background(color(0,0,0));

        map.draw();
//        log.info(frameCount);

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

    public void mouseMoved() {

        /*try {
            Marker marker = map.getPointsManager().getNearestMarker(mouseX, mouseY);
            log.info(((PointMarkerRssi) marker).getRssi());
        } catch (NullPointerException ex) {
            log.info("Нет данных о rssi");
        }*/
    }

    public void mousePressed() {
        try {

            map.removeMarker(map.getMouseClickedMarker());
        } catch (NullPointerException ex) {}

        map.setMouseClickedMarker(
                new SimplePointMarker(map.getLocation(mouseX, mouseY))
        );

        map.addMarker(map.getMouseClickedMarker());

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

    public static void main(String[] args) {
        PApplet.main(new String[] {MapApplet.class.getName()});
    }

}
