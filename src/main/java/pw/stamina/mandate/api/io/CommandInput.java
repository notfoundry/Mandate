package pw.stamina.mandate.api.io;

import java.io.IOException;

/**
 * @author Foundry
 */
@FunctionalInterface
public interface CommandInput {
    String read() throws IOException;
}
