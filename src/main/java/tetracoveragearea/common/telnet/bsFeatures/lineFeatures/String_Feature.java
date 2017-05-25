package tetracoveragearea.common.telnet.bsFeatures.lineFeatures;

/**
 * Created by anatoliy on 18.05.17.
 */
public class String_Feature extends BSFeature<String> {

    @Override
    public void parse(String string) {
        setFeature(getParsingPart(string));
    }
}
