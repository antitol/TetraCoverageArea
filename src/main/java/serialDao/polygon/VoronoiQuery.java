package serialDao.polygon;

import common.entities.visualPart.PolygonMarkerRssi;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by anatoliy on 20.02.17.
 */
@RegisterMapper(VoronoiMapper.class)
public interface VoronoiQuery {

    // Запрос полигонов
    @SqlQuery("SELECT st_astext(test_geom) as geom, rssi FROM l_voronoi_shape;")
    List<PolygonMarkerRssi> getAll();

    // Генерация из точек
    @SqlUpdate("TRUNCATE TABLE l_voronoi_shape;\n" +
            "INSERT INTO l_voronoi_shape (test_geom) " +
            "  SELECT geom " +
            "  FROM (SELECT (st_dump(st_voronoipolygons(st_union(geom)))).geom AS geom " +
            "        FROM gpspoint) AS foo;")
    int generatePolygons();

}
