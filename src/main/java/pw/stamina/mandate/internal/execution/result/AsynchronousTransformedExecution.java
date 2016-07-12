/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Foundry
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

import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.execution.executable.transformer.TransformationTarget;

import java.util.concurrent.*;

/**
 * @author Foundry
 */
public class AsynchronousTransformedExecution implements Execution {
    private static final ExecutorService COMMAND_EXECUTOR = Executors.newCachedThreadPool();

    private final TransformationTarget executable;

    private final IODescriptor io;

    private final Future<ExitCode> pendingComputation;

    public AsynchronousTransformedExecution(TransformationTarget executable, IODescriptor io, Object[] args) {
        this.executable = executable;
        this.io = (IODescriptor) args[0];
        pendingComputation = COMMAND_EXECUTOR.submit(() -> executable.execute(io, args));
    }

    @Override
    public ExitCode result() {
        try {
            return pendingComputation.get();
        } catch (Exception e) {
            io.err().write(String.format("Exception while executing command: %s", e));
            return ExitCode.TERMINATED;
        }
    }

    @Override
    public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
        try {
            return pendingComputation.get(timeout, unit);
        } catch (TimeoutException e) {
            throw e;
        } catch (Exception e) {
            io.err().write(String.format("Exception while executing command: %s", e));
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
