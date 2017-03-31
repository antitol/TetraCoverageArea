package tetracoveragearea.common.entities.centralPart;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;

import java.util.List;

/**
 * Объект наблюдения за изменением набора геометрий
 * Created by anatoliy on 22.03.17.
 */
public interface GeometryObservable {

        void addGeometryListener(GeometryObserver o);
        void removeGeometryListener(GeometryObserver o);

        /**
         * Установить набор точек
         * @param points
         */
        void notifyOnSetPoints(List<Point> points);

        /**
         * Установить набор треугольников
         * @param triangles
         */
        void notifyOnSetTriangles(List<Triangle> triangles);

        /**
         * Добавить точку к набору
         * @param point
         */
        void notifyOnAddPoint(Point point);

        /**
         * Добавить точки к набору
         * @param points
         */
        void notifyOnAddPoints(List<Point> points);


        /**
         * Заменить точку с указанным индексом
         * @param index
         * @param point
         */
        void notifyOnSetPoint(int index, Point point);

        /**
         * Добавить треугольники к набору
         * @param triangles
         */
        void notifyOnAddTriangles(List<Triangle> triangles);

        /**
         * Удалить набор точек
         */
        void notifyOnClearPoints();

        /**
         * Удалить набор треугольников
         */
        void notifyOnClearTriangles();
}
