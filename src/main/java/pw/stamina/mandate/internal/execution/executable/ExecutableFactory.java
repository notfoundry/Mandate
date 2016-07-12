package pw.stamina.mandate.internal.execution.executable;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandExecutable;

import java.lang.reflect.Method;

/**
 * @author Foundry
 */
public final class ExecutableFactory {
    private ExecutableFactory() {}

    public static CommandExecutable newExecutable(Method backingMethod, Object methodParent, CommandManager commandManager) {
        return new MethodExecutable(backingMethod, methodParent, commandManager);
    }
}
