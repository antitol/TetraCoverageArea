package tetracoveragearea.gui.applet;

import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import tetracoveragearea.gui.applet.map.CoverageMap;
import tetracoveragearea.gui.applet.map.InformationDisplay;
import tetracoveragearea.gui.panels.MainContentPanel;

//import tetracoveragearea.common.delaunay.Point;
//import tetracoveragearea.common.entities.centralPart.GeometryStore;

/**
 *
 * Главный класс processing апплета
 */
public class MapApplet extends PApplet {

    public static final Logger log = Logger.getLogger(MapApplet.class);

    private CoverageMap map;
    private InformationDisplay informationDisplay;

    private Location mouseClickedLocation;

    private boolean manualPointInput = false;

    private int stampFrameCount = 0;

    public static MapApplet instance = new MapApplet();

    public static MapApplet getInstance() {
        return instance;
    }

    public void setup() {

        size(800, 600, P2D);

        map = new CoverageMap(this, new OpenStreetMap.OpenStreetMapProvider());
        informationDisplay = new InformationDisplay(this, map);

        log.info("Создана карта");
        log.info(getSize().getHeight() + "x" + getSize().getWidth());
        MapUtils.createDefaultEventDispatcher(this, map);
        // Устанавливает FPS
        frameRate(30);

        mouseClickedLocation = map.getCenter();
    }

    public void draw() {

        background(color(255,255,255));

        map.draw();
        informationDisplay.draw();

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

        mouseClickedLocation = map.getLocation(mouseX, mouseY);

        // Если выбран режим ручного ввода точек, то они добавляются в хранилище
        if (manualPointInput) {
            MainContentPanel.getInstance().getTestDataInputPanel().addManualPoint(
                    mouseClickedLocation
            );
        }

        map.setMouseClickedMarkerAt(
                mouseClickedLocation
        );
    }

    /**
     * Метод вызывается при возникновении события MapEvent
     * Аналог реализации MapEventListener
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

    public CoverageMap getMap() {

        return map;
    }

    public static void main(String[] args) {
        PApplet.main(MapApplet.class.getName());
    }

    public Location getMouseClickedLocation() {
        return mouseClickedLocation;
    }

    public void setManualPointInput(boolean manualInput) {
        manualPointInput = manualInput;
    }
}
