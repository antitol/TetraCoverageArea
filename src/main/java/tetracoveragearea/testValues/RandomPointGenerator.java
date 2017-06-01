package tetracoveragearea.testValues;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.telnet.BStation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс генерирующий тестовые данные (точки) по определенным распределениям
 *
 * Created by anatoliy on 08.02.17.
 */
public class RandomPointGenerator {

    private double minRssi;
    private double maxRssi;

    private AbstractRealDistribution latDistribution;
    private AbstractRealDistribution lonDistribution;


    public RandomPointGenerator(AbstractRealDistribution latDistribution, AbstractRealDistribution lonDistribution, double minRssi, double maxRssi) {
        this.minRssi = minRssi;
        this.maxRssi = maxRssi;
        this.latDistribution = latDistribution;
        this.lonDistribution = lonDistribution;
    }

    public List<Point> getRandomPoints(int amount) {

        ArrayList<Point> points = new ArrayList<Point>();

        for (int i = 0; i < amount; i++) {

            double lat = latDistribution.sample();
            double lon = lonDistribution.sample();

            double a = latDistribution.density(lat) /
                    latDistribution.density(((NormalDistribution) latDistribution).getMean());

            double b = lonDistribution.density(lon) /
                    lonDistribution.density(((NormalDistribution) lonDistribution).getMean());

            double wieght = Math.sqrt((Math.pow(a,2) + Math.pow(b,2))/2);

            Point point = new Point(
                    lat,
                    lon,
                    maxRssi - wieght*(maxRssi - minRssi),
                    LocalDateTime.now(),
                    0,
                    BStation.NULL
            );

            points.add(i, point);
        }

        return points;
    }

    public AbstractRealDistribution getLatDistribution() {

        return latDistribution;
    }

    public AbstractRealDistribution getLonDistribution() {
        return lonDistribution;
    }

    public void setLatDistribution(AbstractRealDistribution distribution) {
        this.latDistribution = latDistribution;
    }

    public void setLonDistribution(NormalDistribution lonDistribution) {
        this.lonDistribution = lonDistribution;
    }
}
