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

package pw.stamina.mandate.internal;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.internal.annotations.Executes;
import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.exceptions.ArgumentParsingException;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.result.CommandResult;
import pw.stamina.mandate.api.execution.result.ExecutableResultHandler;
import pw.stamina.mandate.internal.component.SyntaxComponentFactory;
import pw.stamina.mandate.internal.execution.argument.handlers.NumberArgumentHandler;
import pw.stamina.mandate.internal.execution.argument.handlers.StringArgumentHandler;
import pw.stamina.mandate.internal.execution.result.ResultFactory;
import pw.stamina.mandate.internal.execution.result.handlers.NumberResultHandler;
import pw.stamina.mandate.internal.execution.result.handlers.StringResultHandler;
import pw.stamina.mandate.internal.parsing.InputTokenizer;
import pw.stamina.mandate.internal.utils.Primitives;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Foundry
 */
public class AnnotatedCommandManager implements CommandManager {
    private final Map<String, SyntaxComponent> registeredCommands = new LinkedHashMap<>();

    private final Set<ExecutableResultHandler> executableResultHandlers = new HashSet<>();

    private final Set<ArgumentHandler> argumentHandlers = new HashSet<>();

    public AnnotatedCommandManager() {
        Arrays.asList(
                new StringResultHandler(),
                new NumberResultHandler()
        ).forEach(executableResultHandlers::add);

        Arrays.asList(
                new StringArgumentHandler(),
                new NumberArgumentHandler()
        ).forEach(argumentHandlers::add);
    }

    @Override
    public boolean register(Object container) {
        boolean registered = false;
        for (Method method : container.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Executes.class)) {
                registered |= register0(method, container);
            }
        }
        return registered;
    }

    public Optional<CommandResult> execute(String input) {
        if (input == null) {
            return Optional.of(ResultFactory.immediate("Invalid input: input cannot be null", CommandResult.Status.FAILED));
        } else if (input.length() == 0) {
            return Optional.of(ResultFactory.immediate("Invalid input: input cannot be empty", CommandResult.Status.FAILED));
        }

        List<CommandArgument> consumedArgs = new ArrayList<>();

        Deque<CommandArgument> arguments; SyntaxComponent component; CommandArgument currentArgument; int depth = 0;
        if ((component = registeredCommands.get((currentArgument = (arguments = InputTokenizer.getInstance().tokenize(input)).getFirst()).getArgument())) != null) {
            while ((currentArgument = arguments.poll()) != null) {
                consumedArgs.add(currentArgument);
                if (component.findExecutables().isPresent()) {
                    int lowestConsumed = Integer.MAX_VALUE; ArgumentParsingException lastException = null;
                    for (CommandExecutable executable : component.findExecutables().get()) {
                        if (arguments.size() >= executable.minimumArguments() && arguments.size() <= executable.maximumArguments()) {
                            try {
                                return executable.execute(arguments);
                            } catch (ArgumentParsingException e) {
                                lastException = e;
                            }
                        } else {
                            lowestConsumed = lowestConsumed > executable.minimumArguments() ? executable.minimumArguments() : lowestConsumed;
                        }
                    }
                    if (lastException != null) return Optional.of(ResultFactory.immediate(lastException.getLocalizedMessage(), CommandResult.Status.FAILED));
                    if (lowestConsumed != Integer.MAX_VALUE) depth += lowestConsumed;
                }
                if (!arguments.isEmpty() && component.getChild(arguments.getFirst().getArgument()) != null) {
                    depth++;
                    component = component.getChild(arguments.getFirst().getArgument());
                } else {
                    consumedArgs.addAll(arguments);
                    if (++depth <= consumedArgs.size()) {
                        return Optional.of(ResultFactory.immediate(String.format("Invalid argument(s) '%s' passed to command '%s'", consumedArgs.subList(depth, consumedArgs.size()), consumedArgs.subList(0, depth)), CommandResult.Status.FAILED));
                    } else {
                        return Optional.of(ResultFactory.immediate(String.format("Missing %d argument(s) for command '%s'", depth - consumedArgs.size(), consumedArgs), CommandResult.Status.FAILED));
                    }
                }
            }
            return Optional.empty();
        } else {
            return Optional.of(ResultFactory.immediate(String.format("'%s' is not a valid command", currentArgument), CommandResult.Status.FAILED));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type) {
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        }
        for (ArgumentHandler argumentHandler : argumentHandlers) {
            for (Class handledType : argumentHandler.getHandledTypes()) {
                if (handledType.isAssignableFrom(type)) {
                    return Optional.of(argumentHandler);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ExecutableResultHandler<T>> findOutputParser(Class<T> type) {
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        }
        for (ExecutableResultHandler resultHandler : executableResultHandlers) {
            for (Class handledType : resultHandler.getHandledTypes()) {
                if (handledType.isAssignableFrom(type)) {
                    return Optional.of(resultHandler);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<SyntaxComponent> getCommands() {
        return Collections.unmodifiableCollection(registeredCommands.values());
    }

    private boolean register0(Method backingMethod, Object container) {
        String syntax; SyntaxComponent old;
        for (SyntaxComponent component : SyntaxComponentFactory.getComponents(backingMethod, container, this)) {
            if ((old = registeredCommands.get((syntax = component.getSyntax()))) == null) {
                registeredCommands.put(syntax, component);
            } else {
                mergeSyntaxComponent(component, old);
            }
        }
        return true;
    }

    private static void mergeSyntaxComponent(SyntaxComponent newComponent, SyntaxComponent oldComponent) {
        SyntaxComponent lookup;
        newComponent.findExecutables().ifPresent(set -> set.forEach(oldComponent::addExecutable));
        if (newComponent.findChildren().isPresent()) {
            for (SyntaxComponent component : newComponent.findChildren().get()) {
                if ((lookup = oldComponent.getChild(component.getSyntax())) != null) {
                    mergeSyntaxComponent(component, lookup);
                } else {
                    oldComponent.addChild(component);
                }
            }
        }
    }
}
