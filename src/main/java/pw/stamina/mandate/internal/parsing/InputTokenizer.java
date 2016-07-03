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

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Foundry
 */
public enum InputTokenizer {
    INSTANCE;

    public static InputTokenizer getInstance() {
        return INSTANCE;
    }

    public Deque<CommandArgument> tokenize(CharSequence input) {
        boolean quoted = false;
        final Deque<CommandArgument> arguments = new ArrayDeque<>();
        StringBuilder argument = new StringBuilder();
        parserBlock : for (int index = 0; index < input.length(); index++) {
            switch (input.charAt(index)) {
                case '\"': {
                    if (index > 0 && input.charAt(index - 1) == '\\') {
                        argument.setCharAt(argument.length() - 1, '\"');
                    } else {
                        quoted = !quoted;
                    }
                    continue parserBlock;
                }
                case ' ': {
                    if (!quoted) {
                        if (argument.length() <= 0) continue parserBlock;
                        arguments.add(new BaseCommandArgument(argument.toString()));
                        argument.setLength(0);
                        continue parserBlock;
                    }
                }
                default: {
                    argument.append(input.charAt(index));
                }
            }
        }
        if (argument.length() > 0) arguments.add(new BaseCommandArgument(argument.toString()));
        return arguments;
    }
}
