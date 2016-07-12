package pw.stamina.mandate.api.io;

/**
 * @author Foundry
 */
public interface IODescriptor {
    CommandInput in();

    CommandOutput out();

    CommandOutput err();
}
