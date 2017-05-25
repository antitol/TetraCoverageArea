package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.Digital_Feature;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class SID_Feature extends MultiFeature {

    Digital_Feature CID = new Digital_Feature();
    Digital_Feature LEP = new Digital_Feature();

    public SID_Feature() {
        setFeature(Arrays.asList(CID, LEP));
    }

    @Override
    public int getLength() {
        return 3;
    }

    public Digital_Feature getCID() {
        return CID;
    }

    public Digital_Feature getLEP() {
        return LEP;
    }
}
