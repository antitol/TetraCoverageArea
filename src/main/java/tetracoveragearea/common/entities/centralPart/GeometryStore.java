package tetracoveragearea.common.entities.centralPart;

import tetracoveragearea.common.delaunay.DelaunayTriangulation;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.gui.panels.filterPanels.Filter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by anatoliy on 14.03.17.
 */
public class GeometryStore implements GeometryObservable {

    private boolean enableTimeFilter = false;
    private boolean enableXFilter = false;
    private boolean enableYFilter = false;
    private boolean enableZFilter = false;

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
        this.points = points;
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

        Stream<Point> filteredStream = points.parallelStream();

        if (Filter.getStartTime().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getDateTime().isAfter(Filter.getStartTime().get())
            );
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
        }

        if (Filter.getMaxRssi().isPresent()) {
            filteredStream = filteredStream.filter(
                    point -> point.getZ() <= Filter.getMaxRssi().getAsDouble()
            );
        }

        filterPoints = filteredStream.collect(Collectors.toList());
        filterDelaunayTriangulation = new DelaunayTriangulation(filterPoints);

        filterTriangles = filterDelaunayTriangulation.getTriangulation();
        notifyOnSetPoints(filterPoints);
        notifyOnSetTriangles(filterTriangles);
    }

    /**
     * Интерполирует триангуляцию до определенного максимального размера треугольника
     * Фильтры складываются по логическому &
     * @param area - максимальная площадь треугольника в квадратных километрах
     */
    public void interpolateFilter(double area) {

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

                        if (triangle.getArea() > area) {

                            Point insertPoint = triangle.getCentroid();
                            interpolateTriangulation.insertPoint(insertPoint);
                            interpolatePoints.add(insertPoint);
                            inserts++;
                        }
                    }

                    interpolateTriangles = interpolateTriangulation.getTriangulation();

                    System.out.println("Количество точек: " + interpolatePoints.size());
                    System.out.println("Количество треугольников: " + interpolateTriangles.size());
                }

                notifyOnSetPoints(interpolatePoints);
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
        geometryObservers.forEach(o -> o.addPoints(points));
    }

    @Override
    public void notifyOnAddTriangles(List<Triangle> triangles) {
        geometryObservers.forEach(o -> o.addTriangles(triangles));
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
}
