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

package pw.stamina.mandate.internal.parsing;

import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.internal.execution.argument.BaseCommandArgument;
import pw.stamina.parsor.api.parsing.Parser;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.exceptions.ParseFailException;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Foundry
 */
public enum InputToArgumentParser implements Parser<String, Deque<CommandArgument>> {
    INSTANCE;

    public static InputToArgumentParser getInstance() {
        return INSTANCE;
    }

    @Override
    public Deque<CommandArgument> parse(String input) throws ParseException {
        Deque<CommandArgument> arguments = new ArrayDeque<>();
        StringBuilder content = new StringBuilder();

        boolean escaped = false, quoted = false; int depth = 0;
        for (char character : input.toCharArray()) {
            if (escaped) {
                content.append(character);
                escaped = false;
            } else {
                switch (character) {
                    case '\\':
                        escaped = true;
                        break;
                    case '"':
                        quoted = !quoted;
                        if (depth > 0) {
                            content.append(character);
                        }
                        break;
                    case ']':
                        if (!quoted) {
                            depth--;
                        }
                        content.append(character);
                        break;
                    case '[':
                        if (!quoted) {
                            depth++;
                        }
                        content.append(character);
                        break;
                    case ' ':
                        if (!quoted && depth == 0) {
                            append(content, arguments);
                            break;
                        }
                    default:
                        content.append(character);
                }
            }
        }
        if (depth > 0) {
            throw new ParseFailException(this, input, CommandArgument.class, "Found unterminated list in input: missing " + depth + " terminators");
        } else if (depth < 0) {
            throw new ParseFailException(this, input, CommandArgument.class, "Found " + Math.abs(depth) + " too many list terminators in input");
        }

        append(content, arguments);
        return arguments;
    }

    private static void append(StringBuilder content, Deque<CommandArgument> output) {
        if (content.length() == 0) return;

        output.add(new BaseCommandArgument(content.toString()));
        content.setLength(0);
    }
}
