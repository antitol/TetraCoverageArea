package gui.panels;

import common.ATPortReader;
import gui.components.GuiComponents;
import jssc.*;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import serialDao.SerialTestDao;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static gui.panels.SerialGuiPanel.ATRequest.GPS_AT_QUERY;
import static gui.panels.SerialGuiPanel.ATRequest.RSSI_AT_QUERY;

/**
 * Панель взаимодействия с серийным портом
 *
 * Created by anatoliy on 17.02.17.
 */
public class SerialGuiPanel extends JPanel {

    public static final Logger log = Logger.getLogger(SerialGuiPanel.class);

    private int i,k = 0;

    public enum ATRequest {
        RSSI_AT_QUERY("AT+CSQ?"),
        GPS_AT_QUERY("AT+CGPS=20");

        private String request;

        ATRequest(String request) {
            this.request = request;
        }

        public String getRequest() {
            return request;
        }
    }

    // Таймауты запросов на порт
    private final int CHECK_PORT_TIME_REQUEST = 1000;
    private final int GPS_TIME_REQUEST = 5000;
    private final int RSSI_TIME_REQUEST = 5000;

    // Таймаут сброса точки в бд
    private final int DB_INSERT_TIME_REQUEST = 5000;

    private JLabel selectPortLabel = new JLabel("Последовательный порт: ");
    private JLabel portStateLabel = new JLabel("Устройство не подключено");
    private JComboBox portBox = new JComboBox(SerialPortList.getPortNames());
    private JToggleButton startDataCaptureButton;
    private JButton listenButton;
    private JButton testModeButton;

    // Таймеры запроса данных
    private Timer rssiRequestTimer;
    private Timer gpsRequestTimer;
    private Timer pointToDatabaseTimer;

    // Таймер прослушки порта до начала сбора данных
    private Timer listenTimer;

    private SerialPort serialPort;

    // Считыватель ответа устройства на команду AT для потверждения инициализации
    private SerialPortReader serialPortReader;

    // Считыватель точек покрытия
    private ATPortReader portReader = new ATPortReader(serialPort);

    public SerialGuiPanel() {

        setLayout(new MigLayout());

        startDataCaptureButton = new JToggleButton("Начать сбор данных");

        startDataCaptureButton.setUI(GuiComponents.getToggleButtonGreenUI());
        startDataCaptureButton.addActionListener(e -> {

                    // При старте сбора данных:
                    if (startDataCaptureButton.isSelected()) {

                        stopPortMonitoring();
                        startCapturingData();

                    // При останове сбора данных:
                    } else {

                        stopCapturingData();
                        startPortMonitoring();
                    }
                }
        );

        listenButton = new JButton("Listen");
        listenButton.addActionListener(e -> {

        });

        portBox.addActionListener(e -> {

                    Object selected = portBox.getSelectedItem();
                    portBox.setModel(new DefaultComboBoxModel(SerialPortList.getPortNames()));
                    portBox.setSelectedItem(selected);

                    breakPortConnection();
                    serialPort = new SerialPort(selected.toString());
                }
        );

        List<JComponent> components = Arrays.asList(selectPortLabel, portBox, portStateLabel, startDataCaptureButton);


        for (JComponent component : components) {
            add(component, "w 100%, wrap");
        }

        startPortMonitoring();
    }

    /**
     * Запускает таймер проверки доступности устройства на выбранном порту
     */
    public void startPortMonitoring() {
        portBox.setEnabled(true);
        startDataCaptureButton.setEnabled(false);
        listenTimer = new Timer();
        listenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {

                    if (!serialPort.isOpened()) {

                        serialPort.openPort();
                    } else {
                        serialPort.removeEventListener();
                    }

                    serialPortReader = new SerialPortReader(serialPort);
                    serialPort.addEventListener(serialPortReader);

                    serialPort.setParams(SerialPort.BAUDRATE_38400,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

                    serialPort.writeString("AT\r\n");

                    // Опытным путем установлено, что для прихода ответа требуется 5 мс
                    Thread.sleep(25);


                    if (serialPortReader.isDeviceConnected()) {
                        portStateLabel.setText("Устройство опознано");
                        startDataCaptureButton.setEnabled(true);
                    } else {
                        breakPortConnection();
                    }


                } catch (SerialPortException | InterruptedException | NullPointerException ex) {
                    log.info("Порт " + portBox.getSelectedItem().toString() + " недоступен");
                }
            }
        }, 0, 1000);
    }

    /**
     * Читает ответы устройства на проверку доступности
     */
    private class SerialPortReader implements SerialPortEventListener {

        SerialPort serialPort;

        // По умолчанию устройство принимается отключенным
        private boolean deviceConnected = false;

        public boolean isDeviceConnected() {
            return deviceConnected;
        }

        public SerialPortReader(SerialPort serialPort) {
            this.serialPort = serialPort;
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            try {
                String response = serialPort.readString(serialPortEvent.getEventValue());

                // Если устройство отправило ответ, содержащий OK - то оно признается подключенным
                if (response.contains("OK")) {

                    deviceConnected = true;
                }
            } catch (SerialPortException ex) {}
        }
    }

    /**
     * Обрывает соединение с портом и изменяет элементы соответсвенно
     */
    public void breakPortConnection() {

        try {
            serialPort.closePort();
        } catch (SerialPortException | NullPointerException  ex) {}

        portStateLabel.setText("Устройство не опознано");
        startDataCaptureButton.setEnabled(false);
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

        rssiRequestTimer.schedule(getATTimerTask(rssiRequestTimer, RSSI_AT_QUERY.getRequest()), 0, delayRssi);
        gpsRequestTimer.schedule(getATTimerTask(gpsRequestTimer, GPS_AT_QUERY.getRequest()), 500, delayGps);
        pointToDatabaseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SerialTestDao.getInstance().addGpsWithRssiPoint(
                        portReader.getCurrentGpsPoint(),
                        portReader.getCurrentRssi()
                );
            }
        }, delayGps, delayGps);

    }

    /** Останов таймеров */
    public void stopTimers() {
        try {
            rssiRequestTimer.cancel();
            gpsRequestTimer.cancel();
            pointToDatabaseTimer.cancel();
        } catch (NullPointerException ex) {}
    }

    /**
     * Создание таймера с отправкой запроса по серийному порту
     *
     * @param timer - таймер
     * @param request - запрос
     * @return
     */
    public TimerTask getATTimerTask(Timer timer, String request) {

        return new TimerTask() {
            @Override
            public void run() {


                try {
                    if (portReader.isAlive()) {
                        serialPort.writeString(request + "\r\n");
                    } else {
                        throw new SerialPortException(portBox.getSelectedItem().toString(), "alive", SerialPortException.TYPE_PORT_NOT_FOUND);
                    }
                } catch (SerialPortException e) {
                    stopCapturingData();
                    startPortMonitoring();
                    startDataCaptureButton.setSelected(false);
                    showLostConnectionError();
                }
            }
        };
    }

    /**
     * Запуск работы порта в режиме сбора данных
     */
    public void startCapturingData() {
        // Заменяем слушателя ответов на проверку слушателем данных
        try {
                serialPort.removeEventListener();
                portReader.setSerialPort(serialPort);
                serialPort.addEventListener(portReader);
                portReader.setAlive(true);
                portStateLabel.setText("Сбор данных...");
                portBox.setEnabled(false);
        } catch (SerialPortException ex) {
            log.info("Не удалось подменить слушателя");
        }

        // Запускаем таймеры на отправку запросов
        startTimers(RSSI_TIME_REQUEST, GPS_TIME_REQUEST, DB_INSERT_TIME_REQUEST);

        startDataCaptureButton.setEnabled(true);
        startDataCaptureButton.setText("Завершить");
        log.info("Старт сбора данных");
    }

    /**
     * Прекращение работы порта в режиме сбора данных
     */
    public void stopCapturingData() {

        startDataCaptureButton.setText("Начать сбор данных");

        try {
            stopTimers();
            serialPort.removeEventListener();
            breakPortConnection();
        } catch (SerialPortException e1) {
            e1.printStackTrace();
        }
        log.info("Останов сбора данных");
    }

    /**
     * Прекращение прослушивания портов в режиме проверки доступности устройства
     */
    public void stopPortMonitoring() {
        listenTimer.cancel();
        log.info("Останов мониторинга");
    }

    public void showLostConnectionError() {
        JOptionPane.showMessageDialog(this, "Нет ответа от устройства");
    }

}
