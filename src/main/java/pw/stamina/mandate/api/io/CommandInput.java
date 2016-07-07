package pw.stamina.mandate.api.io;

import java.util.NoSuchElementException;

/**
 * @author Foundry
 */
@FunctionalInterface
public interface CommandInput {
    String read() throws NoSuchElementException;
}
