package serialDao.serialQueries.rssi;

import common.entities.serialPart.Rssi;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by anatoliy on 19.01.17.
 */
public class RssiMapper implements ResultSetMapper<Rssi> {
    @Override
    public Rssi map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return null;
    }
}
