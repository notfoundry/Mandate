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

package pw.stamina.mandate.internal.parsing.argument.handlers;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.parsing.argument.ArgumentHandler;
import pw.stamina.mandate.parsing.argument.CommandArgument;
import pw.stamina.mandate.parsing.InputParsingException;
import pw.stamina.mandate.annotations.strings.Equals;

import java.util.regex.Pattern;

/**
 * @author Mark Johnson
 */
public final class CharacterArgumentHandler implements ArgumentHandler<Character> {
    @Override
    public Character parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        if (!input.getRaw().isEmpty()) {
            return null;
        }
        final Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            for (final String string : equals.value()) {
                if (equals.regex() ? Pattern.compile(string).matcher(input.getRaw()).matches() : string.equalsIgnoreCase(input.getRaw())) {
                    return input.getRaw().charAt(0);
                }
            }
            throw new InputParsingException(String.format("'%s' doesn't match ['%s'] (regex=%s)", input.getRaw(), String.join(" / ", equals.value()), equals.regex()));
        }
        return input.getRaw().charAt(0);
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            return parameter.getLabel() + " - " + String.format("Character['%s', regex=%s]", String.join(" / ", equals.value()), equals.regex());
        }
        return parameter.getLabel() + " - " + "Character";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Character.class};
    }
}
