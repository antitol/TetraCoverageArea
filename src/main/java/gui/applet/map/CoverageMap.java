package gui.applet.map;

import common.entities.visualPart.OuterShapeMarker;
import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.MapEventListener;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import gui.applet.MarkerChangeEvent;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import serialDao.SerialTestDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 17.02.17.
 *
 * Карта покрытия
 */
public class CoverageMap extends UnfoldingMap implements MapEventListener {

    public static final Logger log = Logger.getLogger(SerialTestDao.class);

    // Диапазон значений rssi
    private static int minRssi = 20;
    private static int maxRssi = 100;

    private boolean markerRedrawing = true;

    private SimplePointMarker mouseClickedMarker;
    private OuterShapeMarker outerShapeMarker;

    // Менеджер вспомогательных маркеров (маркер-указатель, маркер внешней области)
    private MarkerManager<Marker> mapToolsManager = new MarkerManager<Marker>();

    // Менеджеры маркеров, добавление маркеров в менеджер означает добавление непосредственно на карту
    private MarkerManager<Marker> pointsManager = new MarkerManager<Marker>();
    private MarkerManager<Marker> delauneyTrianglesManager = new MarkerManager<Marker>();
    private MarkerManager<Marker> voronoiPolygonsManager = new MarkerManager<Marker>();

    public CoverageMap(PApplet pApplet, AbstractMapProvider abstractMapProvider) {

        super(pApplet, abstractMapProvider);

        zoom(535f);
        panTo(new Location(55.0, 73.6));

        showMarkers(false, pointsManager);
        showMarkers(false, delauneyTrianglesManager);

        initMarkersFromDB();

        addMarkerManager(pointsManager);
        addMarkerManager(delauneyTrianglesManager);
        addMarkerManager(voronoiPolygonsManager);

        // Устанавливаем зум и перемещаем карту к указанным координатам
    }

    /**
     * Устанавливает менеджера карты (например, тайлового менеджера TileMapProvider)
     *
     * @param provider
     */
    public void setMapProvider(AbstractMapProvider provider) {
        this.mapDisplay.setMapProvider(provider);
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
     * @param markers - список маркеров
     * @param markerManager - менеджер маркеров
     */
    public void addMarkers(List<? extends Marker> markers, MarkerManager<Marker> markerManager) {

        markers.forEach(markerManager::addMarker);
        onManipulation(new MarkerChangeEvent(this, "addmarkers", "marker"));
    }

    /**
     * Добавляет маркеры точек на карту
     * @param markers
     */
    public void addPoints(List<PointMarkerRssi> markers) {

        addMarkers(markers, pointsManager);
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
    public void setMarkers(List<? extends Marker> markers, MarkerManager<Marker> markerManager) {

            markerManager.clearMarkers();
            addMarkers(markers, markerManager);
    }

    /**
     * Устанавливает маркеры точек на карту
     * @param points
     */
    public void setPoints(List<PointMarkerRssi> points) {
        setMarkers(points, pointsManager);
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
    public void showMarkers(boolean show, MarkerManager<Marker> markerManager) {

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

    /**
     * Удаляет список маркеров указанного менеджера маркеров
     * @param markerManager
     */
    public void clearMarkers(MarkerManager<Marker> markerManager) {

        markerManager.clearMarkers();
    }

    /**
     * Удаляет точки из менеджера маркеров
     */
    public void clearPoints() {
        clearMarkers(pointsManager);
    }

    /**
     * Удаляет треугольники Делоне из менеджера маркеров
     */
    public void clearDelaunayTriangles() {
        clearMarkers(delauneyTrianglesManager);
    }

    /**
     * Интерполирует и выводит интерполированные треугольники Делоне
     *
     * @param area - площадь в кв.км
     */
    public void delaunayInterpolation(double area) {

        setMarkers(
                getInterpolateTriangles(delauneyTrianglesManager.getMarkers()
                    .stream()
                    .map(t -> (TriangleMarkerRssi) t)
                    .collect(Collectors.toList()),
                area),
                delauneyTrianglesManager
        );
    }

    /**
     * Производит интерполяцию треугольников путем их рекурсивного разбиения до требуемой площади
     *
     * @param delauneyTriangles - массив, подвергаемый интерполяции
     * @return
     */
    public List<TriangleMarkerRssi> getInterpolateTriangles(List<TriangleMarkerRssi> delauneyTriangles, double area) {

        List<TriangleMarkerRssi> interpolateTriangles = new ArrayList<TriangleMarkerRssi>();

        for (TriangleMarkerRssi triangle : delauneyTriangles) {

                if (SerialTestDao.getInstance().getArea(triangle) > area) {

                    interpolateTriangles.addAll(getInterpolateTriangles(triangle.centropolate(), area));
                } else {

                    interpolateTriangles.add(triangle);
                }
        }

        return interpolateTriangles;
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

    public void initMarkersFromDB() {

        setPoints(SerialTestDao.getInstance().getPoints());
        setDelaunayTriangles(SerialTestDao.getInstance().getDelaunayTriangles());
        setOuterShapeMarker(SerialTestDao.getInstance().getDelaunayBoundary());
    }

    /**
     * Возвращает список треугольников Делоне
     * @return
     */
    public List<Marker> getDelauneyTriangles() {
        return delauneyTrianglesManager.getMarkers();
    }

    public MarkerManager<Marker> getPointsManager() {
        return pointsManager;
    }

    public MarkerManager<Marker> getDelauneyTrianglesManager() {
        return delauneyTrianglesManager;
    }

    public MarkerManager<Marker> getVoronoiPolygonsManager() {
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

    public OuterShapeMarker getOuterShapeMarker() {
        return outerShapeMarker;
    }

    public void setMouseClickedMarker(SimplePointMarker mouseClickedMarker) {
        this.mouseClickedMarker = mouseClickedMarker;
    }

    public void setOuterShapeMarker(OuterShapeMarker outerShapeMarker) {
        this.outerShapeMarker = outerShapeMarker;
    }

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

    public MarkerManager<Marker> getMapToolsManager() {
        return mapToolsManager;
    }
}

