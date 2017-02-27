package serialDao;

import common.entities.serialPart.GpsPoint;
import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.FloatColumnMapper;
import serialDao.processingQueries.TriangleQuery;
import serialDao.processingQueries.VoronoiQuery;
import serialDao.processingQueries.point.PointQuery;
import serialDao.serialQueries.gps.GpsQuery;

import java.util.List;
import java.util.OptionalInt;

/**
 * Created by anatoliy on 17.01.17.
 */
public class SerialTestDao {

    public static final Logger log = Logger.getLogger(SerialTestDao.class);

    private String url = "jdbc:postgresql://127.0.0.1:5432/postgis";
    private String login = "postgis";
    private String pass = "postgis";
    private Handle h;
    private DBI dbi;

    private final GpsQuery gpsQuery;
    private final PointQuery pointQuery;
    private final TriangleQuery triangleQuery;
    private final VoronoiQuery voronoiQuery;

    private static final SerialTestDao instance = new SerialTestDao();

    public static final SerialTestDao getInstance() {
        return instance;
    }

    private SerialTestDao() {
        log.info("Я живой");
        createConnection();

        gpsQuery = h.attach(GpsQuery.class);
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

            log.warn("Ошибка PostgreSQL JDBC драйвера");
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

        gpsQuery.add(
                point.getSignificantLatitude(),
                point.getSignificantLongitude()
        );

        log.info("Добавлена точка: " + point.getSignificantLatitude() + " " + point.getSignificantLongitude());
    }

    /**
     * Добавляет точку с значением уровня сигнала
     * @param point
     * @param rssi
     */
    public void addGpsWithRssiPoint(GpsPoint point, OptionalInt rssi) {

        // Добавляем точку, если были получены координаты и rssi
        if (point.getLatitude().isPresent() && rssi.isPresent()) {

            gpsQuery.addWithRssi(
                    point.getSignificantLatitude(),
                    point.getSignificantLongitude(),
                    rssi.getAsInt()
            );

            log.info(
                    "Добавлена точка со значением rssi: " +
                            point.getSignificantLatitude() + " " +
                            point.getSignificantLongitude() + " " +
                            rssi.getAsInt()
            );
        }


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
        triangleQuery.generateTriangles();
        return triangleQuery.getAll();
    }

    public void clearGpsTable() {
        gpsQuery.clear();
    }
}
