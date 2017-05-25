package tetracoveragearea.common.telnet;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by anatoliy on 18.05.17.
 */
public class BSTelnetManager {

    public static final Logger log = Logger.getLogger(BSTelnetManager.class);

    BSTelnetClient service;
    BSTelnetClient monitor;

    private String hostname;


    public BSTelnetManager(BStation bStation) {
        this.hostname = hostname;
        service = new BSTelnetClient(bStation, BSTelnetClient.PortType.Service);
        monitor = new BSTelnetClient(bStation, BSTelnetClient.PortType.Monitior);
    }

    public boolean startMoniting() {
        if (service.connect()) {
            log.info(hostname + ": начало мониторинга");
            service.setMonitoring(true);

            try {
                service.disconnect();
                System.out.println(hostname + ": отключение от сервисного порта");
            } catch (IOException ex) {
                System.out.println(hostname + ": отключение от сервисного порта");
            }

            if (monitor.connect()) {
                monitor.monitoringStart();
                return true;
            }
        }

        return false;
    }

    public boolean stopMonitoring() {
        try {
            monitor.disconnect();
        } catch (IOException ex) {}

        if (service.connect()) {
            service.setMonitoring(false);

            try {
                service.disconnect();
            } catch (IOException ex) {
                log.info(hostname + ": мониторинг остановлен");
                return true;
            }
        }

        return false;
    }
}
