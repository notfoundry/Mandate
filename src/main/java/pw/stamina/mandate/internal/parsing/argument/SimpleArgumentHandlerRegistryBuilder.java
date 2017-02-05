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
import pw.stamina.mandate.parsing.argument.ArgumentHandlerRegistryBuilder;

import java.util.Arrays;

/**
 * @author Mark Johnson
 */
public class SimpleArgumentHandlerRegistryBuilder implements ArgumentHandlerRegistryBuilder {

    private final ArgumentHandlerRegistry argumentHandlerRegistry;

    public SimpleArgumentHandlerRegistryBuilder() {
        this.argumentHandlerRegistry = new PrimitiveWrappingArgumentHandlerRegistryDecorator(new SimpleArgumentHandlerRegistry());
    }

    @Override
    public ArgumentHandlerRegistryBuilder addHandler(final ArgumentHandler<?> argumentHandler) {
        if (!argumentHandlerRegistry.addArgumentHandler(argumentHandler)) {
            throw new IllegalStateException(String.format("A top-level handler for arguments of type %s already exists in the registry", Arrays.asList(argumentHandler.getHandledTypes())));
        }
        return this;
    }

    @Override
    public ArgumentHandlerRegistry build() {
        return argumentHandlerRegistry;
    }
}
