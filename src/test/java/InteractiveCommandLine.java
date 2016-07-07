import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Foundry
 */
public class InteractiveCommandLine {
    public static void main(String[] args) {
        final CommandManager commandManager = new UnixCommandManager();
        commandManager.register(new InteractiveCommandLine());
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            Execution execution = commandManager.execute(scanner.nextLine());
            System.out.println("Result code: " + execution.result());
        }
    }

    @Executes
    @Syntax(syntax = "accept")
    public ExitCode acceptUserInput(IODescriptor io) throws NoSuchElementException {
        io.out().write("Please enter something:");
        String input = io.in().read();
        io.out().write(input);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    public ExitCode sum(IODescriptor io, int augend, int addend) {
        io.out().write(augend + addend);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(syntax = {"execute", "exec"})
    public ExitCode execute(IODescriptor io) {
        return ExitCode.SUCCESS;
    }
}
