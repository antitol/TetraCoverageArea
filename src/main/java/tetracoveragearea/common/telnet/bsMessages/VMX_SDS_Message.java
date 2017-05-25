package tetracoveragearea.common.telnet.bsMessages;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.DigitalWithHex_Feature;
import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.HEX_Feature;
import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.String_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.CallParty_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.ESN_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.MultiFeature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.ShortData_Feature;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 18.05.17.
 */
public class VMX_SDS_Message extends MultiFeature implements BSMessage {

    CallParty_Feature calledParty = new CallParty_Feature();
    CallParty_Feature callingParty = new CallParty_Feature();
    HEX_Feature handle = new HEX_Feature();
    String_Feature accessPriority = new String_Feature();
    String_Feature areaSelection = new String_Feature();
    String_Feature trafficStealing = new String_Feature();
    String_Feature fcsFlag = new String_Feature();
    HEX_Feature reserved = new HEX_Feature();
    String_Feature ackExpected = new String_Feature();
    ShortData_Feature shortData = new ShortData_Feature();
    ESN_Feature esn = new ESN_Feature();
    DigitalWithHex_Feature sourceLA = new DigitalWithHex_Feature();
    DigitalWithHex_Feature cellNumber = new DigitalWithHex_Feature();
    String_Feature repeatableLA = new String_Feature();
    String_Feature macFacility = new String_Feature();

    public VMX_SDS_Message(List<String> strings) {
        super(strings);

        if (strings.size() != getLength()) {
            throw new BSMessageParseException(VMX_SDS_Message.class.getName());
        }

        setFeature(Arrays.asList(calledParty, callingParty, handle, accessPriority, areaSelection, trafficStealing,
                fcsFlag, reserved, ackExpected, shortData, esn, sourceLA, cellNumber, repeatableLA, macFacility));
        parse();
    }

    public CallParty_Feature getCalledParty() {
        return calledParty;
    }

    public CallParty_Feature getCallingParty() {
        return callingParty;
    }

    public HEX_Feature getHandle() {
        return handle;
    }

    public String_Feature getAccessPriority() {
        return accessPriority;
    }

    public String_Feature getAreaSelection() {
        return areaSelection;
    }

    public String_Feature getTrafficStealing() {
        return trafficStealing;
    }

    public String_Feature getFcsFlag() {
        return fcsFlag;
    }

    public HEX_Feature getReserved() {
        return reserved;
    }

    public String_Feature getAckExpected() {
        return ackExpected;
    }

    public ShortData_Feature getShortData() {
        return shortData;
    }

    public ESN_Feature getEsn() {
        return esn;
    }

    public DigitalWithHex_Feature getSourceLA() {
        return sourceLA;
    }

    public DigitalWithHex_Feature getCellNumber() {
        return cellNumber;
    }

    public String_Feature getRepeatableLA() {
        return repeatableLA;
    }

    public String_Feature getMacFacility() {
        return macFacility;
    }

    @Override
    public int getLength() {
        return 25;
    }
}
