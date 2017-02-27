package serialDao.serialQueries.gps;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by anatoliy on 19.01.17.
 */
@RegisterMapper(GpsMapper.class)
public interface GpsQuery {

    @SqlUpdate("INSERT INTO GpsPoints (time, lat, lat_hem, lon, lon_hem, speed, course) " +
            "VALUES (:time, :lat, :lat_hem, :lon, :lon_hem, :speed, :course)")
    @GetGeneratedKeys
    int addMySQL(
            @Bind("time") String time,
            @Bind("lat") double lat,
            @Bind("lat_hem") int lat_hem,
            @Bind("lon") double lon,
            @Bind("lon_hem") int lon_hem,
            @Bind("speed") int speed,
            @Bind("course") int course
    );

    @SqlUpdate("INSERT INTO gpsPoint (geom, time, rssi) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), now(), :rssi)")
    @GetGeneratedKeys
    int addWithRssi(
            @Bind("lat") double lat,
            @Bind("lon") double lon,
            @Bind("rssi") int rssi
    );

    @SqlUpdate("INSERT INTO gpsPoint (geom, time) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), now())")
    @GetGeneratedKeys
    int add(
            @Bind("lat") double lat,
            @Bind("lon") double lon
    );

    @SqlUpdate("DELETE FROM gpsPoint")
    void clear();
}
