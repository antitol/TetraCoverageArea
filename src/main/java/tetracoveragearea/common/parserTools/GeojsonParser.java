package tetracoveragearea.common.parserTools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.telnet.BStation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер / генератор точек в GeoJSON
 * Created by anatoliy on 05.04.17.
 */
public class GeojsonParser implements DocumentParser {

    @Override
    public void write(File file, List<Point> points) {

        FeatureCollection features = new FeatureCollection();

        for (Point point : points) {
            Feature feature = new Feature();
            feature.setGeometry(new org.geojson.Point(point.getY(), point.getX(), point.getZ()));
            feature.setProperty("Time", point.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            feature.setProperty("rssi", point.getZ());
            feature.setProperty("bs_id", point.getBStation().getId());
            feature.setProperty("ssi", point.getSsi());
            features.add(feature);
        }

        try {
            new ObjectMapper().writeValue(file, features);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Point> parse(File file) throws Exception {

        List<Point> points = new ArrayList<>();

        try {
            FeatureCollection featureCollection = new  ObjectMapper().readValue(file, FeatureCollection.class);

            for (Feature feature : featureCollection.getFeatures()) {

                LngLatAlt coordinates = ((org.geojson.Point) feature.getGeometry()).getCoordinates();

                Point point = new Point(
                        coordinates.getLatitude(),
                        coordinates.getLongitude(),
                        coordinates.getAltitude()
                );

                if (Double.isNaN(point.getZ()) || point.getZ() == 0.0) {
                    try {
                        point.setZ(Double.parseDouble(feature.getProperty("rssi")));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    LocalDateTime pointDateTime = LocalDateTime.parse(feature.getProperty("Time"), DateTimeFormatter.ISO_DATE_TIME);
                    point.setDateTime(pointDateTime);
                    point.setSsi(feature.getProperty("ssi"));
                    point.setbStation(BStation.getById(feature.getProperty("bs_id")));

                } catch (NullPointerException ex) {
                    point.setDateTime(LocalDateTime.now());
                }

                points.add(point);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return points;
    }
}
