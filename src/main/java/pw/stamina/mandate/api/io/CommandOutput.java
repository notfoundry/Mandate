package pw.stamina.mandate.api.io;

/**
 * A stream of output to which a running command can write
 * This is generally used to allow commands to print messages as they run, giving them an incredibly
 * uncomplicated interface for doing so through the {@link CommandOutput#write(Object) write} method
 *
 * @author Foundry
 */
@FunctionalInterface
public interface CommandOutput {

    /**
     * Submits a string to the output stream represented by this instance
     * <p>
     * The usage of this object is up to the implementation to decide, depending on its intended use
     *
     * @param o the object to be written to the stream
     */
    void write(Object o);
}
