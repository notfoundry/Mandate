package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.api.io.CommandOutput;

/**
 * @author Foundry
 */
public enum StandardOutputStream implements CommandOutput {
    INSTANCE;

    @Override
    public void write(Object o) {
        System.out.println(o);
    }

    public static StandardOutputStream get() {
        return INSTANCE;
    }
}
