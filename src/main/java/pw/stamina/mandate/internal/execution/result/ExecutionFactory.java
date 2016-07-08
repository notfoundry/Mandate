package pw.stamina.mandate.internal.execution.result;

import pw.stamina.mandate.api.execution.result.Execution;

import java.lang.reflect.Method;

/**
 * @author Foundry
 */
public final class ExecutionFactory {
    private ExecutionFactory() {}

    public static Execution makeExecution(Method executable, Object parent, Object[] args) {
        return new AsynchronousExecution(executable, parent, args);
    }
}
