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

package pw.stamina.mandate.internal.execution.executable.context;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.argument.ArgumentProvider;
import pw.stamina.mandate.io.IODescriptor;

import java.util.Map;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class SimpleExecutionContext implements ExecutionContext {

    private final Map<Class<?>, ?> localValues;

    private final ArgumentProvider providerRepository;

    public SimpleExecutionContext(final Map<Class<?>, ?> localValues, final ArgumentProvider providerRepository) {
        this.localValues = localValues;
        this.providerRepository = providerRepository;
    }

    @Override
    public IODescriptor getIODescriptor() {
        return (IODescriptor) localValues.get(IODescriptor.class);
    }

    @Override
    public <T> T getProvidedValue(final Class<T> type) {
        return Optional.ofNullable((T) localValues.get(type))
                .orElseGet(() -> providerRepository.findProvider(type)
                        .orElseThrow(() -> new IllegalArgumentException(""))
                        .get()
                );
    }
}
