package pw.stamina.mandate.internal.execution.parameter;

import pw.stamina.mandate.api.execution.CommandParameter;

import java.lang.reflect.Parameter;

/**
 * @author Foundry
 */
public final class CommandParameterFactory {
    private CommandParameterFactory() {}

    public static CommandParameter newParameter(Parameter parameter, Class type) {
        return new DeclaredCommandParameter(parameter, type);
    }
}
