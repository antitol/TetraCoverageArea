package tetracoveragearea.common.telnet.bsFeatures.lineFeatures;

import tetracoveragearea.common.telnet.bsMessages.BSMessageParseException;

/**
 * Created by anatoliy on 18.05.17.
 */
public abstract class BSFeature<T> {

    private String message;
    private T feature;
    public int length;

    public BSFeature() {
    }

    public BSFeature(String message) {
        this.message = message;
    }

    public void setFeature(T feature) {
        this.feature = feature;
    }

    public T getFeature() {
        return feature;
    };

    public String getParsingPart(String s) {
        return s.split(" \\:")[1].trim();
    }

    public void parse(String string) throws BSMessageParseException {};

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLength() {
        return 1;
    };
}
