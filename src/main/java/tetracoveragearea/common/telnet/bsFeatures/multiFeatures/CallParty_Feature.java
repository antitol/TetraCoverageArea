package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.DigitalWithHex_Feature;
import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.String_Feature;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class CallParty_Feature extends MultiFeature {

    String_Feature typeIdentifier = new String_Feature();
    DigitalWithHex_Feature ssi = new DigitalWithHex_Feature();

    public CallParty_Feature() {
        setFeature(Arrays.asList(typeIdentifier, ssi));
    }

    @Override
    public int getLength() {
        return 3;
    }

    public String_Feature getTypeIdentifier() {
        return typeIdentifier;
    }

    public DigitalWithHex_Feature getSsi() {
        return ssi;
    }
}
