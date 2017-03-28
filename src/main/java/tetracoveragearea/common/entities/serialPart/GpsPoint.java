package tetracoveragearea.common.entities.serialPart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Класс GPS-точки
 * Служит для переноса данных "серийный порт - база данных"
 *
 * Created by anatoliy on 19.01.17.
 */
public class GpsPoint {

    public enum LatitudeSign {
        NORTH, SOUTH
    }

    public enum LongitudeSign {
        WEST, EAST
    }

    private OptionalDouble latitude;
    private OptionalDouble longitude;
    private LatitudeSign latitudeSign;
    private LongitudeSign longitudeSign;

    private Date date;
    private OptionalDouble speed;
    private OptionalDouble course;

    /**
     *
     * @param latitude - Широта в градусах от 0 до 90
     * @param longitude - Долгота в градусах от 0 до 180
     * @param latitudeSign - Северное / южное полушарие
     * @param longitudeSign - Восточная / Западное полушарие
     * @param speed - скорость в узлах
     * @param course - указание курса в градусах (0 -север, по часовой)
     */
    public GpsPoint(OptionalDouble latitude, OptionalDouble longitude, Optional<LatitudeSign> latitudeSign, Optional<LongitudeSign> longitudeSign, OptionalDouble speed, OptionalDouble course) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeSign = latitudeSign.isPresent() ? latitudeSign.get() : LatitudeSign.NORTH;
        this.longitudeSign = longitudeSign.isPresent() ? longitudeSign.get() : LongitudeSign.EAST;
        this.date = new Date();
        this.speed = speed;
        this.course = course;
    }

    public GpsPoint() {
        this(
                OptionalDouble.empty(), OptionalDouble.empty(),
                Optional.empty(), Optional.empty(),
                OptionalDouble.empty(), OptionalDouble.empty()
        );
    }

    public OptionalDouble getLatitude() {
        return latitude;
    }

    public OptionalDouble getLongitude() {
        return longitude;
    }

    public OptionalDouble getSignificantLatitude() {

        if (latitude.isPresent()) {
            return latitudeSign == LatitudeSign.NORTH ? latitude : OptionalDouble.of(-latitude.getAsDouble());
        } else {
            return latitude;
        }
    }

    public OptionalDouble getSignificantLongitude() {

        if (longitude.isPresent()) {
            return longitudeSign == longitudeSign.EAST ? longitude : OptionalDouble.of(-longitude.getAsDouble());
        } else {
            return longitude;
        }
    }

    public OptionalDouble getSpeed() {
        return speed;
    }

    public OptionalDouble getCourse() {
        return course;
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
    }
}
