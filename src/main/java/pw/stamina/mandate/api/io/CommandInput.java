package pw.stamina.mandate.api.io;

import java.io.IOException;

/**
 * A stream of input from which a running command can read if supplementary input is necessary
 *
 * @author Foundry
 */
@FunctionalInterface
public interface CommandInput {

    /**
     * Finds and returns the next complete token from this input stream
     * <p>
     * This method may block while waiting for input
     *
     * @return the next complete token from this input stream
     * @throws IOException
     */
    String read() throws IOException;
}
