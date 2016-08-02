/*
 * Causam - A maximally decoupled event system for Java
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
import pw.stamina.mandate.internal.execution.executable.transformer.InvokerProxy;

import java.util.concurrent.*;

/**
 * @author Foundry
 */
public class SynchronousTransformerExecution implements Execution {
    private final ExitCode exitCode;

    public SynchronousTransformerExecution(InvokerProxy invoker, IODescriptor io, Object[] args) {
        ExitCode exitCode;
        try {
            exitCode = invoker.execute(io, args);
        } catch (Exception e) {
            io.err().write(String.format("Exception while executing method: %s", e));
            exitCode = ExitCode.TERMINATED;
        }
        this.exitCode = exitCode;
    }

    @Override
    public ExitCode result() {
        return exitCode;
    }

    @Override
    public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
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
