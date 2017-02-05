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

package pw.stamina.mandate.execution.executable;

import pw.stamina.mandate.execution.CommandContext;

import java.lang.reflect.Method;

/**
 * The strategy by which a reflected command-executable {@link Method method} is converted to a {@link CommandExecutable CommandExecutable} instance
 * for future invocation. The produced CommandExecutable will eventually invoke underlying method representing by the provided Method object when it is
 * invoked.
 *
 * @author Mark Johnson
 */
public interface CommandExecutableCreationStrategy {

    /**
     * Attempts to create a new CommandExecutable instance from the provided arguments.
     *
     * @param backingMethod the backing method that the produced CommandExecutable will invoke as a command
     * @param methodParent the parent instance associated with the provided method
     * @param commandContext the command context associated with this creation attempt
     * @return a new CommandExecutable instance integrating the provided method in its execution
     */
    CommandExecutable newExecutable(Method backingMethod, Object methodParent, CommandContext commandContext);
}
