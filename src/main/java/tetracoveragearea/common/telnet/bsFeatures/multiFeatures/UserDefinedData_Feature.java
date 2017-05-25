package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;

import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.Digital_Feature;
import tetracoveragearea.common.telnet.bsFeatures.lineFeatures.HEX_Feature;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class UserDefinedData_Feature extends MultiFeature {

    Digital_Feature byteLength = new Digital_Feature();
    HEX_Feature stream = new HEX_Feature();

    public UserDefinedData_Feature() {
        setFeature(Arrays.asList(byteLength, stream));
    }

    @Override
    public int getLength() {
        return 3;
    }

    public HEX_Feature getStream() {
        return stream;
    }

    public Digital_Feature getByteLength() {
        return byteLength;
    }
}
