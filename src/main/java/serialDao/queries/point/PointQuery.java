package serialDao.queries.point;

import common.entities.visualPart.PointMarkerRssi;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by anatoliy on 22.02.17.
 */
@RegisterMapper(PointMapper.class)
public interface PointQuery {

    @SqlQuery("SELECT st_x(geom), st_y(geom), st_z(geom) FROM gpspoint;")
    List<PointMarkerRssi> getAll();

    @SqlQuery("SELECT st_x(geom), st_y(geom), st_z(geom) FROM gpspoint WHERE time BETWEEN :from AND :to;")
    List<PointMarkerRssi> getByDateTimePeriod(
            @Bind("from") Timestamp from,
            @Bind("to") Timestamp to
    );

    @SqlUpdate("INSERT INTO gpspoint_buffer (geom, rssi) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), :rssi)")
    void addToBuffer(
            @Bind("lat") double lat,
            @Bind("lon") double lon,
            @Bind("rssi") int rssi
    );

    @SqlUpdate("DELETE FROM gpspoint_buffer")
    void clearBuffer();

    @SqlUpdate("INSERT INTO gpspoint (geom, time) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat, :rssi), 4326), now())")
    @GetGeneratedKeys
    int addPointWithRssi(
            @Bind("lat") double lat,
            @Bind("lon") double lon,
            @Bind("rssi") double rssi
    );

    @SqlUpdate("INSERT INTO gpsPoint (geom, time) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), now())")
    @GetGeneratedKeys
    int addPoint(
            @Bind("lat") double lat,
            @Bind("lon") double lon
    );

    @SqlUpdate("DELETE FROM gpsPoint")
    void clear();
}

