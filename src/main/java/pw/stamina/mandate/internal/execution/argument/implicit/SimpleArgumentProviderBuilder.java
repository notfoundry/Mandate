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

package pw.stamina.mandate.internal.execution.argument.implicit;

import pw.stamina.mandate.execution.argument.ArgumentProvider;
import pw.stamina.mandate.execution.argument.ArgumentProviderBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Foundry
 */
public class SimpleArgumentProviderBuilder implements ArgumentProviderBuilder {

    private final Map<Class<?>, Supplier<?>> valueProviders;

    public SimpleArgumentProviderBuilder() {
        this.valueProviders = new HashMap<>();
    }

    @Override
    public <T> ArgumentProviderBuilder addProvider(final Class<T> valueType, final Supplier<? extends T> valueProvider) {
        if (valueProviders.put(valueType, valueProvider) != null) {
            throw new IllegalStateException(String.format("Top-level argument provider already mapped for arguments of type %s", valueType.getCanonicalName()));
        }
        return this;
    }

    @Override
    public ArgumentProvider build() {
        return ArgumentProviderFactory.fromMapping(valueProviders);
    }
}
