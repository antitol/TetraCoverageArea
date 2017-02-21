package serialDao;

import common.entities.serialPart.GpsPoint;
import common.entities.serialPart.Rssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.FloatColumnMapper;
import serialDao.gps.GpsQuery;
import serialDao.polygon.TriangleQuery;
import serialDao.polygon.VoronoiQuery;
import serialDao.rssi.RssiQuery;

import java.util.List;

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
    private final TriangleQuery triangleQuery;
    private final VoronoiQuery voronoiQuery;

    private static final SerialTestDao instance = new SerialTestDao();

    public static final SerialTestDao getInstance() {
        return instance;
    }

    private SerialTestDao() {
        createConnection();

        gpsQuery = h.attach(GpsQuery.class);
        rssiQuery = h.attach(RssiQuery.class);
        triangleQuery = h.attach(TriangleQuery.class);
        voronoiQuery = h.attach(VoronoiQuery.class);
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

    public List<PolygonMarkerRssi> getVoronoiPolygons() {
         return voronoiQuery.getAll();
    }

    public float getArea(TriangleMarkerRssi triangleMarkerRssi) {

        String queryString = "";

        for (Location location : triangleMarkerRssi.getLocations()) {
            queryString += location.getLon() + " " + location.getLat() + ",";
        }

        queryString = queryString.substring(0, queryString.length() - 1);
        queryString = "POLYGON((" + queryString + "))";

        Float response = h.createQuery("SELECT st_area(st_transform(geom, 2100)) / 1000000 " +
                "FROM st_geomfromtext(:q, 4326) AS geom;")
                .bind("q", queryString).map(FloatColumnMapper.PRIMITIVE).first();

        return response;
    }

    public List<TriangleMarkerRssi> getDelauneyTriangles() {
        return triangleQuery.getAll();
    }

    public void clearGpsTable() {
        gpsQuery.clear();
    }
}
