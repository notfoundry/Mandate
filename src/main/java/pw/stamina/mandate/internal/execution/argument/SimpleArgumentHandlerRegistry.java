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

package pw.stamina.mandate.internal.execution.argument;

import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.ArgumentHandlerRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class SimpleArgumentHandlerRegistry implements ArgumentHandlerRegistry {

    private final Map<Class<?>, ArgumentHandler<?>> argumentHandlers;

    public SimpleArgumentHandlerRegistry() {
        this.argumentHandlers = new HashMap<>();
    }

    @Override
    public <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type) {
        @SuppressWarnings("unchecked")
        final ArgumentHandler<T> handlerLookup = (ArgumentHandler<T>) argumentHandlers.get(type);
        if (handlerLookup != null) {
            return Optional.of(handlerLookup);
        } else {
            for (final ArgumentHandler<?> argumentHandler : argumentHandlers.values()) {
                for (final Class<?> handledType : argumentHandler.getHandledTypes()) {
                    if (handledType.isAssignableFrom(type)) {
                        @SuppressWarnings("unchecked")
                        final ArgumentHandler<T> handler = (ArgumentHandler<T>) argumentHandler;
                        return Optional.of(handler);
                    }
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean addArgumentHandler(final ArgumentHandler<?> argumentHandler) {
        for (final Class<?> handledType : argumentHandler.getHandledTypes()) {
            argumentHandlers.put(handledType, argumentHandler);
        }
        return true;
    }
}
