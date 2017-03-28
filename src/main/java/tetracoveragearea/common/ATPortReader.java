package tetracoveragearea.common;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.entities.serialPart.GpsPoint;
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
import java.time.LocalDateTime;
import java.util.*;

import static tetracoveragearea.common.entities.serialPart.GpsPoint.LatitudeSign.NORTH;
import static tetracoveragearea.common.entities.serialPart.GpsPoint.LatitudeSign.SOUTH;
import static tetracoveragearea.common.entities.serialPart.GpsPoint.LongitudeSign.EAST;
import static tetracoveragearea.common.entities.serialPart.GpsPoint.LongitudeSign.WEST;

/** Класс чтения сообщений терминала */
public class ATPortReader extends SentenceAdapter implements SerialPortEventListener {

    private List<ATListener> atListeners = new ArrayList<>();

    public static final Logger log = LogManager.getLogger(ATPortReader.class);

    private boolean networkAlive;
    private boolean gpsAlive;

    private int gpsFailureCount = 0;
    private int rssiFailureCount = 0;

    // Таймаут простоя в режиме сбора данных
    private final int BREAK_CAPTURE_TIMEOUT = 6000;

    private Timer responseTimeoutTimer;

    private String nmea;

    // Ссылка на прослушиваемый порт
    private SerialPort serialPort;

    // Парсер сообщений NMEA
    private SentenceReader sentenceReader;

    // Байтовый поток с сообщением NMEA
    private InputStream nmeaStream = new ByteInputStream();

    // Текущее значение rssi
    private OptionalDouble currentRssi = OptionalDouble.empty();

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

        networkAlive = true;
        gpsAlive = true;

        startResponseTimeoutTimer();
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

                        networkAlive = true;

                        try {

                            Double rssi = Double.parseDouble(
                                    data.split("\\+CSQ: ")[1].substring(1, 3));

                            if (!currentRssi.isPresent() || rssi != currentRssi.getAsDouble()) {
                                currentRssi = OptionalDouble.of(rssi);
                                log.info("Обновлено значение rssi: " + rssi);
                            } else {
                                log.info("Принятый rssi не изменился");
                            }

                        } catch (NumberFormatException ex) {
                            log.info("Принят rssi, но не распознано значение");
                        }
                    } else if (dataRow.contains("$GPRMC")) {

                        gpsAlive = true;

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

                        // Если приходит сообщение о необслуживании терминала сетью, то отсылаем ошибку сети слушателям
                    } else if (data.contains("+CME ERROR: 30")) {

                        notifyOnNetworkError();
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
                "Широта: " + (lat.isPresent() ? lat.getAsDouble() : "не принята\n") +
                "Долгота: " + (lon.isPresent() ? lon.getAsDouble() + " " : "не принята\n") +
                "Скорость: " + (speed.isPresent() ? speed.getAsDouble() + " " : "не принята\n") +
                "Курс: " + (course.isPresent() ? course.getAsDouble() : "не принят")
        );

        sentenceReader.stop();
    }

    public OptionalDouble getCurrentRssi() {
        return currentRssi;
    }

    public GpsPoint getCurrentGpsPoint() {
        return currentGpsPoint;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void startGpsResponseTimeoutTimer() {
        responseTimeoutTimer = new Timer();
        responseTimeoutTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        gpsAlive = false;
                    }
                }, BREAK_CAPTURE_TIMEOUT);
    }

    /**
     * Монитор успешного получения данных
     * Отсылает сообщения об ошибках приема данных, если они не были распознаны 3 периода подряд
     */
    public void startResponseTimeoutTimer() {
        responseTimeoutTimer = new Timer();
        responseTimeoutTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {

                        if (networkAlive && gpsAlive) {

                            if (currentGpsPoint.getSignificantLongitude().isPresent() && currentGpsPoint.getSignificantLatitude().isPresent() &&
                                    currentRssi.isPresent()) {
                                // Если все хорошо, то добавляем точку в хранилище
                                GeometryStore.getInstance().addPoint(
                                        new Point(currentGpsPoint.getSignificantLatitude().getAsDouble(),
                                                currentGpsPoint.getSignificantLongitude().getAsDouble(),
                                                currentRssi.getAsDouble(),
                                                LocalDateTime.now()
                                        ));

                                log.info("Была отправлена точка в базу");

                                gpsFailureCount = 0;
                                rssiFailureCount = 0;
                            }

                            log.info("Все было распознано, но данные оказались битыми");

                        } else {
                            // Увеличиваем счетчики
                            gpsFailureCount = gpsAlive ? 0 : gpsFailureCount + 1;
                            rssiFailureCount = networkAlive ? 0 : gpsFailureCount + 1;
                            log.info("Счетчик ошибок приема: GPS - " + gpsFailureCount + " RSSI - " + rssiFailureCount);

                            // Шлем уведомление при превышении пороге
                            if (gpsFailureCount > 3 && gpsFailureCount > 3) {
                                notifyOnDeviceError();
                            } else if (gpsFailureCount > 3) {
                                notifyOnGpsError();
                            } else if (rssiFailureCount > 3) {
                                notifyOnNetworkError();
                            }
                        }

                        networkAlive = false;
                        gpsAlive = false;
                    }
                }, BREAK_CAPTURE_TIMEOUT);
    }

    public void stopResponseTimeoutTimer() {

        responseTimeoutTimer.cancel();
    }

    public void addATListener(ATListener listener) {
        atListeners.add(listener);
    }

    public void removeATListener(ATListener listener) {
        atListeners.remove(listener);
    }

    public void notifyOnNetworkError() {
        atListeners.forEach(listener -> listener.onNetworkError());
    }

    public void notifyOnGpsError() {
        atListeners.forEach(listener -> listener.onGpsError());
    }

    public void notifyOnDeviceError() {
        atListeners.forEach(listener -> listener.onDeviceError());
    }


}