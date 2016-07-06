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

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.internal.annotations.strings.Equals;
import pw.stamina.mandate.internal.annotations.strings.Length;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.exceptions.ParseFailException;

import java.util.regex.Pattern;

/**
 * @author Foundry
 */
public class StringArgumentHandler implements ArgumentHandler<String> {

    @Override
    public String parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        if (input.getRaw().length() == 0) {
            return null;
        }
        Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            for (String string : equals.value()) {
                if (!(equals.regex() ? Pattern.compile(string).matcher(input.getRaw()).matches() : string.equalsIgnoreCase(input.getRaw()))) continue;
                return input.getRaw();
            }
            throw new ParseFailException(input.getRaw(), this.getClass(), String.format("'%s' doesn't match %s (regex=%s)", input.getRaw(), String.format("['%s']", String.join("'/'", equals.value())), equals.regex()));
        }
        Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            int min = Math.min(length.min(), length.max());
            int max = Math.max(length.min(), length.max());
            if (input.getRaw().length() < min) {
                throw new ParseFailException(input.getRaw(), this.getClass(), String.format("'%s' is too short: length can be between %s-%s characters", input.getRaw(), min, max));
            } else if (input.getRaw().length() > max) {
                throw new ParseFailException(input.getRaw(), this.getClass(), String.format("'%s' is too long: length can be between %s-%s characters", input.getRaw(), min, max));
            }
        }
        return input.getRaw();
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            return parameter.getLabel() + " - " + String.format("String['%s', regex=%s]", String.join("'/'", equals.value()), equals.regex());
        }
        Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("String[%s-%s]", Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + "String";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {String.class};
    }
}
