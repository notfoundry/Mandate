package pw.stamina.mandate.api.io;

/**
 * @author Foundry
 */
@FunctionalInterface
public interface CommandOutput {
    void write(Object o);
}
