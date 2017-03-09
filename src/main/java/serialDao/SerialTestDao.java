package serialDao;

import common.entities.serialPart.GpsPoint;
import common.entities.visualPart.OuterShapeMarker;
import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.FloatColumnMapper;
import org.skife.jdbi.v2.util.IntegerColumnMapper;
import org.skife.jdbi.v2.util.StringColumnMapper;
import org.skife.jdbi.v2.util.TimestampColumnMapper;
import serialDao.queries.PolygonMapper;
import serialDao.queries.PolygonQuery;
import serialDao.queries.TriangleQuery;
import serialDao.queries.point.PointQuery;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
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

    private final PointQuery pointQuery;
    private final TriangleQuery triangleQuery;
    private final PolygonQuery voronoiQuery;

    private SimpleDateFormat tableTimedateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final SerialTestDao instance = new SerialTestDao();

    public static final SerialTestDao getInstance() {
        return instance;
    }

    private SerialTestDao() {

        createConnection();

        pointQuery = h.attach(PointQuery.class);
        triangleQuery = h.attach(TriangleQuery.class);
        voronoiQuery = h.attach(PolygonQuery.class);

        log.info("БД загружена");
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

        pointQuery.addPoint(
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

            pointQuery.addPointWithRssi(
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
                .bind("q", queryString)
                .map(FloatColumnMapper.PRIMITIVE)
                .first();

        return response;
    }

    /**
     * Получает из БД внешнюю границу всех полигонов Делоне и возвращает маркер внешнего полигона
     *
     * @return - Маркер
     */
    public OuterShapeMarker getDelaunayBoundary() {

        List<Location> outerShapeLocations;

        // Получает внешнюю границу запросов в формате EWKT и парсит из нее список координат
        try {

            outerShapeLocations =
                    PolygonMapper.parseArray(true,
                            h.createQuery(
                                    "SELECT st_astext(st_union(geom)) FROM l_delauney_shape;")
                                    .map(StringColumnMapper.INSTANCE)
                                    .first()
                    );

        } catch (NullPointerException ex) {
            log.warn("Нет объектов в базе для построения внешней границы");
            outerShapeLocations = Collections.emptyList();
        }

        return new OuterShapeMarker(outerShapeLocations);
    }

    /**
     * Возвращает список всех точек
     * @return
     */
    public List<PointMarkerRssi> getPoints() {
        return pointQuery.getAll();
    }

    /**
     * Возвращает список точек в указанном отрезке времени
     * @param fromDateTime
     * @param toDateTime
     * @return
     */
    public List<PointMarkerRssi> getPointsByDateTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {

        return pointQuery.getByDateTimePeriod(
                getTimestamp(fromDateTime),
                getTimestamp(toDateTime)
        );
    }

    public List<TriangleMarkerRssi> getDelaunayByDateTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {

        triangleQuery.generateTrianglesByTimestamp(
                getTimestamp(fromDateTime),
                getTimestamp(toDateTime)
        );

        return triangleQuery.getAll();
    }

    /**
     * Возвращает число точек, добавленных в таблицу в указанный отрезок времени
     * ISO формат времени
     * @return
     */
    public int getCountPoints(LocalDateTime fromTimeDate, LocalDateTime toTimeDate) {

        return h.createQuery(
                "SELECT count(*) FROM gpspoint WHERE time >= :from AND time <= :to;"
        )
                .bind("from", getTimestamp(fromTimeDate))
                .bind("to", getTimestamp(toTimeDate))
                .map(IntegerColumnMapper.PRIMITIVE)
                .first();
    }

    public LocalDateTime getDateTimeOfEarliestPoint() {

        try {
            return h.createQuery(
                    "SELECT time FROM gpspoint ORDER BY time LIMIT 1;"
            )
                    .map(TimestampColumnMapper.INSTANCE)
                    .first()
                    .toLocalDateTime();
        } catch (NullPointerException ex) {
            return LocalDateTime.now();
        }
    }

    public LocalDateTime getDateTimeOfLatestPoint() {

        try {
            return h.createQuery(
                    "SELECT time FROM gpspoint ORDER BY time DESC LIMIT 1;"
            )
                    .map(TimestampColumnMapper.INSTANCE)
                    .first()
                    .toLocalDateTime();
        } catch (NullPointerException ex) {
            return LocalDateTime.now();
        }
    }


    /**
     * Получает треугольники Делоне из таблицы
     * @return
     */
    public List<TriangleMarkerRssi> getDelaunayTriangles() {
        triangleQuery.generateTriangles();
        return triangleQuery.getAll();
    }


    public void clearTables() {
        pointQuery.clear();
        triangleQuery.clear();
    }

    /**
     * Возвращает jdbc обертку даты-времени для поиска в бд
     * @param localDateTime - дата-время
     * @return
     */
    public Timestamp getTimestamp(LocalDateTime localDateTime) {
        return Timestamp.from(
                localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );
    }


}
