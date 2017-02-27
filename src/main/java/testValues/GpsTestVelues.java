package testValues;

import common.entities.serialPart.GpsPoint;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalDouble;

import static common.entities.serialPart.GpsPoint.LatitudeSign.NORTH;
import static common.entities.serialPart.GpsPoint.LongitudeSign.EAST;

/**
 * Created by anatoliy on 08.02.17.
 */
public class GpsTestVelues {

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
              OptionalDouble.of(randomLat),
                OptionalDouble.of(randomLon),
                Optional.empty(), Optional.empty(),
                OptionalDouble.empty(), OptionalDouble.empty()
        );
    }

    public static ArrayList<GpsPoint> getGetRandomGpsPointList(double meanLat, double sdLat, double meanLong, double sdLong, int amount) {

        setNormalLat(new NormalDistribution(meanLat, sdLat));
        setNormalLong(new NormalDistribution(meanLong, sdLong));

        ArrayList<GpsPoint> gpsPoints = new ArrayList<GpsPoint>();

        for (int i = 0; i < amount; i++) {

            GpsPoint point = new GpsPoint(
                    OptionalDouble.of(normalLat.sample()),
                    OptionalDouble.of(normalLong.sample()),
                    Optional.of(NORTH), Optional.of(EAST),
                    OptionalDouble.empty(), OptionalDouble.empty()
            );

            gpsPoints.add(i, point);
        }

        return gpsPoints;
    }

    public static void setNormalLat(NormalDistribution normalLat) {
        GpsTestVelues.normalLat = normalLat;
    }

    public static void setNormalLong(NormalDistribution normalLong) {
        GpsTestVelues.normalLong = normalLong;
    }
}
