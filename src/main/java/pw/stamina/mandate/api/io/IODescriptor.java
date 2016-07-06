package pw.stamina.mandate.api.io;

/**
 * @author Foundry
 */
public final class IODescriptor {
    private final CommandInput in;
    private final CommandOutput out;
    private final CommandOutput err;

    private IODescriptor(CommandInput in, CommandOutput out, CommandOutput err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    public CommandInput in() {
        return in;
    }

    public CommandOutput out() {
        return out;
    }

    public CommandOutput err() {
        return err;
    }

    public static IODescriptor from(CommandInput in, CommandOutput out, CommandOutput err) {
        return new IODescriptor(in, out, err);
    }
}
