package tetracoveragearea.common.telnet;

import java.io.IOException;

/**
 * Created by anatoliy on 18.05.17.
 */
public class BSTelnetManager {

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
            System.out.println("ServiceConnect");
            service.setMonitoring(true);

            try {
                service.disconnect();
                System.out.println("Service disconnect");
            } catch (IOException ex) {
                System.out.println("Service disconnect");
            }

            if (monitor.connect()) {
                System.out.println("Monitor connected");
                monitor.monitoringStart();
                return true;
            }
        }

        return false;
    }

    public boolean stopMonitoring() {
        try {
            monitor.disconnect();
            System.out.println("Monitor disconnect");
        } catch (IOException ex) {}

        if (service.connect()) {
            System.out.println("ServiceConnect");
            service.setMonitoring(false);

            try {
                service.disconnect();
            } catch (IOException ex) {
                System.out.println("Service disconnect");
                return true;
            }
        }

        return false;
    }
}
