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

package pw.stamina.mandate.execution;

import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.argument.ArgumentProvider;
import pw.stamina.mandate.execution.argument.ArgumentHandlerRegistry;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.io.IOEnvironment;
import pw.stamina.mandate.syntax.CommandRegistry;

/**
 * A central manager and executor for a collection of registered commands
 * <p>
 * A CommandContext is used to coordinate much of the process of using Mandate, including providing a
 * central hub for registering commands, handling the parsing and eventual execution of user input, and
 * allowing external components to interact with the command ecosystem
 *
 * @author Mark Johnson
 */
public interface CommandContext {

    /**
     * Registers all correctly defined commands in the specified container object
     * <p>
     * A command method is considered to be correctly defined if either of these requirements are met:
     *  <li>It is flagged as an {@link pw.stamina.mandate.annotations.Executes executor} and has its own specific {@link Syntax Syntax} annotation</li>
     *  <li>It is flagged as an {@link pw.stamina.mandate.annotations.Executes executor} and its containing class has a global {@link Syntax Syntax} annotation</li>
     *  <p>
     *  If duplicate commands exist in a registered container, a CommandContext implementation should take steps to prevent those commands from being re-added to the
     *  command registry
     *
     * @param container the object containing the commands to be registered
     * @return {@code true} if any valid commands were registered, else {@code false}
     */
    boolean register(Object container);

    /**
     * Tries to execute a command based on the provided input
     * <p>
     * If the execution fails as a result of invalid or malformed user input, the result of this
     * will immediately have a present exit code of {@link pw.stamina.mandate.execution.result.ExitCode#INVALID INVALID}. Otherwise,
     * this will return an {@link Execution execution} corresponding to the evaluated command running possibly asynchronously
     *
     * @param input the input to be tokenized and parsed as a command and its arguments
     * @return a running execution if the invocation completed without errors, else a completed execution with an exit code of INVALID
     */
    Execution execute(String input);

    /**
     * Tries to execute a command based on the provided input, using the provided IODescriptor for the execution
     * <p>
     * If the execution fails as a result of invalid or malformed user input, the result of this
     * will immediately have a present exit code of {@link pw.stamina.mandate.execution.result.ExitCode#INVALID INVALID}. Otherwise,
     * this will return an {@link Execution execution} corresponding to the evaluated command running possibly asynchronously
     *
     * @param input the input to be tokenized and parsed as a command and its arguments
     * @return a running execution if the invocation completed without errors, else a completed execution with an exit code of INVALID
     */
    Execution execute(String input, IODescriptor descriptor);

    IOEnvironment getIOEnvironment();

    CommandConfiguration getCommandConfiguration();

    ArgumentHandlerRegistry getArgumentHandlers();

    CommandRegistry getRegisteredCommands();

    ArgumentProvider getValueProviders();
}
