package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.api.io.CommandInput;
import pw.stamina.mandate.api.io.CommandOutput;
import pw.stamina.mandate.api.io.IODescriptor;

/**
 * @author Foundry
 */
public final class SimpleIODescriptor implements IODescriptor {
    private final CommandInput in;
    private final CommandOutput out;
    private final CommandOutput err;

    private SimpleIODescriptor(CommandInput in, CommandOutput out, CommandOutput err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    @Override
    public CommandInput in() {
        return in;
    }

    @Override
    public CommandOutput out() {
        return out;
    }

    @Override
    public CommandOutput err() {
        return err;
    }

    public static IODescriptor from(CommandInput in, CommandOutput out, CommandOutput err) {
        return new SimpleIODescriptor(in, out, err);
    }
}
