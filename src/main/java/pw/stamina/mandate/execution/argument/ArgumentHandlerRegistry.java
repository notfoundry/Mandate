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

package pw.stamina.mandate.execution.argument;

import java.util.Optional;

/**
 * @author Foundry
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

    boolean addArgumentHandler(ArgumentHandler<?> argumentHandler);
}
