package tetracoveragearea.common.parserTools;

import tetracoveragearea.common.delaunay.Point;

import java.io.File;
import java.util.List;

/**
 * Created by anatoliy on 05.04.17.
 */
public interface DocumentParser {

    /**
     * Генерация массива точек в файл
     * @param file
     * @param points
     */
    public void write(File file, List<Point> points);

    /**
     * Парсинг массива точек из файла
     * @param file
     */
    public List<Point> parse(File file) throws Exception;
}
