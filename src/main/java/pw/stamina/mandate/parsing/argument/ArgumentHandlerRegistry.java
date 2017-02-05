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

package pw.stamina.mandate.parsing.argument;

import java.util.Optional;

/**
 * A registry of all {@link ArgumentHandler argument handlers} available for use by a given {@link pw.stamina.mandate.execution.CommandContext CommandContext}
 * when attempting to reify user-provided arguments during the execution of one or more commands.
 * <p>
 * This registry guarantees that no ambiguous argument handlers will exist, meaning that if an argument handler claiming to support a type T has already been registered,
 * another argument handler that also claims to support type T cannot be registered.
 *
 * @author Mark Johnson
 */
public interface ArgumentHandlerRegistry {
    /**
     * Attempts to find an {@link ArgumentHandler ArgumentHandler} capable of handling the provided type
     *
     * @param type the type for which an argument handler should be looked up
     * @param <T> the type of value returned by the searched argument handler
     * @return an ArgumentHandler capable of handling the provided type if one is present, else {@link Optional#empty() an empty Optional}
     */
    <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type);

    /**
     * Attempts to register a new argument handler for a given set of types to this registry. This call will return true
     * if there are no type incompatibilities with an existing argument handler existing in this registry, and will return false
     * if any incompatibilities are found.
     *
     * @param argumentHandler the argument handler that should be added to this handler registry
     * @return {@code true} if the provided handler has been successfully added to this handler registry, else {@code false}
     */
    boolean addArgumentHandler(ArgumentHandler<?> argumentHandler);
}
