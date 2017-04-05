package parser;

import org.junit.Test;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.parserTools.GeojsonParser;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by anatoliy on 05.04.17.
 */
public class GeojsonTest {

    GeojsonParser parser = new GeojsonParser();
    List<Point> points = new ArrayList<>();

    @Test
    public void test() {

        for (int i = 0; i < 10; i++) {
            points.add(new Point(
                    Math.random() * 50,
                    Math.random() * 50,
                    Math.random() * 50,
                    LocalDateTime.now().plusSeconds(i)));
        }

        parser.write(new File("src/main/resources/export/GeoJSON/points.geojson"), points);

        List<Point> parsePoints = new ArrayList<>();

        try {
            parsePoints = parser.parse(new File("src/main/resources/export/GeoJSON/points.geojson"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        parsePoints.sort(Point::compareTo);
        points.sort(Point::compareTo);

        assertThat(parsePoints, is(points));
    }
}
