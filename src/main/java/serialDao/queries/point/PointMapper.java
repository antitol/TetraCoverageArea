package serialDao.queries.point;

import common.entities.visualPart.PointMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * st_x - долгота точки
 * st_y - широта точки
 * rssi - уровень сигнала в dBm  в точке
 *
 * Created by anatoliy on 22.02.17.
 */
public class PointMapper implements ResultSetMapper<PointMarkerRssi> {
    @Override
    public PointMarkerRssi map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return  new PointMarkerRssi(
                new Location(
                        r.getDouble("st_y"),
                        r.getDouble("st_x")
                ), r.getDouble("st_z"));
    }
}
