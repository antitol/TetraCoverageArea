package common;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import common.entities.serialPart.GpsPoint;
import common.entities.serialPart.Rssi;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.util.Position;
import serialDao.SerialTestDao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anatoliy on 13.12.16.
 */

public class SerialTest {

    private static SerialPort serialPort;

    // Таймеры запроса данных
    private static Timer rssiTimer = new Timer();
    private static Timer gpsTimer = new Timer();

    // AT команды запроса данных
    private static final String GPS_AT_QUERY = "AT+CSQ?";
    private static final String RSSI_AT_QUERY = "AT+CGPS=20";


    public SerialTest() {}

    public static SerialPort getSerialPort() {
        return serialPort;
    }

    /**
     * Запуск таймеров запросов
     *
     * @param delayRssi - интервал запросов rssi
     * @param delayGps - интервал запросов gps
     */
    public static void startTimers(int delayRssi, int delayGps) {

            rssiTimer.schedule(getTimerTask(rssiTimer, GPS_AT_QUERY), 0, delayRssi);
            gpsTimer.schedule(getTimerTask(gpsTimer, RSSI_AT_QUERY), 1550, delayGps);
    }

    /** Останов таймеров */
    public static void stopTimers() {
        rssiTimer.cancel();
        gpsTimer.cancel();
    }

    /**
     * Открытие порта
     *
     * @param port - порт
     */
    public static void initPort(String port) {

        for (Integer i=0; i<1; i++) {

            serialPort = new SerialPort(port);

            try {
                serialPort.openPort();

                //Выставляем параметры
                serialPort.setParams(SerialPort.BAUDRATE_38400,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                System.out.println("SerialPort " + serialPort.getPortName() + " opened");

                //Устанавливаем ивент listener и маску
                serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

                System.err.println("Start requesting");

                break;

            } catch (SerialPortException ex) {
                System.err.println("SerialPort " + serialPort.getPortName() + " not exist");
            } catch (IOException e) {
                System.err.println("IOException receive");
            }
        }
    }

    /**
     * Создание таймера с отправкой запроса по серийному порту
     *
     * @param timer - таймер
     * @param request - запрос
     * @return
     */
    public static TimerTask getTimerTask(Timer timer, String request) {

        return new TimerTask() {
            @Override
            public void run() {

                if (!serialPort.isOpened()) {
                    timer.cancel();
                    return;
                }

                try {
                    serialPort.writeString(request + "\r\n");
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /** Класс чтения сообщений терминала */
    private static class PortReader implements SerialPortEventListener, SentenceListener {

        private String nmea;
        private SentenceReader sentenceReader;
        private InputStream nmeaStream = new ByteInputStream();

        DecimalFormat latFormatter = new DecimalFormat("##.######");
        DecimalFormat lonFormatter = new DecimalFormat("###.######");

        DecimalFormatSymbols formatSymbols = latFormatter.getDecimalFormatSymbols();


        public PortReader() throws IOException {
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

                    // Если пришел RSSI
                    if (data.contains("+CSQ: ")) {

                        try {

                            Integer rssi = Integer.parseInt(data.split("\\+CSQ: ")[1].substring(1,3));
                            System.err.println("RSSI is -" + rssi + "dBm at " + LocalDateTime.now().toString());

                            // Отправляем RSSI в базу данных
                            SerialTestDao.getInstance().addRssi(
                                    new Rssi(Optional.of(rssi))
                            );

                        } catch (NumberFormatException ex) {}

                    // Если пришли данные NMEA
                    } else if (data.contains("$GPRMC")) {

                        String responses[] = data.split("\n");

                        for (String response : responses) {
                            if (response.contains("GPRMC")) {
                                nmea = response;
                            }
                        }

                        // Создаем байтовый поток из строки GPRMC
                        nmeaStream = new ByteInputStream(nmea.getBytes(StandardCharsets.UTF_8), nmea.getBytes().length);


                        sentenceReader.setInputStream(nmeaStream);
                        sentenceReader.setPauseTimeout(50);
                        sentenceReader.start();
                    }
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                } catch (IllegalStateException ex) {

                }
            }
        }

        @Override
        public void readingPaused() {
        }

        @Override
        public void readingStarted() {
        }

        @Override
        public void readingStopped() {
        }

        @Override
        public void sentenceRead(SentenceEvent sentenceEvent) {
            RMCSentence sentence = ((RMCSentence) sentenceEvent.getSentence());
            Position position = sentence.getPosition();

            Optional lat;
            Optional lat_hem;
            Optional lon;
            Optional lon_hem;
            Optional speed;
            Optional course;

            try {
                System.out.println("Latitude is " + latFormatter.format(position.getLatitude()) +
                        position.getLatitudeHemisphere().toChar() + " " +
                        LocalDateTime.now().toString());

                lat = Optional.of(latFormatter.format(position.getLatitude()));
                lat_hem = position.getLatitudeHemisphere().toChar() == 'N' ? Optional.of(true) : Optional.of(false);

            } catch (DataNotAvailableException ex) {
                System.err.println("Latitude is not sended");

                lat = Optional.empty();
                lat_hem = Optional.empty();
            }

            try {
                System.out.println("Longitude is " + lonFormatter.format(position.getLongitude()) +
                        position.getLongitudeHemisphere().toChar() + " " +
                        LocalDateTime.now().toString());

                lon = Optional.of(latFormatter.format(position.getLongitude()));
                lon_hem = position.getLongitudeHemisphere().toChar() == 'N' ? Optional.of(true) : Optional.of(false);
            } catch (DataNotAvailableException ex) {
                System.err.println("Longitude is not sended");
                lon = Optional.empty();
                lon_hem = Optional.empty();
            }

            try {
                System.out.println("Speed is " + sentence.getSpeed());
                speed = Optional.of(sentence.getSpeed());
            } catch (DataNotAvailableException ex) {
                System.err.println("Speed is not sended");
                speed = Optional.empty();
            }

            try {
                System.out.println("Course is " + sentence.getCourse());
                course = Optional.of(sentence.getCourse());
            } catch (DataNotAvailableException ex) {
                System.err.println("Course is not sended");
                course = Optional.empty();
            }

            SerialTestDao.getInstance().addGpsPoint(
                    new GpsPoint(
                            lat, lon, lat_hem, lon_hem, speed, course
                    )
            );

            sentenceReader.stop();
        }
    }
}
