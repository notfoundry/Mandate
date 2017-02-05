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

package pw.stamina.mandate.internal.execution.executable.context;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.parsing.argument.ArgumentProvider;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.security.CommandSender;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class SimpleExecutionContext implements ExecutionContext {

    private final Map<Type, Object> localValues;

    private final ArgumentProvider providerRepository;

    private final CommandSender commandSender;

    public SimpleExecutionContext(final Map<Type, Object> localValues, final ArgumentProvider providerRepository, final CommandSender commandSender) {
        this.localValues = localValues;
        this.providerRepository = providerRepository;
        this.commandSender = commandSender;
    }

    @Override
    public IODescriptor getIODescriptor() {
        return (IODescriptor) localValues.get(IODescriptor.class);
    }

    @Override
    public Object getProvidedValue(final Type type) {
        return Optional.ofNullable(localValues.get(type))
                .orElseGet(() ->  providerRepository.findProvider(type)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to resolve a suitable value for type " + type.getTypeName()))
                        .get()
                );
    }

    @Override
    public CommandSender getCommandSender() {
        return commandSender;
    }
}
