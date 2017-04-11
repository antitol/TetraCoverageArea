package tetracoveragearea.common.entities.centralPart;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;

import java.util.List;

/**
 * Created by anatoliy on 31.03.17.
 */
public abstract class GeometryAdapter implements GeometryObserver {

    @Override
    public void setPoints(List<Point> points) {}

    @Override
    public void setPoint(int index, Point point) {}

    @Override
    public void setTriangles(List<Triangle> triangles) {}

    @Override
    public boolean addPoint(Point point) {return true;}

    @Override
    public void addPoints(List<Point> points) {}

    @Override
    public void addTriangles(List<Triangle> triangles) {}

    @Override
    public void clearPoints() {}

    @Override
    public void clearTriangles() {}
}
