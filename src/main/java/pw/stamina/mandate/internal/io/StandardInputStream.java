package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.api.io.CommandInput;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Foundry
 */
public enum StandardInputStream implements CommandInput {
    INSTANCE;

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String read() throws IOException {
        return scanner.nextLine();
    }

    public static StandardInputStream get() {
        return INSTANCE;
    }
}
