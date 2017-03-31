import org.junit.Test;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anatoliy on 30.03.17.
 */
public class PointsTest {

    @Test
    public void InsertPointsTest() {

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 150000; i++) {
            points.add(
                    new Point(
                            (int) (Math.random()*50),
                            (int) (Math.random()*50),
                            (int) (Math.random()*50)
            ));
        }

        GeometryStore.getInstance().addPoints(points);

    }
}
