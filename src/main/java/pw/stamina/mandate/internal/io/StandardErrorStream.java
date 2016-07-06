package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.api.io.CommandOutput;

/**
 * @author Foundry
 */
public enum StandardErrorStream implements CommandOutput {
    INSTANCE;

    @Override
    public void write(Object o) {
        System.err.println(o);
    }

    public static StandardErrorStream get() {
        return INSTANCE;
    }
}
