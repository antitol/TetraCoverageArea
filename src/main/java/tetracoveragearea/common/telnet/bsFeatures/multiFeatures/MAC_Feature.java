package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;


import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.Digital_Feature;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class MAC_Feature extends MultiFeature {

    Digital_Feature endpointId = new Digital_Feature();
    Digital_Feature rssi = new Digital_Feature();
    Digital_Feature pathDelay = new Digital_Feature();
    Digital_Feature rxMultiframeNum = new Digital_Feature();
    Digital_Feature rxFrameNum = new Digital_Feature();

    public MAC_Feature() {
        setFeature(Arrays.asList(endpointId, rssi, pathDelay, rxMultiframeNum, rxFrameNum));
    }

    @Override
    public int getLength() {
        return 6;
    }

    public Digital_Feature getEndpointId() {
        return endpointId;
    }

    public Digital_Feature getRssi() {
        return rssi;
    }

    public Digital_Feature getPathDelay() {
        return pathDelay;
    }

    public Digital_Feature getRxMultiframeNum() {
        return rxMultiframeNum;
    }

    public Digital_Feature getRxFrameNum() {
        return rxFrameNum;
    }
}
