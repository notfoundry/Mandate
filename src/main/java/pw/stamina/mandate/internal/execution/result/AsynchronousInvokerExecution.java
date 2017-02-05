/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2017 Mark Johnson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pw.stamina.mandate.internal.execution.result;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.execution.executable.invoker.CommandInvoker;

import java.util.concurrent.*;

/**
 * @author Mark Johnson
 */
public class AsynchronousInvokerExecution implements Execution {
    private static final ExecutorService COMMAND_EXECUTOR = Executors.newCachedThreadPool();

    private final ExecutionContext executionContext;

    private final Future<ExitCode> pendingComputation;

    public AsynchronousInvokerExecution(final CommandInvoker invoker, final ExecutionContext executionContext, final Object[] args) {
        pendingComputation = COMMAND_EXECUTOR.submit(() -> invoker.invoke(args));
        this.executionContext = executionContext;
    }

    @Override
    public ExitCode result() {
        try {
            return pendingComputation.get();
        } catch (final Exception e) {
            executionContext.getIODescriptor().err().write(String.format("Exception while executing command: %s", e));
            return ExitCode.TERMINATED;
        }
    }

    @Override
    public ExitCode result(final long timeout, final TimeUnit unit) throws TimeoutException {
        try {
            return pendingComputation.get(timeout, unit);
        } catch (final TimeoutException e) {
            throw e;
        } catch (final Exception e) {
            executionContext.getIODescriptor().err().write(String.format("Exception while executing command: %s", e));
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
