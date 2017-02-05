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

package pw.stamina.mandate.internal.execution.executable;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.parsing.ArgumentReificationException;
import pw.stamina.mandate.parsing.argument.CommandArgument;
import pw.stamina.mandate.security.CommandSender;
import pw.stamina.mandate.security.Permission;

import java.util.Deque;
import java.util.List;

/**
 * @author Mark Johnson
 */
public class PermissionValidatingExecutableWrapper implements CommandExecutable {

    private final CommandExecutable backingExecutable;

    private final Permission requiredPermission;

    public PermissionValidatingExecutableWrapper(final CommandExecutable backingExecutable, final Permission requiredPermission) {
        this.backingExecutable = backingExecutable;
        this.requiredPermission = requiredPermission;;
    }

    @Override
    public Execution execute(final Deque<CommandArgument> arguments, final ExecutionContext executionContext) throws ArgumentReificationException {
        if (canExecute(executionContext.getCommandSender())) {
            return backingExecutable.execute(arguments, executionContext);
        } else {
            executionContext.getIODescriptor().err().write(String.format("You do not have the required permission '%s' to execute this command", requiredPermission.getRawName()));
            return Execution.complete(ExitCode.INVALID);
        }
    }

    @Override
    public boolean canExecute(CommandSender commandSender) {
        return commandSender.hasPermission(requiredPermission);
    }

    @Override
    public List<CommandParameter> getParameters() {
        return backingExecutable.getParameters();
    }

    @Override
    public String getDescription() {
        return backingExecutable.getDescription();
    }

    @Override
    public int minimumArguments() {
        return backingExecutable.minimumArguments();
    }

    @Override
    public int maximumArguments() {
        return backingExecutable.maximumArguments();
    }
}

