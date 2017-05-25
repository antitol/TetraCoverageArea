package tetracoveragearea.common.entities.centralPart;

import org.apache.log4j.Logger;
import tetracoveragearea.common.delaunay.DelaunayTriangulation;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.gui.applet.MapApplet;
import tetracoveragearea.gui.panels.filterPanels.Filter;
import tetracoveragearea.serialDao.SerialTestDao;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Предоставляет методы для взаимодействия с хранилищем точек и полигонов
 * Created by anatoliy on 14.03.17.
 */
public class GeometryStore implements GeometryObservable {

    public static final Logger log = Logger.getLogger(SerialTestDao.class);

    private boolean filtering = false;
    private boolean mapDrawing = false;

    private List<Point> points = new ArrayList<>();
    private List<Triangle> triangles = new ArrayList<Triangle>();
    private DelaunayTriangulation delaunayTriangulation = new DelaunayTriangulation();
    private DelaunayTriangulation filterDelaunayTriangulation = new DelaunayTriangulation();

    List<GeometryObserver> geometryObservers = new LinkedList<>();

    // Набор точек хранилища, хранит пересечение множеств точек, которые выдали фильтры
    private List<Point> filterPoints = new ArrayList<Point>(points);

    private List<Triangle> filterTriangles = new ArrayList<>();

    private static GeometryStore ourInstance = new GeometryStore();

    public static GeometryStore getInstance() {
        return ourInstance;
    }

    private GeometryStore() {
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    /**
     * Обновляет триангуляцию
     */
    public void refreshTriangles() {

        triangles = delaunayTriangulation.getTriangulation();
    }

    /**
     * Обновляет точки покрытия
     * @param points
     */
    public void setPoints(List<Point> points) {
        this.points = new ArrayList<>(points);
        delaunayTriangulation = new DelaunayTriangulation(points);
        refreshTriangles();
        notifyOnSetPoints(points);
        notifyOnSetTriangles(triangles);
    }

    /**
     * Добавляет точки покрытия к уже имеющимся
     * @param points
     */
    public void addPoints(List<Point> points) {
        this.points.addAll(points);
        points.sort(Point::compareTo);
        delaunayTriangulation.insertPoints(points);
        refreshTriangles();
        notifyOnAddPoints(points);
        notifyOnSetTriangles(triangles);
    }

    /**
     * Добавляет точку покрытия
     * @param point
     */
    public void addPoint(Point point) {

        int samePointIndex = Collections.binarySearch(points, point, Point::compareTo);
        // Если точка с такими координатами уже есть в коллекции, то берем среднее rssi от этого значения и нового
        if (samePointIndex >= 0) {

            Point samePoint = points.get(samePointIndex);
            samePoint.setZ((samePoint.getZ() + point.getZ())/2);
            notifyOnSetPoint(samePointIndex, samePoint);

        } else {

            this.points.add(point);
            points.sort(Point::compareTo);
            delaunayTriangulation.insertPoint(point);
            notifyOnAddPoint(point);
        }

        refreshTriangles();
        notifyOnSetTriangles(triangles);
    }

    /**
     * Чистит буфер точек (также буфер маркеров карты)
     */
    public void clear() {
        points.clear();
        triangles.clear();
        delaunayTriangulation = new DelaunayTriangulation();
        notifyOnClearPoints();
        notifyOnClearTriangles();
    }

    /**
     * Сброс фильтров и выставление исходного набора объектов на карту
     */
    public void resetFilters() {
        filterPoints = new ArrayList<Point>(points);
        filterTriangles = new ArrayList<Triangle>();
        filterDelaunayTriangulation = null;

        notifyOnSetPoints(points);
        notifyOnSetTriangles(triangles);
    }

    public void filter() {

        filtering = false;

        Stream<Point> filteredStream = points.parallelStream();

        if (Filter.getStartTime().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getDateTime().isAfter(Filter.getStartTime().get())
            );
            filtering = true;
        }

        if (Filter.getEndTime().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getDateTime().isBefore(Filter.getEndTime().get())
            );
        }

        if (Filter.getMinLat().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getX() >= Filter.getMinLat().getAsDouble()
            );
            filtering = true;
        }

        if (Filter.getMaxLat().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getX() <= Filter.getMaxLat().getAsDouble()
            );
        }

        if (Filter.getMinLong().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getY() >= Filter.getMinLong().getAsDouble()
            );
            filtering = true;
        }

        if (Filter.getMaxLong().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getY() <= Filter.getMaxLong().getAsDouble()
            );
        }

        if (Filter.getMinRssi().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getZ() >= Filter.getMinRssi().getAsDouble()
            );
            filtering = true;
        }

        if (Filter.getMaxRssi().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getZ() <= Filter.getMaxRssi().getAsDouble()
            );
        }

        if (Filter.getbStation().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getBStation() == Filter.getbStation().get()
            );
            filtering = true;
        }

        if (Filter.getSsi().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getSsi() == Filter.getSsi().getAsInt()
            );
            filtering = true;
        }

        filterPoints = filteredStream.collect(Collectors.toList());
        filterDelaunayTriangulation = new DelaunayTriangulation(filterPoints);

        filterTriangles = filterDelaunayTriangulation.getTriangulation();

        MapApplet.getInstance().getMap().setPoints(filterPoints);
        MapApplet.getInstance().getMap().setTriangles(filterTriangles);

        if (filtering) {
            if (getGeometryListeners().contains(MapApplet.getInstance().getMap())) {
                mapDrawing = true;
                removeGeometryListener(MapApplet.getInstance().getMap());
            } else {
                mapDrawing = false;
            }
        } else {
            if (mapDrawing) {
                addGeometryListener(MapApplet.getInstance().getMap());
            }
        }
    }

    /**
     * Интерполирует триангуляцию до определенного максимального размера треугольника
     * Фильтры складываются по логическому &
     * @param length - максимальная площадь треугольника в квадратных километрах
     */
    public void interpolateFilter(double length) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                List<Point> interpolatePoints = new ArrayList<>(points.size() > filterPoints.size() ? points : filterPoints);

                DelaunayTriangulation interpolateTriangulation = new DelaunayTriangulation(interpolatePoints);

                List<Triangle> interpolateTriangles = (filterTriangles.isEmpty() ? triangles : filterTriangles);

                int inserts = interpolateTriangles.size();

                while (inserts > 0) {

                    inserts = 0;

                    for (Triangle triangle : interpolateTriangles) {

                        List<Point> points = Arrays.asList(triangle.getA(), triangle.getB(), triangle.getC());

                        for (int i = 0; i < points.size(); i++) {

                            Point insert = points.get(i).plus(points.get((i + 1) % points.size())).div(2);
                            if (points.get(i).distance(insert) > length) {
                                interpolateTriangulation.insertPoint(insert);
                                inserts++;
                            }

                        }
                    }

                    interpolateTriangles = interpolateTriangulation.getTriangulation();

                    log.info("Количество точек: " + interpolatePoints.size());
                    log.info("Количество треугольников: " + interpolateTriangles.size());
                }

                notifyOnSetPoints(interpolateTriangulation.getVertices());
                notifyOnSetTriangles(interpolateTriangles);
            }
        });

        thread.start();

//        notifyOnSetPoints(filterPoints);
//        notifyOnSetTriangles(filterTriangles);
    }

    public LocalDateTime getDateTimeOfEarliestPoint() {
        return (points.isEmpty() ? LocalDateTime.now() : points.parallelStream().map(point -> point.getDateTime()).min(LocalDateTime::compareTo).get());
    }

    public LocalDateTime getDateTimeOfLatestPoint() {
        return (points.isEmpty() ? LocalDateTime.now() :  points.parallelStream().map(point -> point.getDateTime()).max(LocalDateTime::compareTo).get());
    }

    public double getMinLatitude() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getX()).min(Double::compareTo).get();
    }

    public double getMaxLatitude() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getX()).max(Double::compareTo).get();
    }

    public double getMinLongitude() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getY()).min(Double::compareTo).get();
    }

    public double getMaxLongitude() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getY()).max(Double::compareTo).get();
    }

    public double getMinRssi() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getZ()).min(Double::compareTo).get();
    }

    public double getMaxRssi() {
        return points.isEmpty() ? 0 : points.parallelStream().map(point -> point.getZ()).max(Double::compareTo).get();
    }

    public void addGeometryListener(GeometryObserver o) {
        geometryObservers.add(o);
    }

    @Override
    public void removeGeometryListener(GeometryObserver o) {
        geometryObservers.remove(o);
    }

    @Override
    public List<GeometryObserver> getGeometryListeners() {
        return geometryObservers;
    }

    @Override
    public void notifyOnSetPoints(List<Point> points) {
        geometryObservers.forEach(o -> o.setPoints(points));
    }

    @Override
    public void notifyOnSetTriangles(List<Triangle> triangles) {
        geometryObservers.forEach(o -> o.setTriangles(triangles));
    }

    @Override
    public void notifyOnAddPoint(Point point) {
        geometryObservers.forEach(o -> o.addPoint(point));
    }

    @Override
    public void notifyOnAddPoints(List<Point> points) {
        if (!(filterPoints.size() > 0)) {
            geometryObservers.forEach(o -> o.addPoints(points));
        }
    }

    @Override
    public void notifyOnAddTriangles(List<Triangle> triangles) {
        if (!(filterPoints.size() > 0)) {
            geometryObservers.forEach(o -> o.addTriangles(triangles));
        }
    }

    @Override
    public void notifyOnClearPoints() {
        geometryObservers.forEach(o -> o.clearPoints());
    }

    @Override
    public void notifyOnClearTriangles() {
        geometryObservers.forEach(o -> o.clearTriangles());
    }

    @Override
    public void notifyOnSetPoint(int index, Point point) {
        geometryObservers.forEach(o -> o.setPoint(index, point));
    }

    public List<Point> getFilterPoints() {
        return filterPoints;
    }

    public void setFilterPoints(List<Point> filterPoints) {
        this.filterPoints = filterPoints;
    }
}
