package pw.stamina.mandate.internal.component;

/**
 * @author Foundry
 */
class MissingSyntaxException extends RuntimeException {

    private static final long serialVersionUID = 1054337915116756139L;

    MissingSyntaxException() {}

    MissingSyntaxException(String message) {
        super(message);
    }

    MissingSyntaxException(Throwable cause) {
        super(cause);
    }

    MissingSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    MissingSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
