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

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.parsor.api.parsing.Parser;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.exceptions.ParseFailException;

import java.util.Deque;
import java.util.Optional;

/**
 * @author Foundry
 */
public class ArgumentToObjectParser implements Parser<Object[], Deque<CommandArgument>> {
    private final CommandExecutable executable;

    private final CommandManager commandManager;

    public ArgumentToObjectParser(CommandExecutable executable, CommandManager commandManager) {
        this.executable = executable;
        this.commandManager = commandManager;
    }

    public Object[] parse(Deque<CommandArgument> arguments) throws ParseException {
        final Object[] parsedArgs = new Object[executable.getParameters().size()];
        final boolean[] presentArgs = new boolean[parsedArgs.length];
        for (int i = 0; i < parsedArgs.length; i++) {
            if (!executable.getParameters().get(i).isOptional()) {
                Optional<ArgumentHandler<Object>> argumentHandler = commandManager.findArgumentHandler((Class<Object>) executable.getParameters().get(i).getType());
                if (argumentHandler.isPresent()) {
                    parsedArgs[i] = argumentHandler.get().parse(arguments.poll(), executable.getParameters().get(i), commandManager);
                    presentArgs[i] = true;
                } else {
                    throw new ParseFailException(executable.getParameters().get(i).getLabel(), this.getClass(), String.format("No argument handler exists for argument parameter type '%s'", executable.getParameters().get(i).getType().getCanonicalName()));
                }
            } else {
                parsedArgs[i] = Optional.empty();
            }
        }
        return parsedArgs;
    }
}
