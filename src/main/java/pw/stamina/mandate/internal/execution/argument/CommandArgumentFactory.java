package pw.stamina.mandate.internal.execution.argument;

import pw.stamina.mandate.api.execution.argument.CommandArgument;

/**
 * @author Foundry
 */
public final class CommandArgumentFactory {
    private CommandArgumentFactory() {}

    public static CommandArgument newArgument(String argument) {
        return new BaseCommandArgument(argument);
    }
}
