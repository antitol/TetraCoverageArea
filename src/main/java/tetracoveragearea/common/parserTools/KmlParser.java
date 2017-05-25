package tetracoveragearea.common.parserTools;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import de.micromata.opengis.kml.v_2_2_0.*;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.telnet.BStation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
            Document pointDoc = document.createAndAddDocument();
            pointDoc.createAndAddPlacemark()
                    .withTimePrimitive(
                            new TimeStamp().withWhen(writePoint.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                    )
                    .createAndSetPoint()
                    .addToCoordinates(writePoint.getY(), writePoint.getX(), writePoint.getZ());

            pointDoc.withExtendedData(KmlFactory.createExtendedData().withData(Arrays.asList(
                    KmlFactory.createData(String.valueOf(writePoint.getBStation().getId())).withId("bs_id"),
                    KmlFactory.createData(String.valueOf(writePoint.getSsi())).withId("ssi"))
            ));
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

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Point> points = new ArrayList<>();
        Kml parseKml = Kml.unmarshal(file);

        Document document = (Document) parseKml.getFeature();
        for (Feature feature : document.getFeature()) {
            Coordinate coordinate = new Coordinate(0, 0, 0);
            TimeStamp pointTimestamp = new TimeStamp();
            int ssi = 0;
            int bs_id = 0;

            for (Feature subfeature : ((Document) feature).getFeature()) {
                de.micromata.opengis.kml.v_2_2_0.Point parsePoint = (de.micromata.opengis.kml.v_2_2_0.Point) ((Placemark) subfeature).getGeometry();
                coordinate = parsePoint.getCoordinates().get(0);
                pointTimestamp = (TimeStamp) subfeature.getTimePrimitive();

            }

            for (Data data : feature.getExtendedData().getData()) {
                if (data.getId().equals("ssi")) {
                    ssi = Integer.parseInt(data.getValue());
                } else if (data.getId().equals("bs_id")) {
                    bs_id = Integer.parseInt(data.getValue());
                }
            }

            try {
                points.add(new Point(
                        coordinate.getLatitude(),
                        coordinate.getLongitude(),
                        coordinate.getAltitude(),
                        LocalDateTime.parse(pointTimestamp.getWhen(), formatter),
                        ssi, BStation.getById(bs_id)
                ));
            } catch (NullPointerException ex) {

            }
        }

        return points;
    }
}
