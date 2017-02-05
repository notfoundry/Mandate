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

package pw.stamina.mandate.internal.parsing.argument;

import pw.stamina.mandate.parsing.argument.ArgumentHandler;
import pw.stamina.mandate.parsing.argument.ArgumentHandlerRegistry;
import pw.stamina.mandate.internal.utils.PrimitiveArrays;
import pw.stamina.mandate.internal.utils.Primitives;

import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class PrimitiveWrappingArgumentHandlerRegistryDecorator implements ArgumentHandlerRegistry {

    private final ArgumentHandlerRegistry argumentHandlerRegistry;

    public PrimitiveWrappingArgumentHandlerRegistryDecorator(final ArgumentHandlerRegistry backingArgumentHandlerRegistry) {
        this.argumentHandlerRegistry = backingArgumentHandlerRegistry;
    }

    @Override
    public <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type) {
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        } else if (type.isArray()) {
            @SuppressWarnings("unchecked")
            final Class<T> wrappedArray = (Class<T>) PrimitiveArrays.wrap(type);
            type = wrappedArray;
        }
        return argumentHandlerRegistry.findArgumentHandler(type);
    }

    @Override
    public boolean addArgumentHandler(final ArgumentHandler<?> argumentHandler) {
        return argumentHandlerRegistry.addArgumentHandler(argumentHandler);
    }
}
