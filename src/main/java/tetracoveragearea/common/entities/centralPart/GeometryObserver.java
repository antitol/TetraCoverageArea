package tetracoveragearea.common.entities.centralPart;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;

import java.util.List;

/**
 * Наблюдатель за изменением набора геометрий в хранилище
 * Created by anatoliy on 22.03.17.
 */
public interface GeometryObserver {

    void setPoints(List<Point> points);
    void setPoint(int index, Point point);
    void setTriangles(List<Triangle> triangles);
    void addPoint(Point point);
    void addPoints(List<Point> points);
    void addTriangles(List<Triangle> triangles);
    void clearPoints();
    void clearTriangles();
}
