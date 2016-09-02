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

package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.execution.CommandConfiguration;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.argument.ArgumentProvider;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.argument.ArgumentHandlerRegistry;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.io.IOEnvironment;
import pw.stamina.mandate.parsing.InputTokenizationException;
import pw.stamina.mandate.syntax.CommandRegistry;
import pw.stamina.mandate.syntax.ExecutableLookup;
import pw.stamina.mandate.internal.execution.executable.context.ExecutionContextFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Deque;

/**
 * @author Foundry
 */
public class DefaultCommandContext implements CommandContext {

    private final IOEnvironment ioEnvironment;

    private final CommandConfiguration commandConfiguration;

    private final ArgumentHandlerRegistry argumentHandlerRegistry;

    private final CommandRegistry commandRegistry;

    private final ArgumentProvider providerRepository;

    public DefaultCommandContext(final IOEnvironment ioEnvironment, final CommandConfiguration commandConfiguration, final ArgumentHandlerRegistry argumentHandlerRegistry, final CommandRegistry commandRegistry, final ArgumentProvider providerRepository) {
        this.ioEnvironment = ioEnvironment;
        this.commandConfiguration = commandConfiguration;
        this.argumentHandlerRegistry = argumentHandlerRegistry;
        this.commandRegistry = commandRegistry;
        this.providerRepository = providerRepository;
    }

    @Override
    public boolean register(final Object container) {
        boolean registered = false;
        for (final Method method : container.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Executes.class)) {
                commandConfiguration.getSyntaxCreationStrategy().getComponents(method, container, this).forEach(commandRegistry::addCommand);
                registered = true;
            }
        }
        return registered;
    }

    @Override
    public Execution execute(final String input, final IODescriptor descriptor) {
        if (input == null) {
            descriptor.err().write("Invalid input: input cannot be empty");
            return Execution.complete(ExitCode.INVALID);
        } else if (input.isEmpty()) {
            descriptor.err().write("Invalid input: input cannot be empty");
            return Execution.complete(ExitCode.INVALID);
        }

        final Deque<CommandArgument> arguments;
        try {
            arguments = commandConfiguration.getInputTokenizationStrategy().parse(input, commandConfiguration.getArgumentCreationStrategy());
        } catch (final InputTokenizationException e) {
            descriptor.err().write("Exception tokenizing input: " + e.getLocalizedMessage());
            return Execution.complete(ExitCode.INVALID);
        }

        final ExecutionContext executionContext = ExecutionContextFactory.makeContext(Collections.singletonMap(IODescriptor.class, descriptor), providerRepository);
        final ExecutableLookup executableLookup = commandRegistry.findExecutable(arguments, executionContext);
        if (executableLookup.wasSuccessful()) {
            return executableLookup.getExecutable().execute(arguments, executionContext);
        } else {
            descriptor.err().write(executableLookup.getException().getMessage());
            return Execution.complete(ExitCode.INVALID);
        }
    }

    @Override
    public Execution execute(final String input) {
        return execute(input, ioEnvironment.descriptorFactory().get());
    }

    @Override
    public IOEnvironment getIOEnvironment() {
        return ioEnvironment;
    }

    @Override
    public CommandConfiguration getCommandConfiguration() {
        return commandConfiguration;
    }

    public ArgumentHandlerRegistry getArgumentHandlers() {
        return argumentHandlerRegistry;
    }

    public CommandRegistry getRegisteredCommands() {
        return commandRegistry;
    }

    @Override
    public ArgumentProvider getValueProviders() {
        return providerRepository;
    }
}
