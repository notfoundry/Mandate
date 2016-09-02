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

package pw.stamina.mandate.internal.execution.argument;

import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.ArgumentHandlerRegistry;

import java.util.Optional;

/**
 * @author Foundry
 */
public class ForkedArgumentHandlerRegistry implements ArgumentHandlerRegistry {

    private final ArgumentHandlerRegistry forkedRegistry;

    private final ArgumentHandlerRegistry thisRegistry;

    public ForkedArgumentHandlerRegistry(final ArgumentHandlerRegistry forkedRegistry, final ArgumentHandlerRegistry thisRegistry) {
        this.forkedRegistry = forkedRegistry;
        this.thisRegistry = thisRegistry;
    }

    @Override
    public <T> Optional<ArgumentHandler<T>> findArgumentHandler(final Class<T> type) {
        final Optional<ArgumentHandler<T>> lookup = thisRegistry.findArgumentHandler(type);
        return lookup.isPresent() ? lookup : forkedRegistry.findArgumentHandler(type);
    }

    @Override
    public boolean addArgumentHandler(final ArgumentHandler<?> argumentHandler) {
        return thisRegistry.addArgumentHandler(argumentHandler);
    }
}
