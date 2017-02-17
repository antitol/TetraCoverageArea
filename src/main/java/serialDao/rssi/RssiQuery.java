package serialDao.rssi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by anatoliy on 19.01.17.
 */
@RegisterMapper(RssiMapper.class)
public interface RssiQuery {

    @SqlUpdate("INSERT INTO rssi (rssi, time) " +
            "VALUES (:rssi, now())")
    @GetGeneratedKeys
    int add(
            @Bind("rssi") int rssi
    );
}
