package tetracoveragearea.common.entities.centralPart;

import org.apache.log4j.Logger;
import tetracoveragearea.common.delaunay.BoundingBox;
import tetracoveragearea.common.delaunay.DelaunayTriangulation;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.common.telnet.BStation;
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
     * @param xCells - количество ячеек, на которые разбивается карта
     */
    public void interpolateFilter(int xCells, int cellDistance) {

        long start = System.currentTimeMillis();
        filterTriangles.clear();
        BoundingBox bbx = (filterPoints.size() > 0) ? filterDelaunayTriangulation.getBoundingBox() : delaunayTriangulation.getBoundingBox();

        double rowCellSize = (bbx.maxX() - bbx.minX()) / xCells;
        double colCellSize = rowCellSize * Math.sqrt(3) / 2;
        int yCells = (int) Math.ceil((bbx.maxY() - bbx.minY()) / colCellSize);

        List<Point> tempPoints = new ArrayList<>((filterPoints.size() > 0) ? filterPoints : points);
        List<Point> interpolationPoints = new ArrayList<>();
        List<Triangle> interpolationTriangles = new ArrayList<>();

        double radius = rowCellSize * cellDistance;
        Map<BStation, List<Point>> bsPointsMap = tempPoints.parallelStream().collect(Collectors.groupingBy(Point::getBStation));

        for (List<Point> bsPoints : bsPointsMap.values()) {

            for (int i = 0; i < xCells * yCells; i++) {
                Point pcell = new Point(
                        bbx.minX() + ((i % xCells) + ((i / xCells) % 2 == 1 ? 0.5 : 0)) * rowCellSize,
                        bbx.minY() + ((i / xCells)) * colCellSize,
                        200
                );

                List<Point> included = tempPoints.parallelStream()
                        .filter(point -> Math.abs(pcell.getX() - point.getX()) < radius)
                        .filter(point -> Math.abs(pcell.getY() - point.getY()) < radius)
                        .filter(point -> pcell.distance(point) < radius)
                        .collect(Collectors.toList());

                int idx;
                if ((idx = Collections.binarySearch(included, pcell, Point::compareTo)) >= 0) {
                    pcell.setZ(tempPoints.get(idx).getZ());
                } else {

                    double weight = 0;
                    double sumzw = 0;
                    double sumw = 0;
                    for (int j = 0; j < included.size() && j < 10; j++) {
                        double d = pcell.distance(included.get(j));
                        weight = Math.pow((rowCellSize - d) / (rowCellSize * d), 2);
                        sumw += weight;
                        sumzw += included.get(j).getZ() * weight;
                    }

                    if (sumw > 0) {
                        pcell.setZ(sumzw / sumw);
                    }
                }

                if (interpolationPoints.size() == xCells * yCells) {
                    if (pcell.getZ() != 0 && interpolationPoints.get(i).getZ() != 0) {
                        interpolationPoints.get(i).setZ(Math.min(pcell.getZ(), interpolationPoints.get(i).getZ()));
                    } else {
                        interpolationPoints.get(i).setZ(Math.max(pcell.getZ(), interpolationPoints.get(i).getZ()));
                    }

                } else {
                    interpolationPoints.add(pcell);
                }
            }
        }

        for (int i = 0; i < xCells * yCells; i++) {
            int xPoint = i % xCells;
            int yPoint = i / xCells;

            int parity = yPoint % 2 == 1 ? 1 : 0;

            if (xPoint < xCells - 1) {
                if (yPoint > 0) {
                    interpolationTriangles.add(
                            new Triangle(
                                    interpolationPoints.get(i),
                                    interpolationPoints.get(i - xCells + parity),
                                    interpolationPoints.get(i + 1), false
                            )
                    );
                }

                if (yPoint < yCells - 1) {
                    interpolationTriangles.add(
                            new Triangle(
                                    interpolationPoints.get(i),
                                    interpolationPoints.get(i + xCells + parity),
                                    interpolationPoints.get(i + 1), false
                            )
                    );
                }
            }
        }

        System.out.println((System.currentTimeMillis() - start) + " millis");
//        MapApplet.getInstance().getMap().setPoints(interpolationPoints);
        MapApplet.getInstance().getMap().setTriangles(interpolationTriangles);

//        TreeMap<Integer, List<Point>> idwCellMap = new TreeMap<>();
        /*IntStream.range(0, xCells * yCells).forEach(i -> idwCellMap.put(i, new ArrayList<Point>()));

        for (Point point : points) {
            int xCell = (int) ((point.getX() - bbx.minX()) / rowCellSize);
            int yCell = (int) ((point.getY() - bbx.minY()) / rowCellSize);

            idwCellMap.get(yCell * xCells + xCell).add(point);
        }

        for (int i : idwCellMap.keySet()) {
            if (idwCellMap.get(i).size() == 0) {
                Point pcell = new Point(
                        bbx.minX() + ((i % xCells)) * rowCellSize,
                        bbx.minY() + ((i / xCells)) * rowCellSize,
                200);

                filterPoints.add(pcell);
                continue;
            }

            for (int j = 0; j < cellDiv; j++) {

                for (int k = 0; k < cellDiv; k++) {
                    Point pcell = new Point(
                            bbx.minX() + ((i % xCells) + j / (double) cellDiv) * rowCellSize,
                            bbx.minY() + ((i / xCells) + k / (double) cellDiv) * rowCellSize);

                    List<Point> idwCellPoints = idwCellMap.get(i);

                    Collections.sort(idwCellPoints, new Comparator<Point>() {
                        @Override
                        public int compare(Point o1, Point o2) {
                            return Double.compare(o1.distance(pcell), o2.distance(pcell));
                        }
                    });

                    int idx;
                    if ((idx = Collections.binarySearch(idwCellPoints, pcell, Point::compareTo)) >= 0) {
                        pcell.setZ(idwCellPoints.get(idx).getZ());
                    } else {
                        double weight = 0;
                        double sumzw = 0;
                        double sumw = 0;
                        for (int l = 0; l <= Math.min(idwCellPoints.size() - 1, 10); l++) {
                            double d = pcell.distance(idwCellPoints.get(l));
                            weight = Math.pow((rowCellSize - d) / (rowCellSize * d), 2);
                            sumw += weight;
                            sumzw += idwCellPoints.get(l).getZ() * weight;
                        }


                        pcell.setZ(sumzw / sumw);
                    }

                    filterPoints.add(pcell);
                }
            }*/
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

    private class CellKey implements Comparable<CellKey> {
        public int xCell;
        public int yCell;

        public CellKey(int xCell, int yCell) {
            this.xCell = xCell;
            this.yCell = yCell;
        }

        @Override
        public int compareTo(CellKey o) {
            if (xCell > o.xCell)
                return 1;
            if (xCell < o.xCell)
                return -1;
            // x1 == x2
            if (yCell > o.yCell)
                return 1;
            if (yCell < o.yCell)
                return -1;
            // y1==y2
            return 0;
        }
    }
}

