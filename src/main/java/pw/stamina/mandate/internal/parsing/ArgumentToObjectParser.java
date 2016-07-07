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
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.api.annotations.flag.UserFlag;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.internal.execution.argument.BaseCommandArgument;
import pw.stamina.parsor.api.parsing.Parser;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.exceptions.ParseFailException;

import java.util.*;

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
        final CommandParameter[] parameters = executable.getParameters().toArray(new CommandParameter[executable.getParameters().size()]);
        final Set<String> excludedFlags = new HashSet<>();

        for (int i = 0; i < parsedArgs.length; i++) {
            Optional<ArgumentHandler<Object>> argumentHandler = commandManager.findArgumentHandler((Class<Object>) parameters[i].getType());
            if (!argumentHandler.isPresent()) {
                throw new ParseFailException(parameters[i].getLabel(), CommandArgument.class, String.format("No argument handler exists for argument parameter type '%s'", parameters[i].getType().getCanonicalName()));
            }

            AutoFlag autoFlag; UserFlag userFlag;
            if ((autoFlag = parameters[i].getAnnotation(AutoFlag.class)) != null) {
                CommandArgument present = popFlagIfPresent(arguments, autoFlag.flag());

                Optional<String> conflictingFlagLookup;
                if (present != null && (conflictingFlagLookup = findConflictingFlag(autoFlag.flag(), autoFlag.xor(), excludedFlags)).isPresent()) {
                    throw new ParseFailException(Arrays.toString(autoFlag.flag()), AutoFlag.class, String.format("Provided flag '%s' conflicts with exclusive flag '%s'", present.getRaw(), conflictingFlagLookup.get()));
                }

                String def = (parameters[i].getType() == Boolean.class || parameters[i].getType() == Boolean.TYPE) ? present != null ? "true" : "false" : present != null ? autoFlag.ifdef() : autoFlag.elsedef();
                if (!parameters[i].isOptional()) {
                    parsedArgs[i] = (!def.isEmpty()) ? argumentHandler.get().parse(new BaseCommandArgument(def), parameters[i], commandManager) : null;
                } else {
                    parsedArgs[i] = (!def.isEmpty()) ? Optional.of(argumentHandler.get().parse(new BaseCommandArgument(def), parameters[i], commandManager)) : Optional.empty();
                }

            } else if ((userFlag = parameters[i].getAnnotation(UserFlag.class)) != null) {
                CommandArgument present = popFlagAndOperandIfPresent(arguments, userFlag.flag());

                Optional<String> conflictingFlagLookup;
                if (present != null && (conflictingFlagLookup = findConflictingFlag(userFlag.flag(), userFlag.or(), excludedFlags)).isPresent()) {
                    throw new ParseFailException(Arrays.toString(userFlag.flag()), AutoFlag.class, String.format("Provided flag '%s' conflicts with exclusive flag '%s'", present.getRaw(), conflictingFlagLookup.get()));
                }

                String def = (present != null) ? present.getRaw() : userFlag.elsedef();
                if (!parameters[i].isOptional()) {
                    parsedArgs[i] = (!def.isEmpty()) ? argumentHandler.get().parse(new BaseCommandArgument(def), parameters[i], commandManager) : null;
                } else {
                    parsedArgs[i] = (!def.isEmpty()) ? Optional.of(argumentHandler.get().parse(new BaseCommandArgument(def), parameters[i], commandManager)) : Optional.empty();
                }

            } else {
                if (!parameters[i].isOptional()) {
                    parsedArgs[i] = argumentHandler.get().parse(arguments.poll(), parameters[i], commandManager);
                } else {
                    parsedArgs[i] = !arguments.isEmpty() ? Optional.of(argumentHandler.get().parse(arguments.poll(), parameters[i], commandManager)) : Optional.empty();
                }
            }
        }

        if (!arguments.isEmpty()) {
            throw new ParseFailException(arguments.toString(), CommandArgument.class, String.format("Passed %d invalid or previously present argument(s): %s", arguments.size(), arguments.toString()));
        } else {
            return parsedArgs;
        }
    }

    private static CommandArgument popFlagIfPresent(Deque<CommandArgument> arguments, String[] possibilities) {
        for (Iterator<CommandArgument> it = arguments.iterator(); it.hasNext();) {
            CommandArgument arg = it.next();
            for (String option : possibilities) {
                if (arg.getRaw().equals("-" + option)) {
                    it.remove();
                    return arg;
                }
            }
        }
        return null;
    }

    private static CommandArgument popFlagAndOperandIfPresent(Deque<CommandArgument> arguments, String[] possibilities) {
        for (Iterator<CommandArgument> it = arguments.iterator(); it.hasNext();) {
            CommandArgument arg = it.next();
            for (String option : possibilities) {
                if (arg.getRaw().equals("-" + option) && it.hasNext()) {
                    it.remove();
                    CommandArgument operand = it.next();
                    it.remove();
                    return operand;
                }
            }
        }
        return null;
    }

    private static Optional<String> findConflictingFlag(String[] flags, String[] exclusives, Set<String> excluded) {
        Optional<String> result = Optional.empty();
        for (String s : exclusives) {
            if (excluded.contains(s)) {
                result = Optional.of(s);
                break;
            }
        }

        excluded.addAll(Arrays.asList(flags));
        if (exclusives.length > 0) {
            excluded.addAll(Arrays.asList(exclusives));
        }
        return result;
    }
}
