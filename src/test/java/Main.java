import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.CommandResult;
import pw.stamina.mandate.internal.AnnotatedCommandManager;

import java.util.Optional;
import java.util.Scanner;

/**
 * @author Foundry
 */
public class Main {
    private static final CommandManager commandManager = new AnnotatedCommandManager();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        commandManager.register(new TestCommandBody());

        while (scanner.hasNext()) {
            Optional<CommandResult> result = commandManager.execute(scanner.nextLine());
            if (result.isPresent()) {
                System.out.println(result.get().getResult());
            }
        }
    }
}
