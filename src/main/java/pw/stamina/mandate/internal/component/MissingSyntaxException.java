package pw.stamina.mandate.internal.component;

/**
 * @author Foundry
 */
public class MissingSyntaxException extends RuntimeException {

    private static final long serialVersionUID = 1054337915116756139L;

    public MissingSyntaxException() {}

    public MissingSyntaxException(String message) {
        super(message);
    }

    public MissingSyntaxException(Throwable cause) {
        super(cause);
    }

    public MissingSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
