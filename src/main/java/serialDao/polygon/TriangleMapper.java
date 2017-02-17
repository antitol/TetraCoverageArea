package serialDao.polygon;

import de.fhpotsdam.unfolding.geo.Location;
import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMapper implements ResultSetMapper<TriangleMarkerRssi> {
    @Override
    public TriangleMarkerRssi map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        PointMarkerRssi pointA = new PointMarkerRssi(
                new Location(
                        r.getDouble("y1"),
                        r.getDouble("x1")
                ), r.getFloat("rssi1"));

        PointMarkerRssi pointB = new PointMarkerRssi(
                new Location(
                        r.getDouble("y2"),
                        r.getDouble("x2")
                ), r.getFloat("rssi2"));

        PointMarkerRssi pointC = new PointMarkerRssi(
                new Location(
                        r.getDouble("y3"),
                        r.getDouble("x3")
                ), r.getFloat("rssi3"));

        return new TriangleMarkerRssi(pointA, pointB, pointC);
    }
}
