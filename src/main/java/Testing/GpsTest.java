package Testing;

import entities.GpsPoint;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by anatoliy on 08.02.17.
 */
public class GpsTest {

    private static NormalDistribution normalLat;

    public static NormalDistribution getNormalLat() {

        return normalLat;
    }

    public static NormalDistribution getNormalLong() {
        return normalLong;
    }

    private static NormalDistribution normalLong;


    public static GpsPoint getRandomGpsPoint() {

        double randomLat = normalLat.sample();
        double randomLon = normalLong.sample();

        return new GpsPoint(
              Optional.of(randomLat),
                Optional.of(randomLon),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()
        );
    }

    public static ArrayList<GpsPoint> getGetRandomGpsPointList(double meanLat, double sdLat, double meanLong, double sdLong, int amount) {

        setNormalLat(new NormalDistribution(meanLat, sdLat));
        setNormalLong(new NormalDistribution(meanLong, sdLong));

        ArrayList<GpsPoint> gpsPoints = new ArrayList<GpsPoint>();

        for (int i = 0; i < amount; i++) {

            GpsPoint point = new GpsPoint(
                    Optional.of(normalLat.sample()),
                    Optional.of(normalLong.sample()),
                    Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty()
            );

            gpsPoints.add(i, point);
        }

        return gpsPoints;
    }

    public static void setNormalLat(NormalDistribution normalLat) {
        GpsTest.normalLat = normalLat;
    }

    public static void setNormalLong(NormalDistribution normalLong) {
        GpsTest.normalLong = normalLong;
    }
}
