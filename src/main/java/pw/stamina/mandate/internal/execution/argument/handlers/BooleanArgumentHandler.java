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

import java.util.*;

/**
 * @author Foundry
 */
public final class BooleanArgumentHandler implements ArgumentHandler<Boolean> {

    private static final Map<String, Boolean> BOOLEAN_LOOKUPS;

    @Override
    public Boolean parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        return Optional.ofNullable(BOOLEAN_LOOKUPS.get(input.getRaw().toLowerCase()))
                .orElseThrow(() -> new InputParsingException(String.format("'%s' cannot be parsed to a boolean.", input)));
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        return parameter.getLabel() + " - " + "Boolean";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Boolean.class};
    }

    static {
        final HashMap<String, Boolean> lookups = new HashMap<>();
        Arrays.asList("enable", "true", "yes", "on", "1").forEach(x -> lookups.put(x, true));
        Arrays.asList("disable", "false", "off", "no", "0").forEach(x -> lookups.put(x, false));
        BOOLEAN_LOOKUPS = Collections.unmodifiableMap(lookups);
    }
}

