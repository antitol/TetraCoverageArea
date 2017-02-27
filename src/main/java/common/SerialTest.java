package common;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.apache.log4j.Logger;
import serialDao.SerialTestDao;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anatoliy on 13.12.16.
 */

public class SerialTest {

    public static final Logger log = Logger.getLogger(SerialTest.class);

    private SerialPort serialPort;
    private PortReader portReader = new PortReader();

    // Таймеры запроса данных
    private Timer rssiRequestTimer;
    private Timer gpsRequestTimer;
    private Timer pointToDatabaseTimer;

    // AT команды запроса данных
    private final String GPS_AT_QUERY = "AT+CSQ?";
    private final String RSSI_AT_QUERY = "AT+CGPS=20";


    public SerialTest() {}

    private static SerialTest instance = new SerialTest();

    public static SerialTest getInstance() {
        return instance;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    /**
     * Запуск таймеров запросов
     *
     * @param delayRssi - интервал запросов rssi
     * @param delayGps - интервал запросов gps
     */
    public void startTimers(int delayRssi, int delayGps, int delayDatabase) {

        rssiRequestTimer = new Timer();
        gpsRequestTimer = new Timer();
        pointToDatabaseTimer = new Timer();

        rssiRequestTimer.schedule(getTimerTask(rssiRequestTimer, GPS_AT_QUERY), 0, delayRssi);
        gpsRequestTimer.schedule(getTimerTask(gpsRequestTimer, RSSI_AT_QUERY), 500, delayGps);
        pointToDatabaseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SerialTestDao.getInstance().addGpsWithRssiPoint(
                        portReader.getCurrentGpsPoint(),
                        portReader.getCurrentRssi()
                );
            }
        }, delayGps, delayGps);

        log.info("Таймеры запущены");

    }

    /** Останов таймеров */
    public void stopTimers() {
        rssiRequestTimer.cancel();
        gpsRequestTimer.cancel();
        pointToDatabaseTimer.cancel();
        log.info("Таймеры остановлены");
    }

    /**
     * Открытие порта
     *
     * @param port - порт
     */
    public boolean initPort(String port) {


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
            serialPort.addEventListener(portReader, SerialPort.MASK_RXCHAR);

            log.info("Порт " + serialPort.getPortName() + " открыт");

            return true;

        } catch (SerialPortException ex) {
            log.warn("Серийный порт " + serialPort.getPortName() + " не существует");
        }

        return false;
    }

    /**
     * Создание таймера с отправкой запроса по серийному порту
     *
     * @param timer - таймер
     * @param request - запрос
     * @return
     */
    public TimerTask getTimerTask(Timer timer, String request) {

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

                }
            }
        };
    }
}
