package tetracoveragearea.common.entities.centralPart;

import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;

import java.util.List;

/**
 * Объект наблюдения за изменением набора геометрий
 * Created by anatoliy on 22.03.17.
 */
public interface GeometryObservable {

        void registerObserver(GeometryObserver o);
        void removeObserver(GeometryObserver o);

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
         * Добавить точки к набору
         * @param points
         */
        void notifyOnAddPoints(List<Point> points);

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
