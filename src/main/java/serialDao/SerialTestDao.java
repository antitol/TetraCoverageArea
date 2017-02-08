package serialDao;

import entities.GpsPoint;
import entities.Rssi;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 * Created by anatoliy on 17.01.17.
 */
public class SerialTestDao {

    private String url = "jdbc:postgresql://127.0.0.1:5432/postgis";
    private String login = "postgis";
    private String pass = "postgis";
    private Handle h;
    private DBI dbi;

    private final GpsQuery gpsQuery;
    private final RssiQuery rssiQuery;

    private static final SerialTestDao instance = new SerialTestDao();

    public static final SerialTestDao getInstance() {
        return instance;
    }

    private SerialTestDao() {
        createConnection();

        gpsQuery = h.attach(GpsQuery.class);
        rssiQuery = h.attach(RssiQuery.class);
    }

    private void createConnection() {

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        dbi = new DBI(url, login, pass);
        h = dbi.open();
    }

    public void addGpsPoint(GpsPoint point) {

        System.err.println(point.getLatitude() + " " + point.getLongitude());

        gpsQuery.addWithRssi(
                point.getLatitude(),
                point.getLongitude(),
                ((Double) (30 + Math.random()*30)).intValue()
        );
    }

    public void addGpsWithRssiPoint(GpsPoint point, Rssi rssi) {

        System.err.println(point.getLatitude() + " " + point.getLongitude());

        gpsQuery.addWithRssi(
                point.getLatitude(),
                point.getLongitude(),
                rssi.getRssi()
        );
    }

    public void addRssi(Rssi rssi) {
        System.out.println(rssi.getTime());
        rssiQuery.add(
                rssi.getRssi()
        );
    }

    public void clearGpsTable() {
        gpsQuery.clear();
    }
}
