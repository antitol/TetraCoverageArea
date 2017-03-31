package tetracoveragearea.common.entities.centralPart;

import org.apache.commons.collections4.CollectionUtils;
import tetracoveragearea.common.delaunay.DelaunayTriangulation;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 14.03.17.
 */
public class GeometryStore implements GeometryObservable {

    private List<Point> points = new ArrayList<>();
    private List<Triangle> triangles = new ArrayList<Triangle>();
    private DelaunayTriangulation delaunayTriangulation = new DelaunayTriangulation();
    private DelaunayTriangulation filterDelaunayTriangulation = new DelaunayTriangulation();

    List<GeometryObserver> geometryObservers = new LinkedList<>();

    // Набор точек хранилища, хранит пересечение множеств точек, которые выдали фильтры
    private List<Point> filterPoints = new ArrayList<Point>();

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

    public void resetFilters() {
        filterPoints = new ArrayList<Point>();
        filterTriangles = new ArrayList<Triangle>();
        filterDelaunayTriangulation = null;

        notifyOnSetPoints(points);
        notifyOnSetTriangles(triangles);
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

                filterPoints = (filterPoints.isEmpty() ? points : filterPoints);

                filterDelaunayTriangulation = new DelaunayTriangulation(filterPoints);

                int inserts = filterPoints.size();

                while (inserts > 0) {

                    inserts = 0;

                    for (Triangle triangle : (filterTriangles.isEmpty() ? triangles : filterTriangles)) {

                        if (triangle.getArea() > area) {

                            Point insertPoint = triangle.getCentroid();
                            filterDelaunayTriangulation.insertPoint(insertPoint);
                            filterPoints.add(insertPoint);
                            inserts++;
                        }
                    }

                    filterTriangles = filterDelaunayTriangulation.getTriangulation();
                    notifyOnSetTriangles(filterTriangles);

                    System.out.println("Количество точег: " + filterPoints.size());
                    System.out.println("Количество треугольнеков: " + filterTriangles.size());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}

                }

                System.out.println("Ну все, конец");
            }
        });

        thread.start();

//        notifyOnSetPoints(filterPoints);
//        notifyOnSetTriangles(filterTriangles);
    }

    /**
     * Фильтрует по указанному времени набор точек хранилища
     * @param fromDateTime - минимальное время
     * @param toDateTime - максимальное время
     */
    public void timeFilter(LocalDateTime fromDateTime, LocalDateTime toDateTime) {

        filterPoints = points.stream()
                .filter(point -> point.getDateTime().isAfter(fromDateTime))
                .filter(point -> point.getDateTime().isBefore(toDateTime))
                .collect(Collectors.toList());

        filterDelaunayTriangulation = new DelaunayTriangulation(filterPoints);

        filterTriangles = filterDelaunayTriangulation.getTriangulation();
        notifyOnSetPoints(filterPoints);
        notifyOnSetTriangles(filterTriangles);
    }

    /**
     * Пересечение множеств точек
     * @param points1
     * @param points2
     * @return
     */
    public List<Point> intersectPoints(List<Point> points1, List<Point> points2) {
        return (List<Point>) CollectionUtils.intersection(points1, points2);
    }

    public LocalDateTime getDateTimeOfEarliestPoint() {

        return (points.isEmpty() ? LocalDateTime.now() : points.stream().map(point -> point.getDateTime()).min(LocalDateTime::compareTo).get());
    }

    public LocalDateTime getDateTimeOfLatestPoint() {

        return (points.isEmpty() ? LocalDateTime.now() :  points.stream().map(point -> point.getDateTime()).max(LocalDateTime::compareTo).get());
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
