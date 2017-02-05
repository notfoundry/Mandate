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

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A registry of {@link pw.stamina.mandate.annotations.Implicit implicit argument} value providers. {@link pw.stamina.mandate.execution.CommandContext CommandContext}
 * instances may use this as a registry of all valid implicit types and their associated value suppliers to more effectively support
 * implicit values in command execution.
 * <p>
 * This provider makes the guarantee that no two providers may be present for any given type,
 * preventing any potential ambiguities in implicit argument value resolution.
 *
 * @author Mark Johnson
 */
public interface ArgumentProvider {

    /**
     * Attempts to register a new implicit value provider to this provider registry. The registration will fail if it will result
     * in a potentially ambiguous choice of value suppliers, such as when a provider of values of type V has already been
     * registered to this registry.
     *
     * @param valueType A class representing the type of value that this provider will be supplying
     * @param valueProvider the provider supplying values of type V
     */
    void registerProvider(Type valueType, Supplier<?> valueProvider);

    /**
     * Attempts to locate a provider of values of type V, if one is present.
     *
     * @param valueType A class representing the type of value that this provider to be located should supply
     * @return A present {@link Optional Optional} value wrapping the located provider if it is present, else an {@link Optional#empty() empty Optional}
     */
    Optional<Supplier<?>> findProvider(Type valueType);

    /**
     * Determines if there is a argument provider present that is capable of returning values of the type
     * described by valueType.
     *
     * @param valueType A class representing the type of value that this provider to be located should supply
     * @return {@code true} if a matching argument provider is found, else {@code false}
     */
    boolean isProviderPresent(Type valueType);
}
