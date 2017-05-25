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

    @SqlQuery("SELECT st_x(geom), st_y(geom), avg(st_z(geom)) as st_z, max(point_time) as p_time, bs_id, ssi FROM gpspoint GROUP BY geom, bs_id, ssi;")
    List<Point> getAll();

    @SqlQuery("SELECT st_x(geom), st_y(geom), avg(st_z(geom)) as st_z, max(point_time) as p_time, bs_id, ssi FROM gpspoint WHERE point_time BETWEEN :from AND :to GROUP BY geom, bs_id, ssi;")
    List<Point> getByDateTimePeriod(
            @Bind("from") Timestamp from,
            @Bind("to") Timestamp to
    );

    @SqlUpdate("INSERT INTO gpspoint (geom, point_time, bs_id, ssi) " +
            "VALUES (ST_SetSRID(ST_MakePoint(:lon, :lat, :rssi), 4326), :time, :bs, :ssi)")
    @GetGeneratedKeys
    int addPoint(
            @Bind("lat") double lat,
            @Bind("lon") double lon,
            @Bind("rssi") double rssi,
            @Bind("time") Timestamp timestamp,
            @Bind("bs") int bs_id,
            @Bind("ssi") int ssi
    );

    @SqlUpdate("DELETE FROM gpsPoint")
    void clear();
}

