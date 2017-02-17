package serialDao.gps;

import common.entities.serialPart.GpsPoint;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by anatoliy on 19.01.17.
 */
public class GpsMapper implements ResultSetMapper<GpsPoint> {
    @Override
    public GpsPoint map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return null;
    }
}
