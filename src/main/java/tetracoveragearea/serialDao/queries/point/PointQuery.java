package tetracoveragearea.serialDao.queries.point;

import tetracoveragearea.common.delaunay.Point;
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

    @SqlQuery("SELECT st_x(geom), st_y(geom), st_z(geom), point_time FROM gpspoint;")
    List<Point> getAll();

    @SqlQuery("SELECT st_x(geom), st_y(geom), st_z(geom) FROM gpspoint WHERE point_time BETWEEN :from AND :to;")
    List<Point> getByDateTimePeriod(
            @Bind("from") Timestamp from,
            @Bind("to") Timestamp to
    );

    @SqlUpdate("INSERT INTO gpspoint (geom, point_time) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat, :rssi), 4326), now())")
    @GetGeneratedKeys
    int addPoint(
            @Bind("lat") double lat,
            @Bind("lon") double lon,
            @Bind("rssi") double rssi,
            @Bind("time") Timestamp timestamp
    );

    @SqlUpdate("DELETE FROM gpsPoint")
    void clear();
}

