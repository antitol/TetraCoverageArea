package tetracoveragearea.common.telnet.bsMessages;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.Digital_Feature;
import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.String_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.CallParty_Feature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.MultiFeature;
import tetracoveragearea.common.telnet.bsFeatures.multiFeatures.ShortData_Feature;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 18.05.17.
 */
public class TNSDS_Message extends MultiFeature {

    String_Feature areaSelection = new String_Feature();
    String_Feature fcsFlag = new String_Feature();
    CallParty_Feature calledParty = new CallParty_Feature();
    ShortData_Feature shortData = new ShortData_Feature();
    Digital_Feature sourceLA = new Digital_Feature();
    Digital_Feature cellNumber = new Digital_Feature();

    public TNSDS_Message(List<String> strings) throws BSMessageParseException {
        super(strings);
        if (strings.size() != getLength()) {
            throw new BSMessageParseException(TNSDS_Message.class.getName());
        } else {
            setFeature(Arrays.asList(areaSelection, fcsFlag, calledParty, shortData, sourceLA, cellNumber));
            parse();
        }
    }

    public String_Feature getAreaSelection() {
        return areaSelection;
    }

    public String_Feature getFcsFlag() {
        return fcsFlag;
    }

    public CallParty_Feature getCalledParty() {
        return calledParty;
    }

    public ShortData_Feature getShortData() {
        return shortData;
    }

    public Digital_Feature getSourceLA() {
        return sourceLA;
    }

    public Digital_Feature getCellNumber() {
        return cellNumber;
    }

    @Override
    public int getLength() {
        return 12;
    }
}
