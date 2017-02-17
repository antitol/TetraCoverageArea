package common.entities.serialPart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Класс GPS-точки
 * Служит для переноса данных "серийный порт - база данных"
 *
 * Created by anatoliy on 19.01.17.
 */
public class GpsPoint {

    private double latitude;
    private double longitude;
    private boolean latitudeNorth;
    private boolean longitudeEast;

    private final Date date;
    private int speed;
    private int course;

    public GpsPoint(Optional latitude,Optional longitude,Optional latitudeNorth, Optional longitudeEast, Optional speed, Optional course) {

        if (latitude.isPresent()) {
            this.latitude = Double.parseDouble(latitude.get().toString());
        } else {
            this.latitude = 0.0;
        }

        if (longitude.isPresent()) {
            this.longitude = Double.parseDouble(longitude.get().toString());
        } else {
            this.longitude = 0.0;
        }

        this.latitudeNorth = latitudeNorth.isPresent() ? (Boolean) latitudeNorth.get() : true;

        this.longitudeEast = longitudeEast.isPresent() ? (Boolean) longitudeEast.get() : true;

        this.date = new Date();

        if (speed.isPresent()) {
            this.course = ((Double) Double.parseDouble(speed.get().toString())).intValue();
        } else {
            this.speed = 0;
        }

        if (course.isPresent()) {
            this.course = ((Double) Double.parseDouble(course.get().toString())).intValue();
        } else {
            this.course = 0;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isLatitudeNorth() {
        return latitudeNorth;
    }

    public boolean isLongitudeEast() {
        return longitudeEast;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCourse() {
        return course;
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
    }
}
