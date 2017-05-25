package tetracoveragearea.common.telnet.bsFeatures.lineFeatures;

/**
 * Created by anatoliy on 20.05.17.
 */
public class DigitalWithHex_Feature extends Digital_Feature {

    @Override
    public void parse(String string) {
        try {
            setFeature(Integer.parseInt(getParsingPart(string).split("\\s")[0]));
        } catch (Exception ex) {
            setFeature(0);
        }
    }
}
