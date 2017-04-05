package tetracoveragearea.common.parserTools;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import de.micromata.opengis.kml.v_2_2_0.*;
import tetracoveragearea.common.delaunay.Point;

import javax.xml.bind.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * KML парсер / генератор
 * Created by anatoliy on 04.04.17.
 */
public class KmlParser implements DocumentParser {

    @Override
    public void write(File file, List<Point> points) {

        Kml kml = new Kml();
        Document document = kml.createAndSetDocument();

        for (Point writePoint : points) {
            document.createAndAddPlacemark()
                    .withTimePrimitive(
                            new TimeStamp().withWhen(writePoint.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                    )
                    .createAndSetPoint()
                    .addToCoordinates(writePoint.getY(), writePoint.getX(), writePoint.getZ());
        }

        try {
            Marshaller marshaller = JAXBContext.newInstance(new Class[]{Kml.class}).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
                @Override
                public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                    return namespaceUri.matches("http://www.w3.org/\\d{4}/Atom") ? "atom"
                            : (
                            namespaceUri.matches("urn:oasis:names:tc:ciq:xsdschema:xAL:.*?") ? "xal"
                                    : (
                                    namespaceUri.matches("http://www.google.com/kml/ext/.*?") ? "gx"
                                            : (
                                            namespaceUri.matches("http://www.opengis.net/kml/.*?") ? ""
                                                    : (
                                                    null
                                            )
                                    )
                            )
                    );
                }
            });

            marshaller.marshal(kml, file);
        } catch (JAXBException ex) {}
    }

    @Override
    public List<Point> parse(File file) throws Exception {

        List<Point> points = new ArrayList<>();

        Kml parseKml = Kml.unmarshal(file);

        Document document = (Document) parseKml.getFeature();
        for (Feature feature : document.getFeature()) {
            de.micromata.opengis.kml.v_2_2_0.Point parsePoint = (de.micromata.opengis.kml.v_2_2_0.Point) ((Placemark) feature).getGeometry();
            Coordinate coordinate = parsePoint.getCoordinates().get(0);

            TimeStamp pointTimestamp = (TimeStamp) feature.getTimePrimitive();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            points.add(new Point(
                    coordinate.getLatitude(),
                    coordinate.getLongitude(),
                    coordinate.getAltitude(),
                    LocalDateTime.parse(pointTimestamp.getWhen(), formatter)
            ));
        }

        return points;
    }
}
