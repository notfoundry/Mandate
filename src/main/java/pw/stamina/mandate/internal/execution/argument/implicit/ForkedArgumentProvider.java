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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Foundry
 */
public class ForkedArgumentProvider implements ArgumentProvider {

    private final ArgumentProvider forkedProvider;

    private final ArgumentProvider thisProvider;

    public ForkedArgumentProvider(final ArgumentProvider forkedProvider, final ArgumentProvider thisProvider) {
        this.forkedProvider = forkedProvider;
        this.thisProvider = thisProvider;
    }

    @Override
    public <V> void registerProvider(final Class<V> valueType, final Supplier<? extends V> valueProvider) {
        if (thisProvider.isProviderPresent(valueType)) {
            throw new IllegalStateException(String.format("Top-level argument provider already mapped for arguments of type %s", valueType.getCanonicalName()));
        } else {
            thisProvider.registerProvider(valueType, valueProvider);
        }
    }

    @Override
    public <V> Optional<Supplier<? extends V>> findProvider(final Class<V> valueType) {
        final Optional<Supplier<? extends V>> lookup = thisProvider.findProvider(valueType);
        return lookup.isPresent() ? lookup : forkedProvider.findProvider(valueType);
    }

    @Override
    public boolean isProviderPresent(final Class<?> valueType) {
        return thisProvider.isProviderPresent(valueType) || forkedProvider.isProviderPresent(valueType);
    }
}
