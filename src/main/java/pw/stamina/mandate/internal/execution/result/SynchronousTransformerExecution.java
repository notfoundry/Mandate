/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Mark Johnson
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Mark Johnson
 */
public class SynchronousTransformerExecution implements Execution {
    private final ExitCode exitCode;

    public SynchronousTransformerExecution(final CommandInvoker invoker, final ExecutionContext executionContext, final Object[] args) {
        ExitCode exitCode;
        try {
            exitCode = invoker.invoke(args);
        } catch (final Exception e) {
            executionContext.getIODescriptor().err().write(String.format("Exception while executing command: %s", e));
            exitCode = ExitCode.TERMINATED;
        }
        this.exitCode = exitCode;
    }

    @Override
    public ExitCode result() {
        return exitCode;
    }

    @Override
    public ExitCode result(final long timeout, final TimeUnit unit) throws TimeoutException {
        return exitCode;
    }

    @Override
    public boolean kill() {
        return false;
    }

    @Override
    public boolean completed() {
        return true;
    }
}
