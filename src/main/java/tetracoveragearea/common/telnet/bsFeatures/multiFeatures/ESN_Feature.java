package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;


import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.Digital_Feature;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class ESN_Feature extends MultiFeature {

    Digital_Feature lengthIndicator = new Digital_Feature();
    Digital_Feature digits = new Digital_Feature();

    public ESN_Feature() {
        setFeature(Arrays.asList(lengthIndicator, digits));
    }

    @Override
    public int getLength() {
        return 3;
    }
}
