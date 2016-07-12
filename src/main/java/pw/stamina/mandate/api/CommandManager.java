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

package pw.stamina.mandate.api;

import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.io.IODescriptor;

import java.util.Collection;
import java.util.Optional;

/**
 * A central manager and executor for a collection of registered commands
 * A CommandManager is used to coordinate much of the process of using Mandate, including providing a
 * central hub for registering commands, handling the parsing and eventual execution of user input, and
 * allowing external components to interact with the command ecosystem
 *
 * @author Foundry
 */
public interface CommandManager {

    /**
     * Registers all correctly defined commands in the specified container object
     * A command method is considered to be correctly defined if either of these requirements are met:
     *  - It is flagged as an {@link pw.stamina.mandate.api.annotations.Executes executor} and has its own specific {@link Syntax Syntax} annotation
     *  - It is flagged as an {@link pw.stamina.mandate.api.annotations.Executes executor} and it's containing class has a global {@link Syntax Syntax} annotation
     *  If duplicate commands exist in a registered container, a CommandManager implementation should take steps to prevent those commands from being re-added to the
     *  command registry
     *
     * @param container the object containing the commands to be registered
     * @return {@code true} if any valid commands were registered, else {@code false}
     */
    boolean register(Object container);

    /**
     * Tries to execute a command based on the provided input
     * If the execution fails as a result of invalid or malformed user input, the result of this
     * will immediately have a present exit code of {@link pw.stamina.mandate.api.execution.result.ExitCode#INVALID INVALID}. Otherwise,
     * this will return an {@link Execution execution} corresponding to the evaluated command running possibly asynchronously
     *
     * @param input the input to be tokenized and parsed as a command and it's arguments
     * @return a running execution if the invocation completed without errors, else a completed execution with an exit code of INVALID
     */
    Execution execute(String input);

    /**
     * Tries to execute a command based on the provided input, using the provided IODescriptor for the execution
     * If the execution fails as a result of invalid or malformed user input, the result of this
     * will immediately have a present exit code of {@link pw.stamina.mandate.api.execution.result.ExitCode#INVALID INVALID}. Otherwise,
     * this will return an {@link Execution execution} corresponding to the evaluated command running possibly asynchronously
     *
     * @param input the input to be tokenized and parsed as a command and it's arguments
     * @return a running execution if the invocation completed without errors, else a completed execution with an exit code of INVALID
     */
    Execution execute(String input, IODescriptor descriptor);

    /**
     * Attempts to find an {@link ArgumentHandler ArgumentHandler} capable of handling the provided type
     *
     * @param type the type for which an argument handler should be looked up
     * @param <T> the type of value returned by the searched argument handler
     * @return an ArgumentHandler capable of handling the provided type if one is present, else {@link Optional#empty() an empty Optional}
     */
    <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type);

    /**
     * @return a collection of all the base syntax components from which valid commands exist
     */
    Collection<SyntaxComponent> getCommands();
}
