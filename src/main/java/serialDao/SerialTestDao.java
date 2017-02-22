package serialDao;

import common.entities.serialPart.GpsPoint;
import common.entities.serialPart.Rssi;
import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.FloatColumnMapper;
import serialDao.processingQueries.point.PointQuery;
import serialDao.serialQueries.gps.GpsQuery;
import serialDao.processingQueries.TriangleQuery;
import serialDao.processingQueries.VoronoiQuery;
import serialDao.serialQueries.rssi.RssiQuery;

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
    private final PointQuery pointQuery;
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
        pointQuery = h.attach(PointQuery.class);
        triangleQuery = h.attach(TriangleQuery.class);
        voronoiQuery = h.attach(VoronoiQuery.class);
    }

    /**
     * Установка соединения с драйвером
     */
    private void createConnection() {

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Ошибка PostgreSQL JDBC драйвера");
            e.printStackTrace();
            return;

        }

        dbi = new DBI(url, login, pass);
        h = dbi.open();
    }

    /**
     * Добавляет точку в таблицу
     * @param point
     */
    public void addGpsPoint(GpsPoint point) {

        System.err.println(point.getLatitude() + " " + point.getLongitude());

        gpsQuery.addWithRssi(
                point.getLatitude(),
                point.getLongitude(),
                ((Double) (30 + Math.random()*30)).intValue()
        );
    }

    /**
     * Добавляет точку с значением уровня сигнала
     * @param point
     * @param rssi
     */
    public void addGpsWithRssiPoint(GpsPoint point, Rssi rssi) {

        System.err.println(point.getLatitude() + " " + point.getLongitude());

        gpsQuery.addWithRssi(
                point.getLatitude(),
                point.getLongitude(),
                rssi.getRssi()
        );
    }

    /**
     * Добавляет значение уровня сигнала
     * @param rssi
     */
    public void addRssi(Rssi rssi) {
        System.out.println(rssi.getTime());
        rssiQuery.add(
                rssi.getRssi()
        );
    }

    /**
     * Получает полигоны Вороного из таблицы
     * @return
     */
    public List<PolygonMarkerRssi> getVoronoiPolygons() {
         return voronoiQuery.getAll();
    }

    /**
     * Запрашивает площадь полигона в кв.км
     *
     * @param polygonMarker
     * @return
     */
    public float getArea(SimplePolygonMarker polygonMarker) {

        String queryString = "";

        for (Location location : polygonMarker.getLocations()) {
            queryString += location.getLon() + " " + location.getLat() + ",";
        }

        queryString = queryString.substring(0, queryString.length() - 1);
        queryString = "POLYGON((" + queryString + "))";

        Float response = h.createQuery("SELECT st_area(st_transform(geom, 2100)) / 1000000 " +
                "FROM st_geomfromtext(:q, 4326) AS geom;")
                .bind("q", queryString).map(FloatColumnMapper.PRIMITIVE).first();

        return response;
    }

    public List<PointMarkerRssi> getPoints() {
        return pointQuery.getAll();
    }

    /**
     * Получает треугольники Делоне из таблицы
     * @return
     */
    public List<TriangleMarkerRssi> getDelauneyTriangles() {
        return triangleQuery.getAll();
    }

    public void clearGpsTable() {
        gpsQuery.clear();
    }
}
