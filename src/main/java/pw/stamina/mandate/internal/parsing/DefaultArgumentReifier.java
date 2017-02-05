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

package pw.stamina.mandate.internal.parsing;

import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.flag.AutoFlag;
import pw.stamina.mandate.annotations.flag.UserFlag;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.parsing.argument.ArgumentHandler;
import pw.stamina.mandate.parsing.argument.CommandArgument;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.internal.utils.reflect.TypeBuilder;
import pw.stamina.mandate.parsing.ArgumentReificationException;
import pw.stamina.mandate.parsing.ArgumentReificationStrategy;
import pw.stamina.mandate.parsing.InputParsingException;

import java.util.*;

/**
 * @author Mark Johnson
 */
public enum DefaultArgumentReifier implements ArgumentReificationStrategy {
    INSTANCE;

    public Object[] parse(final Deque<CommandArgument> arguments, final List<CommandParameter> parameters, final ExecutionContext executionContext, final CommandContext commandContext) throws InputParsingException {
        final List<Object> parsedArgs = new ArrayList<>(parameters.size());
        final Set<String> excludedFlags = new HashSet<>();

        for (final CommandParameter parameter : parameters) {
            if (parameter.getAnnotation(Implicit.class) == null) {
                final ArgumentHandler<?> argumentHandler = commandContext.getArgumentHandlers().findArgumentHandler((Class<?>) parameter.getType())
                        .orElseThrow(() -> new ArgumentReificationException(String.format("No argument handler exists for argument parameter type '%s'", parameter.getType().getCanonicalName())));

                final AutoFlag autoFlag;
                final UserFlag userFlag;
                if ((autoFlag = parameter.getAnnotation(AutoFlag.class)) != null) {
                    final CommandArgument present = popFlagIfPresent(arguments, autoFlag.flag());

                    final Optional<String> conflictingFlagLookup;
                    if (present != null && (conflictingFlagLookup = findConflictingFlag(autoFlag.flag(), autoFlag.xor(), excludedFlags)).isPresent()) {
                        throw new ArgumentReificationException(String.format("Provided flag '%s' conflicts with exclusive flag '%s'", present.getRaw(), conflictingFlagLookup.get()));
                    }

                    final String def = (parameter.getType() == Boolean.class || parameter.getType() == Boolean.TYPE)
                            ? present != null ? "true" : "false" : present != null ? autoFlag.ifdef()
                            : autoFlag.elsedef();
                    if (!parameter.isOptional()) {
                        parsedArgs.add((!def.isEmpty()) ? argumentHandler.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(def), parameter, commandContext) : null);
                    } else {
                        parsedArgs.add((!def.isEmpty()) ? Optional.of(argumentHandler.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(def), parameter, commandContext)) : Optional.empty());
                    }

                } else if ((userFlag = parameter.getAnnotation(UserFlag.class)) != null) {
                    final CommandArgument present = popFlagAndOperandIfPresent(arguments, userFlag.flag());

                    final Optional<String> conflictingFlagLookup;
                    if (present != null && (conflictingFlagLookup = findConflictingFlag(userFlag.flag(), userFlag.xor(), excludedFlags)).isPresent()) {
                        throw new ArgumentReificationException(String.format("Provided flag '%s' conflicts with exclusive flag '%s'", present.getRaw(), conflictingFlagLookup.get()));
                    }

                    final String def = (present != null) ? present.getRaw() : userFlag.elsedef();
                    if (!parameter.isOptional()) {
                        parsedArgs.add((!def.isEmpty()) ? argumentHandler.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(def), parameter, commandContext) : null);
                    } else {
                        parsedArgs.add((!def.isEmpty()) ? Optional.of(argumentHandler.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(def), parameter, commandContext)) : Optional.empty());
                    }

                } else {
                    if (!parameter.isOptional()) {
                        parsedArgs.add(argumentHandler.parse(arguments.poll(), parameter, commandContext));
                    } else {
                        parsedArgs.add(!arguments.isEmpty() ? Optional.of(argumentHandler.parse(arguments.poll(), parameter, commandContext)) : Optional.empty());
                    }
                }
            } else {
                final Object implicitLookup = executionContext.getProvidedValue(TypeBuilder.from(parameter.getType(), parameter.getTypeParameters()));
                if (implicitLookup != null) {
                    parsedArgs.add(implicitLookup);
                } else {
                    throw new ArgumentReificationException(String.format("No mapped instance present for implicit parameters of type %s", parameter.getType()));
                }
            }
        }


        if (!arguments.isEmpty()) {
            throw new ArgumentReificationException(String.format("Passed %d invalid or previously present argument(s): %s", arguments.size(), arguments.toString()));
        } else {
            return parsedArgs.toArray();
        }
    }

    private static CommandArgument popFlagIfPresent(final Deque<CommandArgument> arguments, final String[] possibilities) {
        for (final Iterator<CommandArgument> it = arguments.iterator(); it.hasNext();) {
            final CommandArgument arg = it.next();
            for (final String option : possibilities) {
                if (arg.getRaw().equals("-" + option)) {
                    it.remove();
                    return arg;
                }
            }
        }
        return null;
    }

    private static CommandArgument popFlagAndOperandIfPresent(final Deque<CommandArgument> arguments, final String[] possibilities) {
        for (final Iterator<CommandArgument> it = arguments.iterator(); it.hasNext();) {
            final CommandArgument arg = it.next();
            for (final String option : possibilities) {
                if (arg.getRaw().equals("-" + option) && it.hasNext()) {
                    it.remove();
                    final CommandArgument operand = it.next();
                    it.remove();
                    return operand;
                }
            }
        }
        return null;
    }

    private static Optional<String> findConflictingFlag(final String[] flags, final String[] exclusives, final Set<String> excluded) {
        Optional<String> result = Optional.empty();
        for (final String s : exclusives) {
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

    public static DefaultArgumentReifier getInstance() {
        return INSTANCE;
    }
}
