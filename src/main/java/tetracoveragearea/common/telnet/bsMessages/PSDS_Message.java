package tetracoveragearea.common.telnet.bsMessages;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.String_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 18.05.17.
 */
public class PSDS_Message extends MultiFeature {

    SID_Feature sid = new SID_Feature();
    String_Feature fcsFlag = new String_Feature();
    String_Feature encryptionFlag = new String_Feature();
    MAC_Feature mac = new MAC_Feature();
    String_Feature areaSelection = new String_Feature();
    CallParty_Feature calledParty = new CallParty_Feature();
    ShortData_Feature shortData = new ShortData_Feature();

    private int SSI;

    public PSDS_Message(List<String> strings) throws BSMessageParseException {
        super(strings);
        setFeature(Arrays.asList(sid, fcsFlag, encryptionFlag, mac, areaSelection, calledParty, shortData));

        if (strings.size() != getLength()) {
            throw new BSMessageParseException(PSDS_Message.class.getName());
        }

        try {
            SSI = Integer.parseInt(strings.get(0).split(",")[1].substring(5));
        } catch (Exception ex) {
            throw new BSMessageParseException();
        }
        parse();
    }

    public SID_Feature getSid() {
        return sid;
    }

    public String_Feature getFcsFlag() {
        return fcsFlag;
    }

    public String_Feature getEncryptionFlag() {
        return encryptionFlag;
    }

    public MAC_Feature getMac() {
        return mac;
    }

    public String_Feature getAreaSelection() {
        return areaSelection;
    }

    public CallParty_Feature getCalledParty() {
        return calledParty;
    }

    public ShortData_Feature getShortData() {
        return shortData;
    }

    public int getSSI() {
        return SSI;
    }

    @Override
    public int getLength() {
        return 20;
    }
}
