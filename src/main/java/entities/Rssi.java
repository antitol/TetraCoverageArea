package entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by anatoliy on 19.01.17.
 */
public class Rssi {

    private int rssi;
    private final Date date;

    public Rssi(Optional rssi) {
        this.rssi = (int) rssi.get();
        date = new Date();
    }

    public int getRssi() {
        return rssi;
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
    }
}
