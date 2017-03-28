import org.junit.Test;
import tetracoveragearea.common.delaunay.DelaunayTriangulation;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by anatoliy on 16.03.17.
 */
public class TriangleTest {

    @Test
    public void test() {

        List<Point> points1 = Arrays.asList(
                new Point(1,2,3),
                new Point(2,2,3),
                new Point(3,2,3),
                new Point(4,2,3),
                new Point(5,2,3)
        );

        List<Point> points2 = Arrays.asList(
                new Point(1,1,3),
                new Point(1,2,3),
                new Point(1,3,3),
                new Point(1,4,3),
                new Point(1,5,3)
                );

        System.out.println(GeometryStore.getInstance().intersectPoints(points1, points2));

        assertEquals(GeometryStore.getInstance().intersectPoints(points1, points2), Arrays.asList(new Point(1,2,3)));
    }

    @Test
    public void manyPointsTriangulationTest() {

        DelaunayTriangulation delaunayTriangulation = new DelaunayTriangulation();
        IntStream.rangeClosed(1, 100000).forEach(i -> {
            delaunayTriangulation.insertPoint(new Point(Math.random() * 100, Math.random() * 100, Math.random() * 100));
            if (i % 1000 == 0) {
                delaunayTriangulation.getTriangulation();
                System.out.println(i);
            }
        });
        System.out.println(delaunayTriangulation.getTriangulation().stream().count());
    }
}
