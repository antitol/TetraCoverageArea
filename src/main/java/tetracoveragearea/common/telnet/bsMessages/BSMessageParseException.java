package tetracoveragearea.common.telnet.bsMessages;

/**
 * Created by anatoliy on 22.05.17.
 */
public class BSMessageParseException extends IllegalStateException {

    private static final long serialVersionUID = 234122696006267687L;
    public BSMessageParseException() {
        super();
    }

    public BSMessageParseException(String s) {
        super(s);
    }
}
