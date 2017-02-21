package gui.applet;

import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import processing.core.PApplet;
import serialDao.SerialTestDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class CoverageMap extends UnfoldingMap {

    private static int minRssi = 20;
    private static int maxRssi = 100;

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

    private List<PointMarkerRssi> points;
    private List<TriangleMarkerRssi> delauneyTriangles;
    private List<PolygonMarkerRssi> voronoiPolygons;

    private List<TriangleMarkerRssi> interpolateDelaunayTriangles;

    public CoverageMap(PApplet pApplet, AbstractMapProvider abstractMapProvider) {
        super(pApplet, abstractMapProvider);

        zoom(535f);
        panTo(new Location(55.0, 73.6));
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
     * Показывать точки
     *
     * @param show
     */
    public void enablePoints(boolean show) {
        enableMarkers(show, points);
    }

    /**
     * Показывать триангуляцию Делоне
     *
     * @param show
     */
    public void enabelDelaunayTriangles(boolean show) {
        enableMarkers(show, delauneyTriangles);
    }

    /**
     * Показывать полигоны Вороного
     *
     * @param show
     */
    public void enabelVoronoiPolygons(boolean show) {
        enableMarkers(show, voronoiPolygons);
    }

    /**
     * Добавляет/удаляет лист маркеров на карту
     *
     * @param show - добавить/удалить
     * @param markers - маркеры
     */
    public void enableMarkers(boolean show, List<? extends Marker> markers) {
        try {
            if (show) {
                    markers.forEach(p -> this.addMarkers(p));
            } else {
                removeMarkers(markers);
            }
        } catch (Exception ex) {

        }
    }

    public void startInterpolationDelaunay(double area) {

        enableMarkers(false, interpolateDelaunayTriangles);

        interpolateDelaunayTriangles = new ArrayList<TriangleMarkerRssi>();
        interpolateTriangles(delauneyTriangles, area);

        System.out.println(interpolateDelaunayTriangles.size());

        enableMarkers(true, interpolateDelaunayTriangles);

    }

    /**
     * Производит интерполяцию треугольников путем их разбиения до требуемой площади
     *
     * @param delauneyTriangles
     * @return
     */
    public void interpolateTriangles(List<TriangleMarkerRssi> delauneyTriangles, double area) {

        for (TriangleMarkerRssi triangle : delauneyTriangles) {

                if (SerialTestDao.getInstance().getArea(triangle) > area) {
                    interpolateTriangles(triangle.centropolate(), area);
                } else {
                    interpolateDelaunayTriangles.add(triangle);
                }
        }
    }

    /**
     * Удаляет лист маркеров
     *
     * @param list - маркеры
     */
    public void removeMarkers(List<? extends Marker> list) throws NullPointerException {
        list.forEach(getDefaultMarkerManager()::removeMarker);
    }

    public List<PointMarkerRssi> getPoints() {
        return points;
    }

    public List<TriangleMarkerRssi> getDelauneyTriangles() {
        return delauneyTriangles;
    }

    public List<PolygonMarkerRssi> getVoronoiPolygons() {
        return voronoiPolygons;
    }

    public void setPoints(List<PointMarkerRssi> points) {
        this.points = points;
    }

    public void setDelauneyTriangles(List<TriangleMarkerRssi> delauneyTriangles) {
        this.delauneyTriangles = delauneyTriangles;
    }

    public void setVoronoiPolygons(List<PolygonMarkerRssi> voronoiPolygons) {
        this.voronoiPolygons = voronoiPolygons;
    }
}

