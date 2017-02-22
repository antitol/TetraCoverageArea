package serialDao.processingQueries.point;

import common.entities.visualPart.PointMarkerRssi;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by anatoliy on 22.02.17.
 */
@RegisterMapper(PointMapper.class)
public interface PointQuery {

    @SqlQuery("SELECT st_x(geom), st_y(geom), rssi FROM gpspoint;")
    List<PointMarkerRssi> getAll();
}
