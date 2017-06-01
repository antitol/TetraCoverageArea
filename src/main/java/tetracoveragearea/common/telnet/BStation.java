package tetracoveragearea.common.telnet;

/**
 * Created by anatoliy on 23.05.17.
 */
public enum BStation {
    BS_SEV1(1, "10.22.101.1", "ГУИС #1"),
    BS_SEV2(2, "10.22.101.33", "ГУИС #2"),
    BS_SEV3(3, "10.22.101.65", "ГУИС #3"),
    BS_SEV4(4, "10.22.101.97", "ГУИС #4"),
    BS_OMS1(100, "10.3.101.1", "ОНИИП #1"),
    NULL(0, "", "Не определено");

    private int id;
    private String address;
    private String description;

    BStation(int id, String address, String description) {
        this.id = id;
        this.address = address;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public static BStation getByAddress(String address) {
        switch (address) {
            case "10.22.101.1": return BS_SEV1;
            case "10.22.101.33": return BS_SEV2;
            case "10.22.101.65": return BS_SEV3;
            case "10.22.101.97": return BS_SEV4;
            case "10.3.101.1": return BS_OMS1;
            default: return null;
        }
    }

    public static BStation getById(int id) {
        switch (id) {
            case 1: return BS_SEV1;
            case 2: return BS_SEV2;
            case 3: return BS_SEV3;
            case 4: return BS_SEV4;
            case 100: return BS_OMS1;
            default: return null;
        }
    }


    @Override
    public String toString() {
        return description;
    }
}
