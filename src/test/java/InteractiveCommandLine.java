import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Foundry
 */
public class InteractiveCommandLine {
    public static void main(String[] args) {
        final CommandManager commandManager = new AnnotatedCommandManager();
        commandManager.register(new InteractiveCommandLine());
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            ExitCode exitCode = commandManager.execute(scanner.nextLine());
            System.out.println("Result code: " + exitCode);
        }
    }

    @Executes
    @Syntax(syntax = "accept")
    public ExitCode acceptUserInput(IODescriptor io) throws IOException {
        io.out().write("Please enter something:");
        String input = io.in().read();
        io.out().write(input);
        return ExitCode.SUCCESS;
    }
}
