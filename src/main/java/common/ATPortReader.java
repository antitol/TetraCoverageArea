package common;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import common.entities.serialPart.GpsPoint;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.Position;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static common.entities.serialPart.GpsPoint.LatitudeSign.NORTH;
import static common.entities.serialPart.GpsPoint.LatitudeSign.SOUTH;
import static common.entities.serialPart.GpsPoint.LongitudeSign.EAST;
import static common.entities.serialPart.GpsPoint.LongitudeSign.WEST;

/** Класс чтения сообщений терминала */
public class ATPortReader extends SentenceAdapter implements SerialPortEventListener {

    public static final Logger log = LogManager.getLogger(ATPortReader.class);

    private String nmea;

    // Ссылка на прослушиваемый порт
    private SerialPort serialPort;

    // Парсер сообщений NMEA
    private SentenceReader sentenceReader;

    // Байтовый поток с сообщением NMEA
    private InputStream nmeaStream = new ByteInputStream();

    // Текущее значение rssi
    private OptionalInt currentRssi = OptionalInt.empty();

    private GpsPoint currentGpsPoint = new GpsPoint();

    DecimalFormat latFormatter = new DecimalFormat("##.######");
    DecimalFormat lonFormatter = new DecimalFormat("###.######");

    DecimalFormatSymbols formatSymbols = latFormatter.getDecimalFormatSymbols();


    public ATPortReader(SerialPort serialPort) {

            this.serialPort = serialPort;

            sentenceReader = new SentenceReader(nmeaStream);
            sentenceReader.addSentenceListener(this);

            formatSymbols.setDecimalSeparator('.');

            latFormatter.setDecimalFormatSymbols(formatSymbols);
            lonFormatter.setDecimalFormatSymbols(formatSymbols);
    }

    /** Событие слушателя порта */
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0){

            try {

                // Чтение принимаемых данных
                String data = serialPort.readString(event.getEventValue());

                for (String dataRow : data.split("\n")) {
                    // Если пришел RSSI
                    if (dataRow.contains("+CSQ:")) {

                        try {

                            Integer rssi = Integer.parseInt(
                                    data.split("\\+CSQ: ")[1].substring(1, 3));

                            if (!currentRssi.isPresent() || rssi != currentRssi.getAsInt()) {
                                currentRssi = OptionalInt.of(rssi);
                                log.info("Обновлено значение rssi: " + rssi);
                            } else {
                                log.info("Принятый rssi не изменился");
                            }

                        } catch (NumberFormatException ex) {
                            log.info("Принят rssi, но не распознано значение");
                        }
                    } else if (dataRow.contains("$GPRMC")) {

                        nmea = dataRow;

                        // Создаем байтовый поток из строки GPRMC
                        nmeaStream = new ByteInputStream(
                                nmea.getBytes(StandardCharsets.UTF_8),
                                nmea.getBytes().length
                        );

                        // Передаем парсеру поток данных
                        sentenceReader.setInputStream(nmeaStream);

                        // Сообщения могут приходить минимум через 50мс
                        sentenceReader.setPauseTimeout(50);
                        sentenceReader.start();
                    }
                }
            }
            catch (SerialPortException ex) {
                log.warn("Ошибка серийного порта");
            } catch (IllegalStateException ex) {
                log.warn("Неверное состояние");
            }
        }
    }

    @Override
    public void sentenceRead(SentenceEvent sentenceEvent) {
        // Поток преобразуется в RMC предложение
        RMCSentence sentence = ((RMCSentence) sentenceEvent.getSentence());
        Position position = sentence.getPosition();

        OptionalDouble lat;
        Optional lat_hem;
        OptionalDouble lon;
        Optional lon_hem;
        OptionalDouble speed;
        OptionalDouble course;

        try {

            lat = OptionalDouble.of(Double.parseDouble(latFormatter.format(position.getLatitude())));
            lat_hem = position.getLatitudeHemisphere().toChar() == 'N' ? Optional.of(NORTH) : Optional.of(SOUTH);

        } catch (DataNotAvailableException ex) {

            lat = OptionalDouble.empty();
            lat_hem = Optional.empty();
        }

        try {

            lon = OptionalDouble.of(Double.parseDouble(latFormatter.format(position.getLongitude())));
            lon_hem = position.getLongitudeHemisphere().toChar() == 'E' ? Optional.of(EAST) : Optional.of(WEST);
        } catch (DataNotAvailableException ex) {

            lon = OptionalDouble.empty();
            lon_hem = Optional.empty();
        }

        try {

            speed = OptionalDouble.of(sentence.getSpeed());
        } catch (DataNotAvailableException ex) {

            speed = OptionalDouble.empty();
        }

        try {

            course = OptionalDouble.of(sentence.getCourse());
        } catch (DataNotAvailableException ex) {

            course = OptionalDouble.empty();
        }

        currentGpsPoint = new GpsPoint(lat, lon, lat_hem, lon_hem, speed, course);

        log.info(
                "Распознана точка GPS: \n" +
                "Широта: " + (lat.isPresent() ? lat.getAsDouble() : "не принята ") +
                "Долгота: " + (lon.isPresent() ? lon.getAsDouble() + " " : "не принята ") +
                "Скорость: " + (speed.isPresent() ? speed.getAsDouble() + " " : "не принята ") +
                "Курс: " + (course.isPresent() ? course.getAsDouble() : "не принят")
        );

        sentenceReader.stop();
    }

    public OptionalInt getCurrentRssi() {
        return currentRssi;
    }

    public GpsPoint getCurrentGpsPoint() {
        return currentGpsPoint;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
}