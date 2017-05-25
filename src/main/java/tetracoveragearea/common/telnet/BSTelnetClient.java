package tetracoveragearea.common.telnet;


import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.MultiFeature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.ShortData_Feature;
import tetracoveragearea.common.telnet.bsMessages.BSMessageParseException;
import tetracoveragearea.common.telnet.bsMessages.PSDS_Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anatoliy on 18.05.17.
 */
public class BSTelnetClient extends TelnetClient {

    public static final Logger log = Logger.getLogger(BSTelnetClient.class);

    public enum PortType {
        Monitior(5003),
        Service(5000);

        private int port;

        PortType(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }
    }

    private int connectionTimeout = 200000;

    private String hostname;
    private PortType portType;
    private boolean connected;
    private BufferedReader bufferedInputReader;

    private BStation sourceLA;

    public BSTelnetClient() {
    }

    public BSTelnetClient(BStation bStation, PortType portType) {
        this.hostname = bStation.getAddress();
        this.portType = portType;
        this.sourceLA = bStation;
    }

    public boolean connect() {
        try {
            connect(hostname, portType.port);

            connected = true;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    connected = false;
                }
            }, connectionTimeout);

            while (connected) {
                char c = (char) getInputStream().read();
                if (c == '>') {

                    log.info("Клиент " + hostname + ":" + portType.getPort() + " успешно подключен");
                    return true;
                }
            }

            log.info("Истек таймаут подключения для " + hostname + ":" + portType.getPort());
            return false;

        } catch (IOException ex) {
            log.info("Ошибка подключения к " + hostname + ":" + portType.getPort());
            return false;
        }
    }

    public void setMonitoring(boolean enable) {
        send("u s3\n");
        send("tfrm sap s " + (enable ? "1" : "0") + " sds\n");
        log.info(hostname + ": мониторинг " + (enable ? "включен" : "выключен"));
    }

    public void send(String s) {
        try {
            getOutputStream().write(s.getBytes());
            getOutputStream().flush();
            Thread.sleep(500);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void monitoringStart() {

        Thread monitoringThread = new Thread() {
            @Override
            public void run() {
                bufferedInputReader = new BufferedReader(new InputStreamReader(getInputStream()));
                while (isConnected()) {
                    try {

                        MultiFeature message;
                        List<String> sentences = new ArrayList<>();
                        String s;
                        do {
                            s = bufferedInputReader.readLine();
                            if (s == null) {
                                break;
                            } else if (s.length() > 0){
                                sentences.add(s);
                            }
                        } while (s.length() > 0);

                        try {
                            if (sentences.size() > 0) {
                                switch (sentences.get(0).split("\\s")[1]) {
                                    case "VMX_SDS_P":
                                        break;
                                    case "TNSDS_U":
                                        break;
                                    case "PSDS_U":

                                        PSDS_Message psds_message = new PSDS_Message(sentences);
                                        ShortData_Feature shortData = psds_message.getShortData();
                                        double latitude = shortData.getLatitude();
                                        double longitude = shortData.getLongitude();
                                        double rssi = psds_message.getMac().getRssi().getFeature();
                                        int ssi = psds_message.getSSI();
                                        if (latitude > 0 && longitude > 0) {
                                            log.info(hostname + ": " + " абонент " +
                                                    psds_message.getSSI() + ": широта " + latitude + " долгота " + longitude + " rssi: " + rssi);
                                            GeometryStore.getInstance().addPoint(new Point(latitude, longitude, rssi, LocalDateTime.now(), ssi, sourceLA));
                                        }
                                        break;
                                    default:
                                        log.info(hostname + ": сообщение не распознано");
                                        continue;
                                }
                            }
                        } catch (BSMessageParseException ex) {
                            log.info(hostname + " ошибка декодирования сообщения: " + ex.getMessage());
                            continue;
                        }



                    } catch (IOException ex) {
                        break;
                    }
                }
            }
        };

        monitoringThread.start();

    }






}
