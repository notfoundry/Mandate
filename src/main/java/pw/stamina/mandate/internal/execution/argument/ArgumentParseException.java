package pw.stamina.mandate.internal.execution.argument;

import pw.stamina.parsor.exceptions.ParseException;

/**
 * @author Foundry
 */
public class ArgumentParseException extends ParseException {

    private static final long serialVersionUID = 651940454649205892L;

    private final String input;

    public ArgumentParseException(String input, Class<?> parserType, String message) {
        super(null, parserType, message);
        this.input = input;
    }

    public ArgumentParseException(String input, Class<?> parserType, String message, Throwable cause) {
        super(null, parserType, message, cause);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
