package tetracoveragearea.serialDao.queries;

import tetracoveragearea.common.entities.visualPart.TriangleMarkerRssi;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
@RegisterMapper(TriangleMapper.class)
public interface TriangleQuery {

    @SqlQuery("SELECT "+
           "x [1] x1, "+
           "y [1] y1, "+
           "rssi [1] rssi1, "+
           "x [2] x2, "+
           "y [2] y2, "+
           "rssi [2] rssi2, "+
           "x [3] x3, "+
           "y [3] y3, "+
           "rssi [3] rssi3 "+
           "FROM ( "+
           "SELECT "+
           "id, "+
           "array_agg(st_x(triangle_point)) x, "+
           "array_agg(st_y(triangle_point)) y, "+
           "array_agg(st_z(triangle_point)) rssi "+
           "FROM ( "+
           "SELECT "+
           "id, "+
           "(st_dumppoints(geom)).geom AS triangle_point "+
           "FROM l_delauney_shape) points "+
           "GROUP BY id "+
           ") coordinates;")
    List<TriangleMarkerRssi> getAll();

    /**
     *
     * @return
     */
    @SqlUpdate(
           "TRUNCATE TABLE l_delauney_shape; "+
                   "INSERT INTO l_delauney_shape (geom) "+
                   "SELECT (ST_Dump(delaunay_collection)).geom "+
                   "FROM (SELECT st_delaunaytriangles(ST_Collect(unique_geom.geom)) AS delaunay_collection "+
                   "FROM ( "+
                   "SELECT geom "+
                   "FROM gpspoint_buffer "+
                   "GROUP BY geom "+
                   ") AS unique_geom) AS triangles;")
    void generateTrianglesFromBuffer();

    /**
     *
     * @return
     */
    @SqlUpdate(
           "TRUNCATE TABLE l_delauney_shape; "+
                   "INSERT INTO l_delauney_shape (geom) "+
                   "SELECT (ST_Dump(delaunay_collection)).geom "+
                   "FROM (SELECT st_delaunaytriangles(ST_Collect(unique_geom.geom)) AS delaunay_collection "+
                   "FROM ( "+
                   "SELECT geom "+
                   "FROM gpspoint "+
                   "GROUP BY geom "+
                   ") AS unique_geom) AS triangles;")
    void generateTriangles();

    @SqlUpdate(
            "TRUNCATE TABLE l_delauney_shape; "+
                    "INSERT INTO l_delauney_shape (geom) "+
                    "SELECT (ST_Dump(delaunay_collection)).geom "+
                    "FROM (SELECT st_delaunaytriangles(ST_Collect(unique_geom.geom)) AS delaunay_collection "+
                    "FROM ( "+
                    "SELECT geom "+
                    "FROM gpspoint " +
                    "WHERE time BETWEEN :from AND :to "+
                    "GROUP BY geom "+
                    ") AS unique_geom) AS triangles;")
    void generateTrianglesByTimestamp(
            @Bind("from") Timestamp from,
            @Bind("to") Timestamp to
    );



    @SqlUpdate("TRUNCATE TABLE l_delauney_shape;")
    int clear();
}
