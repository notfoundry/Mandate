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

package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.execution.CommandConfiguration;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.ContextBuilder;
import pw.stamina.mandate.parsing.argument.ArgumentHandlerRegistry;
import pw.stamina.mandate.parsing.argument.ArgumentProvider;
import pw.stamina.mandate.internal.parsing.argument.ForkedArgumentHandlerRegistry;
import pw.stamina.mandate.internal.parsing.argument.implicit.ForkedArgumentProvider;
import pw.stamina.mandate.internal.syntax.ForkedCommandRegistry;
import pw.stamina.mandate.io.IOEnvironment;
import pw.stamina.mandate.syntax.CommandRegistry;
import pw.stamina.mandate.internal.parsing.argument.DefaultArgumentHandlerRegistry;
import pw.stamina.mandate.internal.parsing.argument.implicit.ArgumentProviderFactory;
import pw.stamina.mandate.internal.io.DefaultIOEnvironment;
import pw.stamina.mandate.internal.syntax.SimpleCommandRegistry;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class SimpleContextBuilder implements ContextBuilder {

    private IOEnvironment ioEnvironment;

    private CommandConfiguration commandConfiguration;

    private ArgumentHandlerRegistry argumentHandlerRegistry;

    private CommandRegistry commandRegistry;

    private ArgumentProvider argumentProvider;

    @Override
    public ContextBuilder usingIOEnvironment(final IOEnvironment ioEnvironment) {
        checkPrecondition(this.ioEnvironment == null, "IO Environment has already been supplied");
        this.ioEnvironment = ioEnvironment;
        return this;
    }

    @Override
    public ContextBuilder usingConfiguration(final CommandConfiguration commandConfiguration) {
        checkPrecondition(this.commandConfiguration == null, "Command Configuration has already been supplied");
        this.commandConfiguration = commandConfiguration;
        return this;
    }

    @Override
    public ContextBuilder withHandlerRegistry(final ArgumentHandlerRegistry argumentHandlerRegistry) {
        if (this.argumentHandlerRegistry == null) {
            this.argumentHandlerRegistry = DefaultArgumentHandlerRegistry.makeDefaultRegistry();
        }
        this.argumentHandlerRegistry = new ForkedArgumentHandlerRegistry(this.argumentHandlerRegistry, argumentHandlerRegistry);
        return this;
    }

    @Override
    public ContextBuilder withCommandRegistry(final CommandRegistry commandRegistry) {
        if (this.commandRegistry == null) {
            this.commandRegistry = commandRegistry;
        } else {
            this.commandRegistry = new ForkedCommandRegistry(this.commandRegistry, commandRegistry);
        }
        return this;
    }

    @Override
    public ContextBuilder withArgumentProvider(final ArgumentProvider argumentProvider) {
        if (this.argumentProvider == null) {
            this.argumentProvider = argumentProvider;
        } else {
            this.argumentProvider = new ForkedArgumentProvider(this.argumentProvider, argumentProvider);
        }
        return this;
    }

    @Override
    public CommandContext build() {
        return new DefaultCommandContext(
                Optional.ofNullable(ioEnvironment).orElseGet(DefaultIOEnvironment::getInstance),
                Optional.ofNullable(commandConfiguration).orElseGet(DefaultCommandConfiguration::getInstance),
                Optional.ofNullable(argumentHandlerRegistry).orElseGet(DefaultArgumentHandlerRegistry::makeDefaultRegistry),
                Optional.ofNullable(commandRegistry).orElseGet(SimpleCommandRegistry::new),
                Optional.ofNullable(argumentProvider).orElseGet(() -> ArgumentProviderFactory.fromMapping(new HashMap<>()))
        );
    }

    private static void checkPrecondition(final boolean assertion, final String failureMessage) {
        if (!assertion) throw new IllegalStateException(failureMessage);
    }
}
