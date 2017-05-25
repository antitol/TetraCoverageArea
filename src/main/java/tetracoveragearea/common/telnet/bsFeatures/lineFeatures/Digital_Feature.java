package tetracoveragearea.common.telnet.bsFeatures.lineFeatures;

/**
 * Created by anatoliy on 18.05.17.
 */
public class Digital_Feature extends BSFeature<Integer> {

    @Override
    public void parse(String string) {
        try {
            setFeature(Integer.parseInt(getParsingPart(string)));
        } catch (NumberFormatException ex) {
            setFeature(0);
        }
    }
}
