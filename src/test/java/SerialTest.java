import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.serialDao.SerialTestDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by anatoliy on 20.03.17.
 */
public class SerialTest {

    public static final Logger log = Logger.getLogger(SerialTest.class.getName());

    @Before
    public void connect() {
        try {
            SerialTestDao.getInstance().createConnection(
                    "localhost", "5432", "postgis", "postgis", "postgis"
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.warning("Connection fall");
        }
    }

    @Test
    /**
     * Тест распознавания времени точек
     */
    public void getPointsDateNum() {

        List<Point> points = SerialTestDao.getInstance().getPoints();

        log.info("Количество точек: " + points.stream().filter(point -> point.getDateTime().isBefore(LocalDateTime.now())).count());
    }

    @Test
    /**
     * Тест распознавания координат точек
     */
    public void getPointsLocationNum() {

        List<Point> points = SerialTestDao.getInstance().getPoints();

        log.info("Количество точек: " + points.stream()
                .filter(point ->
                point.getX() > 54 && point.getX() < 55 && point.getY() > 73.4 && point.getY() < 74
                )
                .count());
    }

    @After
    public void closeConnection() {
        SerialTestDao.getInstance().closeConnection();
    }
}
