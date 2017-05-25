package tetracoveragearea.serialDao.queries.point;

import tetracoveragearea.common.delaunay.Point;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import tetracoveragearea.common.telnet.BStation;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * st_x - долгота точки
 * st_y - широта точки
 * rssi - уровень сигнала в dBm  в точке
 *
 * Created by anatoliy on 22.02.17.
 */
public class PointMapper implements ResultSetMapper<Point> {
    @Override
    public Point map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return  new Point(
                r.getDouble("st_y"),
                r.getDouble("st_x"),
                r.getDouble("st_z"),
                r.getTimestamp("p_time").toLocalDateTime(),
                r.getInt("ssi"),
                BStation.getById(r.getInt("bs_id")));
    }
}
