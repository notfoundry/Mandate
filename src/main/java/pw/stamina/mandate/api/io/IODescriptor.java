package pw.stamina.mandate.api.io;

/**
 * A container for the various IO streams available to a running command
 * <p>
 * An IODescriptor provides painless methods for accessing the input, output, and error
 * streams associated with a running command.
 * <p>
 * This is used to give commands a standardized way to interact with
 * their running environment without having to resort to calling third-party code to handle input or output
 *
 * @author Foundry
 */
public interface IODescriptor {

    /**
     * @return the {@link CommandInput CommandInput} instance to be used by a running command for accepting user input
     */
    CommandInput in();

    /**
     * @return the {@link CommandOutput CommandOutput} instance to be used by a running command for printing standard output
     */
    CommandOutput out();

    /**
     * @return the {@link CommandOutput CommandOutput} instance to be used by a running command for printing error output
     */
    CommandOutput err();
}
