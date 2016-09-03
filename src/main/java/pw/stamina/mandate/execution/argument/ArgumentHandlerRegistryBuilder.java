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

package pw.stamina.mandate.execution.argument;

/**
 * A builder object for an {@link ArgumentHandlerRegistry ArgumentHandlerRegistry}. Similar to what ArgumentHandlerRegistry guarantees
 * when validating newly added {@link ArgumentHandler ArgumentHandler} instances, this builder guarantees that no ambiguous ArgumentHandlers
 * can be present at any given time.
 *
 * @author Mark Johnson
 */
public interface ArgumentHandlerRegistryBuilder {

    /**
     * Attempts to register a new argument handler for a given set of types to the registry under construction. This call will return true
     * if there are no type incompatibilities with an existing argument handler existing in the registry, and will return false
     * if any incompatibilities are found.
     *
     * @param argumentHandler the argument handler that should be added to the handler registry under construction
     * @return this ArgumentHandlerRegistryBuilder instance
     */
    ArgumentHandlerRegistryBuilder addHandler(ArgumentHandler<?> argumentHandler);

    /**
     * Returns a newly constructed {@link ArgumentHandlerRegistry ArgumentHandlerRegistry} with all {@link ArgumentHandler ArgumentHandlers}
     * that had been provided during the construction process automatically registered to it.
     * @return a newly constructed {@link ArgumentHandlerRegistry ArgumentHandlerRegistry}
     */
    ArgumentHandlerRegistry build();
}
