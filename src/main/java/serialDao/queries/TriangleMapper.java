package serialDao.queries;

import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMapper implements ResultSetMapper<TriangleMarkerRssi> {
    @Override
    public TriangleMarkerRssi map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        Location first = new Location(
                r.getDouble("y1"),
                r.getDouble("x1")
        );

        Location second = new Location(
                r.getDouble("y2"),
                r.getDouble("x2")
        );

        Location third = new Location(
                r.getDouble("y3"),
                r.getDouble("x3")
        );

        List<Location> locations = Arrays.asList(
          first, second, third, first
        );

        List<Double> rssiValues = Arrays.asList(
                r.getDouble("rssi1"),
                r.getDouble("rssi2"),
                r.getDouble("rssi3"),
                r.getDouble("rssi1")
        );

        return new TriangleMarkerRssi(locations, rssiValues);
    }
}
