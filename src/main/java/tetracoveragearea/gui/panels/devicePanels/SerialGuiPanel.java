package tetracoveragearea.gui.panels.devicePanels;

import jssc.*;
import org.apache.log4j.Logger;
import tetracoveragearea.common.ATListener;
import tetracoveragearea.common.ATPortReader;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.primitives.SubPanel;
import tetracoveragearea.gui.panels.settingsPanels.timers.TimersPanel;

import javax.swing.*;
import java.util.*;
import java.util.Timer;

import static tetracoveragearea.gui.panels.devicePanels.SerialGuiPanel.ATRequest.GPS_AT_QUERY;
import static tetracoveragearea.gui.panels.devicePanels.SerialGuiPanel.ATRequest.RSSI_AT_QUERY;

/**
 * Панель взаимодействия с серийным портом
 *
 * Created by anatoliy on 17.02.17.
 */
public class SerialGuiPanel extends SubPanel implements ATListener {

    public static final Logger log = Logger.getLogger(SerialGuiPanel.class);

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


    private JLabel selectPortLabel = new JLabel("Последовательный порт: ");
    private JLabel portStateLabel = new JLabel();
    private JToggleButton startDataCaptureButton = new JToggleButton("Начать сбор данных");

    private Vector portBoxVector = new Vector(Arrays.asList(SerialPortList.getPortNames()));

    private JComboBox portBox = new JComboBox(portBoxVector);

    // Таймеры запроса данных
    private Timer rssiRequestTimer;
    private Timer gpsRequestTimer;

    // Таймер прослушки порта до начала сбора данных
    private Timer listenTimer;

    private SerialPort serialPort = new SerialPort("dev/ttyS0");

    // Считыватель ответа устройства на команду AT для потверждения инициализации
    private SerialPortReader serialPortReader;

    // Считыватель точек покрытия
    private ATPortReader portReader = new ATPortReader(serialPort);

    public SerialGuiPanel() {

        Vector portBoxVector = new Vector(Arrays.asList(SerialPortList.getPortNames()));

        startDataCaptureButton.setUI(GuiComponents.getToggleButtonGreenUI());
        startDataCaptureButton.addActionListener(e -> {

                    // При старте сбора данных:
                    if (startDataCaptureButton.isSelected()) {

                        stopPortMonitoring();
                        startCapturingData();
                        setInCaptureState();

                    // При останове сбора данных:
                    } else {

                        stopCapturingData();
                        startPortMonitoring();
                        setNotAvailableCaptureState();
                    }
                }
        );

        portBox.addActionListener(e -> {

                    Object selected = portBox.getSelectedItem();

                    closePortConnection();
                    serialPort = new SerialPort(selected.toString());
                    setNotAvailableCaptureState();
                }
        );

        List<JComponent> components = Arrays.asList(selectPortLabel, portBox, portStateLabel, startDataCaptureButton);


        for (JComponent component : components) {
            add(component, "w 100%, wrap");
        }
    }

    TimerTask timerTask;

    /**
     * Запускает таймер проверки доступности устройства на выбранном порту
     */
    public void startPortMonitoring() {

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

                        setAvailableCaptureState();
                    } else {
                        closePortConnection();
                        setNotAvailableCaptureState();
                    }

                } catch (SerialPortException | InterruptedException | NullPointerException ex) {
                    log.info("Порт " + portBox.getSelectedItem().toString() + " недоступен");
                    setNotAvailableCaptureState();
                }
            }
        }, 0, CHECK_PORT_TIME_REQUEST);
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
     * Обрывает соединение с портом
     */
    public void closePortConnection() {

        try {
            serialPort.closePort();
        } catch (SerialPortException | NullPointerException  ex) {}
    }

    public void setNotAvailableCaptureState() {

        portBox.setEnabled(true);
        portStateLabel.setText("Устройство не опознано");
        startDataCaptureButton.setText("Начать сбор данных");
        startDataCaptureButton.setSelected(false);
        startDataCaptureButton.setEnabled(false);
    }

    public void setAvailableCaptureState() {

        portStateLabel.setText("Устройство опознано");
        startDataCaptureButton.setEnabled(true);
    }

    public void setInCaptureState() {

        portBox.setEnabled(false);
        portStateLabel.setText("Cбор данных...");
        startDataCaptureButton.setText("Завершить");
    }

    /**
     * Запуск таймеров запросов
     *
     * @param delayRssi - интервал запросов rssi
     * @param delayGps - интервал запросов gps
     */
    public void startTimers(int delayRssi, int delayGps) {

        rssiRequestTimer = new Timer();
        gpsRequestTimer = new Timer();

        rssiRequestTimer.schedule(getATTimerTask(rssiRequestTimer, RSSI_AT_QUERY.getRequest()), 0, delayRssi);
        gpsRequestTimer.schedule(getATTimerTask(gpsRequestTimer, GPS_AT_QUERY.getRequest()), 500, delayGps);


    }

    /** Останов таймеров */
    public void stopTimers() {
        try {
            rssiRequestTimer.cancel();
            gpsRequestTimer.cancel();
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

                    serialPort.writeString(request + "\r\n");

                } catch (SerialPortException e) {
                    
                    onDeviceError();
                }
            }
        };
    }

    /**
     * Запуск работы порта в режиме сбора данных
     */
    public void startCapturingData() {
        // Заменяем слушателя проверочных ответов слушателем данных
        try {

            serialPort.removeEventListener();
            portReader.setSerialPort(serialPort);
            serialPort.addEventListener(portReader);
            portReader.addATListener(this);
            portReader.startResponseTimeoutTimer();

            // Запускаем таймеры на отправку запросов
            startTimers((int) (TimersPanel.getRssiQueryTime() * 1000), (int) (TimersPanel.getGpsQueryTime() * 1000));
            log.info("Старт сбора данных");

        } catch (SerialPortException ex) {
            log.info("Не удалось подменить слушателя");
        }
    }

    /**
     * Прекращение работы порта в режиме сбора данных
     */
    public void stopCapturingData() {

        try {
            // Прерываем таймеры, убираем слушателя данных, разрываем соединение
            stopTimers();
            portReader.stopResponseTimeoutTimer();
            closePortConnection();
            setNotAvailableCaptureState();

            serialPort.removeEventListener();
        } catch (SerialPortException e1) {}
        log.info("Останов сбора данных");
    }

    /**
     * Прекращение прослушивания портов в режиме проверки доступности устройства
     */
    public void stopPortMonitoring() {
        listenTimer.cancel();

        // Ожидание, пока не закончит выполнение таймер проверки состояния устройства
        try {
            Thread.sleep(30);
        } catch (InterruptedException ex) {ex.printStackTrace();}
        log.info("Останов мониторинга");
    }

    public void showDeviceError() {
        JOptionPane.showMessageDialog(this, "Нет ответа устройства");
    }

    public void showLostGpsError() {
        JOptionPane.showMessageDialog(this, "Нет ответа системы навигации");
    }

    public void showLostNetworkError() {
        JOptionPane.showMessageDialog(this, "Нет ответа базовой станции");

    }

    public void breakConnection() {

        stopCapturingData();
        startPortMonitoring();
        setNotAvailableCaptureState();
    }

    @Override
    public void onNetworkError() {
        breakConnection();
        showLostNetworkError();
    }

    @Override
    public void onGpsError() {
        breakConnection();
        showLostGpsError();
    }

    @Override
    public void onDeviceError() {
        breakConnection();
        showDeviceError();
    }

    @Override
    public void onInvoke() {
        startPortMonitoring();
    }

    @Override
    public void onRevoke() {
        stopPortMonitoring();
    }
}
