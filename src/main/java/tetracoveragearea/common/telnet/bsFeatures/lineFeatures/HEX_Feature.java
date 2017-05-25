package tetracoveragearea.common.telnet.bsFeatures.lineFeatures;

import tetracoveragearea.common.telnet.bsMessages.BSMessageParseException;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by anatoliy on 18.05.17.
 */
public class HEX_Feature extends BSFeature<byte[]> {

    @Override
    public void parse(String string) {
        try {
            String part = getParsingPart(string).split("\\:")[0];
            if (part.length() % 2 == 1) part += "0";
            setFeature(DatatypeConverter.parseHexBinary(part));
        } catch (Exception ex) {
            throw new BSMessageParseException();
        }
    }
}
