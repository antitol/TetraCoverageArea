package tetracoveragearea.serialDao.queries;

import tetracoveragearea.common.entities.visualPart.PolygonMarkerRssi;
import de.fhpotsdam.unfolding.geo.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Не используется в данный момент
 *
 * Created by anatoliy on 20.02.17.
 */
public class PolygonMapper implements ResultSetMapper<PolygonMarkerRssi> {
    @Override
    public PolygonMarkerRssi map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        // Парсим ответ в список координат точек полигона
        List<Location> locations = parseArray(true, r.getString("geom"));

        return new PolygonMarkerRssi(locations, Collections.emptyList());
    }

    /**
     * Преобразует строку типа "POLYGON((x1 y1,...,xN yN))" в список координат
     * Таблица отдает точки в виде "долгота широта"
     *
     * @param ewktPolygon
     * @param threeDimensional - Полигон 3х / 2х-мерный
     * @return - список координатных точек
     */
    public static List<Location> parseArray(boolean threeDimensional, String ewktPolygon) {

        ewktPolygon = ewktPolygon.replaceAll( threeDimensional ? "(POLYGON Z |\\(|\\))" : "(POLYGON |\\(|\\))", "");

        return Arrays.stream(ewktPolygon.split("\\,"))
                .map(p ->
                                new Location(
                                        Double.parseDouble(p.split("\\s")[1]),
                                        Double.parseDouble(p.split("\\s")[0]))
                )
                .collect(Collectors.toList());
    }
}
