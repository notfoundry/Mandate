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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Foundry
 */
public class SimpleArgumentProvider implements ArgumentProvider {

    private final Map<Class<?>, Supplier<?>> valueProviders;

    public SimpleArgumentProvider(final Map<Class<?>, Supplier<?>> valueProviders) {
        this.valueProviders = valueProviders;
    }

    @Override
    public <V> void registerProvider(final Class<V> valueType, final Supplier<? extends V> valueProvider) {
        valueProviders.put(valueType, valueProvider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Optional<Supplier<? extends V>> findProvider(final Class<V> valueType) {
        return Optional.ofNullable(valueProviders.get(valueType)).map(provider -> (Supplier<? extends V>) provider);
    }

    @Override
    public boolean isProviderPresent(final Class<?> valueType) {
        return valueProviders.containsKey(valueType);
    }
}
