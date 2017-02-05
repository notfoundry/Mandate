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
import java.util.function.Supplier;

/**
 * A builder object for an {@link ArgumentProvider ArgumentProvider}. Similar to what ArgumentProvider guarantees, this builder
 * will prevent any potentially ambiguous argument providers from being added to the registry that is being built.
 *
 * @author Mark Johnson
 */
public interface ArgumentProviderBuilder {

    /**
     * Attempts to register a new implicit value provider to the provider registry being built. The registration will
     * fail if it will result in a potentially ambiguous choice of value suppliers, such as when a provider of values
     * of type V has already been registered to the registry being built.
     *
     * @param valueType A class representing the type of value that this provider will be supplying
     * @param valueProvider the provider supplying values of type V
     * @param <T> the type of value that this provider will be supplying
     * @return this ArgumentProviderBuilder instance
     */
    <T> ArgumentProviderBuilder addProvider(Class<T> valueType, Supplier<? extends T> valueProvider);

    /**
     * Attempts to register a new implicit value provider to the provider registry being built. The registration will
     * fail if it will result in a potentially ambiguous choice of value suppliers.
     * <p>
     * This method differs from {@link ArgumentProviderBuilder#addProvider(Class, Supplier)} in that it support using
     * parameterized types as value types, allowing value providers to provide very specfic object instances for parameters.
     *
     * @param valueType A class representing the type of value that this provider will be supplying
     * @param valueProvider the provider supplying values of type V
     * @return this ArgumentProviderBuilder instance
     */
    ArgumentProviderBuilder addProvider(Type valueType, Supplier<?> valueProvider);

    /**
     * Returns a newly constructed {@link ArgumentProvider ArgumentProvider} with all argument providers
     * that had been provided during the construction process automatically registered to it.
     * @return a newly constructed {@link ArgumentProvider ArgumentProvider}
     */
    ArgumentProvider build();
}
