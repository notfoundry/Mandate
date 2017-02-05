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
import pw.stamina.mandate.annotations.Length;
import pw.stamina.mandate.annotations.strings.Equals;

import javax.swing.text.Segment;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Mark Johnson
 */
public class CharSequenceArgumentHandler implements ArgumentHandler<CharSequence> {

    private static final Map<Class<?>, Function<String, ? extends CharSequence>> sequencers;

    @Override
    public CharSequence parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        if (input.getRaw().isEmpty()) {
            return null;
        }
        final Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            for (final String string : equals.value()) {
                if (!(equals.regex() ? Pattern.compile(string).matcher(input.getRaw()).matches() : string.equalsIgnoreCase(input.getRaw()))) continue;
                return formatSequence(input.getRaw(), parameter.getType());
            }
            throw new InputParsingException(String.format("'%s' doesn't match ['%s'] (regex=%s)", input.getRaw(), String.join(" / ", equals.value()), equals.regex()));
        }
        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            final int min = Math.min(length.min(), length.max());
            final int max = Math.max(length.min(), length.max());
            if (input.getRaw().length() < min) {
                throw new InputParsingException(String.format("'%s' is too short: length can be between %d-%d characters", input.getRaw(), min, max));
            } else if (input.getRaw().length() > max) {
                throw new InputParsingException(String.format("'%s' is too long: length can be between %d-%d characters", input.getRaw(), min, max));
            }
        }
        return formatSequence(input.getRaw(), parameter.getType());
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final Equals equals = parameter.getAnnotation(Equals.class);
        if (equals != null) {
            return parameter.getLabel() + " - " + String.format("%s['%s', regex=%s]", parameter.getType().getSimpleName(), String.join(" / ", equals.value()), equals.regex());
        }
        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("%s[%s-%s chars]", parameter.getType().getSimpleName(), Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + "String";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {CharSequence.class};
    }

    private static CharSequence formatSequence(final String input, final Class<?> sequenceType) throws InputParsingException {
        return Optional.ofNullable(sequencers.get(sequenceType))
                .map(f -> f.apply(input))
                .orElseThrow(() -> new InputParsingException(String.format("CharSequences of type %s are not supported at this time", sequenceType.getCanonicalName())));
    }

    static {
        final Map<Class<?>, Function<String, ? extends CharSequence>> seq = new HashMap<>();
        seq.put(String.class, Function.identity());
        seq.put(StringBuilder.class, StringBuilder::new);
        seq.put(StringBuffer.class, StringBuffer::new);
        seq.put(CharBuffer.class, CharBuffer::wrap);
        seq.put(Segment.class, s -> new Segment(s.toCharArray(), 0, s.length()));
        sequencers = seq;
    }
}
