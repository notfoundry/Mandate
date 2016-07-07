package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * @author Foundry
 */
class AsynchronousExecution implements Execution {
    private static final ExecutorService COMMAND_EXECUTOR = Executors.newCachedThreadPool();

    private final Method executable;

    private final IODescriptor io;

    private final Future<ExitCode> pendingComputation;

    AsynchronousExecution(IODescriptor io, Method executable, Object parent, Object... args) {
        this.executable = executable;
        this.io = io;
        pendingComputation = COMMAND_EXECUTOR.submit(() -> (ExitCode) executable.invoke(parent, args));
    }

    @Override
    public ExitCode result() {
        try {
            return pendingComputation.get();
        } catch (Exception e) {
            io.err().write(String.format("Exception while executing method '%s': %s", executable.getName(), e));
            return ExitCode.TERMINATED;
        }
    }

    @Override
    public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
        try {
            return pendingComputation.get(timeout, unit);
        } catch (Exception e) {
            io.err().write(String.format("Exception while executing method '%s': %s", executable.getName(), e));
            return ExitCode.TERMINATED;
        }
    }

    @Override
    public boolean kill() {
        return pendingComputation.cancel(true);
    }

    @Override
    public boolean completed() {
        return pendingComputation.isDone();
    }
}
