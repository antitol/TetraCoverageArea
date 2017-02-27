package serialDao.processingQueries;

import common.entities.visualPart.TriangleMarkerRssi;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
@RegisterMapper(TriangleMapper.class)
public interface TriangleQuery {

    @SqlQuery("SELECT " +
            "st_x(foo.point [1]) AS x1, " +
            "st_y(foo.point [1]) AS y1, " +
            "foo.point_rssi [1] AS rssi1, " +
            "st_x(foo.point [2]) AS x2, " +
            "st_y(foo.point [2]) AS y2, " +
            "foo.point_rssi [2] AS rssi2, " +
            "st_x(foo.point [3]) AS x3, " +
            "st_y(foo.point [3]) AS y3, " +
            "foo.point_rssi [3] AS rssi3 " +
            "FROM " +
            "(SELECT " +
            "l_delauney_shape.geom AS shape, " +
            "array_agg(gpspoint.rssi) AS point_rssi, " +
            "array_agg(gpspoint.geom) AS point " +
            "FROM gpspoint, l_delauney_shape " +
            "WHERE gpspoint.geom && l_delauney_shape.geom " +
            "AND st_intersects(l_delauney_shape.geom, gpspoint.geom) " +
            "GROUP BY l_delauney_shape.geom) AS foo")
    List<TriangleMarkerRssi> getAll();

    /**
     *
     * @return
     */
    @SqlUpdate(
            "TRUNCATE TABLE l_delauney_shape;" + 
            "INSERT INTO l_delauney_shape (geom) " +
            "SELECT (ST_Dump(geom)).geom " +
            "FROM (SELECT st_delaunaytriangles(ST_Collect(geom)) AS geom " +
            "FROM gpspoint) AS triangles;")
    int generateTriangles();

    @SqlUpdate("TRUNCATE TABLE l_delauney_shape;")
    int truncateTriangles();
}
