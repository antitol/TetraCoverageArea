package tetracoveragearea.gui.applet.map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.MapEventListener;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.common.entities.centralPart.GeometryObserver;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.entities.visualPart.OuterBoundingBoxMarker;
import tetracoveragearea.common.entities.visualPart.OuterShapeMarker;
import tetracoveragearea.common.entities.visualPart.PointMarkerRssi;
import tetracoveragearea.common.entities.visualPart.TriangleMarkerRssi;
import tetracoveragearea.gui.applet.MarkerChangeEvent;
import tetracoveragearea.serialDao.SerialTestDao;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 17.02.17.
 *
 * Карта покрытия
 */
public class CoverageMap extends UnfoldingMap implements MapEventListener, GeometryObserver {

    public static final Logger log = Logger.getLogger(SerialTestDao.class);

    // Диапазон значений rssi
    private static int minRssi = 20;
    private static int maxRssi = 100;

    private boolean markerRedrawing = true;

    private SimplePointMarker mouseClickedMarker;
    private OuterShapeMarker outerShapeMarker;
    private OuterBoundingBoxMarker boundingBoxMarker;

    // Менеджер вспомогательных маркеров (маркер-указатель, маркер внешней области)
    private EnhancedMarkerManager<Marker> mapToolsManager = new EnhancedMarkerManager<Marker>();

    // Менеджеры маркеров, добавление маркеров в менеджер означает добавление непосредственно на карту
    private EnhancedMarkerManager<Marker> pointsManager = new EnhancedMarkerManager<Marker>();
    private EnhancedMarkerManager<Marker> delauneyTrianglesManager = new EnhancedMarkerManager<Marker>();
    private EnhancedMarkerManager<Marker> voronoiPolygonsManager = new EnhancedMarkerManager<Marker>();
    private EnhancedMarkerManager<Marker> boundingBoxManager = new EnhancedMarkerManager<Marker>();

    public CoverageMap(PApplet pApplet, AbstractMapProvider provider) {

        super(pApplet, provider);

//        mapDisplay = new OpenGLMapDisplay(p, provider, PConstants.P2D, 0, 0, p.width, p.height);
        zoom(535f);
        panTo(new Location(55.0, 73.6));

        showMarkers(false, pointsManager);
        showMarkers(false, delauneyTrianglesManager);
        showMarkers(false, boundingBoxManager);

        addMarkerManager(mapToolsManager);
        addMarkerManager(pointsManager);
        addMarkerManager(delauneyTrianglesManager);
        addMarkerManager(voronoiPolygonsManager);
        addMarkerManager(boundingBoxManager);

        boundingBoxMarker = new OuterBoundingBoxMarker(-85,-180,85,180);
        boundingBoxManager.addMarker(boundingBoxMarker);

        setMouseClickedMarkerAt(getCenter());

        GeometryStore.getInstance().addGeometryListener(this);
    }

    /**
     * Устанавливает менеджера карты (например, тайлового менеджера TileMapProvider)
     *
     * @param provider
     */
    public void setMapProvider(AbstractMapProvider provider) {
        this.mapDisplay.setMapProvider(provider);
        onManipulation(new ZoomMapEvent(this, "", ""));
    }

    /**
     * Добавляет/удаляет лист маркеров на карту
     * При выполнении метода спаунится MarkerChangeEvent
     *
     * @param markers - маркеры
     */
    @Override
    public void addMarkers(List<Marker> markers) {

        mapDisplay.addMarkers(markers);
    }

    /**
     * Добавляет маркеры к указанному менеджеру маркеров
     * Спаунит событие обновления для карты
     * @param markers - маркеры
     * @param markerManager - менеджерМаркеров
     */
    public void addMarkers(List<? extends Marker> markers, EnhancedMarkerManager<Marker> markerManager) {

        markers.forEach(markerManager::addMarker);
        onManipulation(new MarkerChangeEvent(this, "addmarkers", "marker"));
    }

    /**
     * Добавляет маркеры точек на карту
     * @param markers
     */
    public void addDelaunayTriangles(List<TriangleMarkerRssi> markers) {

        addMarkers(markers, delauneyTrianglesManager);
    }

    /**
     * Подменяет список маркеров для указанного менеджера маркеров
     * @param markers - список маркеров
     * @param markerManager - менеджер маркеров
     */
    public void setMarkers(List<? extends Marker> markers, EnhancedMarkerManager<Marker> markerManager) {

            markerManager.clearMarkers();
            addMarkers(markers, markerManager);
    }

    /**
     * Устанавливает маркеры треугольников Делоне на карту
     * @param triangles
     */
    public void setDelaunayTriangles(List<TriangleMarkerRssi> triangles) {
        setMarkers(triangles, delauneyTrianglesManager);
    }

    /**
     * Отображать / скрыть маркеры указанного менеджера на карте
     * @param show
     * @param markerManager
     */
    public void showMarkers(boolean show, EnhancedMarkerManager<Marker> markerManager) {

        if (show) {
            markerManager.enableDrawing();
        } else {
            markerManager.disableDrawing();
        }

        onManipulation(new MarkerChangeEvent(this, "marker", "marker"));
    }

    /**
     * Отображать / скрыть точки
     * @param show
     */
    public void showPoints(boolean show) {

        showMarkers(show, pointsManager);
    }

    /**
     * Отображать / скрыть полигоны Делоне
     * @param show
     */
    public void showDelaunayTriangles(boolean show) {

        showMarkers(show, delauneyTrianglesManager);
    }

    public void showBoundingBox(boolean show) {

        showMarkers(show, boundingBoxManager);
    }

    /**
     * Удаляет список маркеров указанного менеджера маркеров
     * @param markerManager
     */
    public void clearMarkers(EnhancedMarkerManager<Marker> markerManager) {

        markerManager.clearMarkers();
        onManipulation(new MarkerChangeEvent(this, "removemarker", "marker"));
    }

    /**
     * Удаляет лист маркеров
     *
     * @param list - маркеры
     */
    public void removeMarkers(List<? extends Marker> list) throws NullPointerException {

        list.forEach(getDefaultMarkerManager()::removeMarker);
    }

    /**
     * Удаляет отдельный маркер с карты
     * @param marker
     */
    public void removeMarker(Marker marker) {
        getDefaultMarkerManager().removeMarker(marker);
    }

    /**
     * Список точек на карте
     * @return
     */
    public List<Marker> getPoints() {
        return pointsManager.getMarkers();
    }

    /**
     * Возвращает список треугольников Делоне
     * @return
     */
    public List<Marker> getDelauneyTriangles() {
        return delauneyTrianglesManager.getMarkers();
    }

    public EnhancedMarkerManager<Marker> getPointsManager() {
        return pointsManager;
    }

    public EnhancedMarkerManager<Marker> getDelauneyTrianglesManager() {
        return delauneyTrianglesManager;
    }

    public EnhancedMarkerManager<Marker> getVoronoiPolygonsManager() {
        return voronoiPolygonsManager;
    }

    public boolean isMarkerRedrawing() {
        return markerRedrawing;
    }

    public static void setMinRssi(int minRssi) {
        CoverageMap.minRssi = minRssi;
    }

    public static void setMaxRssi(int maxRssi) {
        CoverageMap.maxRssi = maxRssi;
    }

    public static int getMinRssi() {

        return minRssi;
    }

    public static int getMaxRssi() {
        return maxRssi;
    }

    public SimplePointMarker getMouseClickedMarker() {
        return mouseClickedMarker;
    }

    /**
     * Установка маркера клика мыши по карте
     * @param location
     */
    public void setMouseClickedMarkerAt(Location location) {

        try {
            mouseClickedMarker.setLocation(location);
        } catch (NullPointerException ex) {
            mouseClickedMarker = new SimplePointMarker(location);
            mapToolsManager.addMarker(mouseClickedMarker);
        }
        onManipulation(new MarkerChangeEvent(this, "changemarker", "marker"));
    }

    public OuterShapeMarker getOuterShapeMarker() {
        return outerShapeMarker;
    }

    /**
     * Установка маркера внешней границы триангуляции
     * @param outerShapeMarker
     */
    public void setOuterShapeMarker(OuterShapeMarker outerShapeMarker) {
        this.outerShapeMarker = outerShapeMarker;
    }

    /**
     * Переключение показа внешней границы триангуляции
     * @param show
     */
    public void showOuterShape(boolean show) {
        try {

            if (show) {
                mapToolsManager.addMarker(outerShapeMarker);
            } else {
                mapToolsManager.removeMarker(outerShapeMarker);
            }

        } catch (NullPointerException ex) {
            log.info("Маркер внешней области уже удален");
        }
    }

    /**
     * Генерация маркеров-точек из точек
     * @param points
     * @return
     */
    private List<PointMarkerRssi> parsePointMarkers(List<Point> points) {
        return points.stream().map(point -> new PointMarkerRssi(point)).collect(Collectors.toList());
    }

    /**
     * Генерация маркеров-треугольников из треугольников
     * @param triangles
     * @return
     */
    public List<TriangleMarkerRssi> parseDelaunayMarkers(List<Triangle> triangles) {

        return triangles.stream()
                .map(TriangleMarkerRssi::new)
                .collect(Collectors.toList());
    }

    public EnhancedMarkerManager<Marker> getMapToolsManager() {
        return mapToolsManager;
    }

    public EnhancedMarkerManager<Marker> getBoundingBoxManager() {
        return boundingBoxManager;
    }

    /**
     * Установка точек на карту
     * @param points
     */
    @Override
    public void setPoints(List<Point> points) {

        setMarkers(parsePointMarkers(points), pointsManager);
    }

    /**
     * Установка треугольников на карту
     * @param triangles
     */
    @Override
    public void setTriangles(List<Triangle> triangles) {

        setMarkers(parseDelaunayMarkers(triangles), delauneyTrianglesManager);
    }

    /**
     * Добавить точку на карту
     * @param point
     */
    @Override
    public boolean addPoint(Point point) {
        return pointsManager.addMarker(new PointMarkerRssi(point));
    }

    /**
     * Добавление точек на карту
     * @param points
     */
    @Override
    public void addPoints(List<Point> points) {

        addMarkers(parsePointMarkers(points), pointsManager);
    }

    /**
     * Добавление треугольников на карту
     * @param triangles
     */
    @Override
    public void addTriangles(List<Triangle> triangles) {

        addMarkers(parseDelaunayMarkers(triangles), delauneyTrianglesManager);
    }

    /**
     * Удаление точек с карты
     */
    @Override
    public void clearPoints() {

        clearMarkers(pointsManager);
    }

    /**
     * Удаление треугольников с карты
     */
    @Override
    public void clearTriangles() {

        clearMarkers(delauneyTrianglesManager);
    }

    /**
     * Устанавливает границы маркера фильтр-области по широте
     * @param minLat
     * @param maxLat
     */
    public void setLatitudeBounds(double minLat, double maxLat) {
        boundingBoxMarker.setLatitudeBounds(minLat, maxLat);
        onManipulation(new MarkerChangeEvent(this, "changemarker", "boundingBox"));
    }

    /**
     * Устанавливает границы маркера фильтр-области по долготе
     * @param minLon
     * @param maxLon
     */
    public void setLongitudeBounds(double minLon, double maxLon) {
        boundingBoxMarker.setLongitudeBounds(minLon, maxLon);
        onManipulation(new MarkerChangeEvent(this, "changemarker", "boundingBox"));
    }

    @Override
    public void setPoint(int index, Point point) {
        pointsManager.getMarkers().set(index, new PointMarkerRssi(point));
    }


    public class EnhancedMarkerManager<E extends Marker> extends MarkerManager {
        @Override
        public void draw() {

            if (!bEnableDrawing)
                return;

            try {
                Iterator<? extends Marker> markerIterator = markers.iterator();
                while (markerIterator.hasNext()) {
                    markerIterator.next().draw(map);
                }
            } catch (ConcurrentModificationException ex) {}
        }

        @Override
        public E getFirstHitMarker(float v, float v1) {

            E foundMarker = null;
            // NB: Markers should be ordered, e.g. by size ascending, i.e. big, medium, small

            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {

                E nextMarker = (E) iterator.next();

                if (nextMarker.isInside(map, v, v1)) {
                    foundMarker = nextMarker;
                    break;
                }
            }

            return foundMarker;
        }
    }
}

