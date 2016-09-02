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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.parsing.InputParsingException;

import java.util.Arrays;

/**
 * @author Foundry
 */
public final class EnumArgumentHandler implements ArgumentHandler<Enum<?>> {

    @Override
    public Enum<?> parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        final String lookupString = input.getRaw().toLowerCase();
        for (final Enum<?> constant : (Enum<?>[]) parameter.getType().getEnumConstants()) {
            if (constant.name().toLowerCase().equals(lookupString) || constant.toString().equals(lookupString)) {
                return constant;
            }
        }
        throw new InputParsingException(String.format("'%s' is not a valid constant in enumeration %s", input.getRaw(), parameter.getType().getCanonicalName()));
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final String constantsString = Arrays.toString(parameter.getType().getEnumConstants());
        return parameter.getLabel() + " - " + "one of " + constantsString.substring(1, constantsString.length() - 1);
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Enum.class};
    }

}

