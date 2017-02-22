package serialDao.processingQueries;

import common.entities.visualPart.PolygonMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by anatoliy on 20.02.17.
 */
public class VoronoiMapper implements ResultSetMapper<PolygonMarkerRssi> {
    @Override
    public PolygonMarkerRssi map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        // Парсим ответ в список координат точек полигона
        List<Location> locations = parseArray(r.getString("geom"));

        int rssi = r.getInt("rssi");

        return new PolygonMarkerRssi(locations, rssi);
    }

    /**
     * Преобразует строку типа "POLYGON((x1 y1,...,xN yN))" в список координат
     * Таблица отдает точки в виде "долгота широта"
     *
     * @param string
     * @return
     */
    public List<Location> parseArray(String string) {

        string = string.replaceAll("(POLYGON|\\(|\\))", "");


        return Arrays.asList(string.split("\\,"))
                .stream()
                .map(p ->
                                new Location(
                                        Double.parseDouble(p.split("\\s")[1]),
                                        Double.parseDouble(p.split("\\s")[0]))
                )
                .collect(Collectors.toList());
    }
}
