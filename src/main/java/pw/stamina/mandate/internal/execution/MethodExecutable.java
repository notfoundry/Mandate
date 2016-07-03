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

package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.exceptions.MalformedCommandException;
import pw.stamina.mandate.api.exceptions.UnsupportedParameterException;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.parameter.CommandParameter;
import pw.stamina.mandate.api.execution.result.CommandResult;
import pw.stamina.mandate.internal.execution.parameter.DeclaredCommandParameter;
import pw.stamina.mandate.internal.execution.result.ResultFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Foundry
 */
public class MethodExecutable implements CommandExecutable {
    private final Method backingMethod;
    private final Object methodParent;
    private final CommandManager commandManager;
    private final List<CommandParameter> parameters;

    public MethodExecutable(Method backingMethod, Object methodParent, CommandManager commandManager) throws MalformedCommandException {
        if (!(this.commandManager = commandManager).findOutputParser(backingMethod.getReturnType()).isPresent()) {
            throw new MalformedCommandException("Unsupported method return type " + backingMethod.getReturnType().getCanonicalName());
        }

        int[] index = new int[1];
        this.parameters = Arrays.stream((this.backingMethod = backingMethod).getParameters()).map(parameter -> {
            Class<?> type = parameter.getType();
            if (type == Optional.class) {
                Type generic = backingMethod.getGenericParameterTypes()[index[0]];
                if (generic instanceof ParameterizedType) {
                    type = (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[0];
                } else {
                    throw new UnsupportedParameterException("failed to resolve argument type for parameter " + parameter.getName());
                }
            }
            if (!commandManager.findArgumentHandler(type).isPresent()) {
                throw new UnsupportedParameterException(String.format("%s is not a supported parameter type.", type.getCanonicalName()));
            }
            index[0]++;
            return new DeclaredCommandParameter(parameter, type);
        }).collect(Collectors.toList());

        this.methodParent = methodParent;
        this.backingMethod.setAccessible(true);
    }

    @Override
    public Optional<CommandResult> execute(Object... arguments) {
        try {
            final Object output = backingMethod.invoke(methodParent, arguments);
            return Optional.ofNullable(output).flatMap(o -> commandManager.findOutputParser((Class<Object>) o.getClass())).map(parser -> Optional.of(parser.generateResult(output)))
                    .orElse(Optional.of(ResultFactory.immediate("Unable to parse output of backing method " + backingMethod.getName(), CommandResult.Status.FAILED)));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of(ResultFactory.immediate("Exception encountered while executing backing method " + backingMethod.getName(), CommandResult.Status.TERMINATED));
        }
    }

    @Override
    public List<CommandParameter> getParameters() {
        return parameters;
    }

    @Override
    public int minimumArguments() {
        return (int) parameters.stream().filter(param -> !param.isOptional()).count();
    }

    @Override
    public int maximumArguments() {
        return parameters.size();
    }

    @Override
    public String toString() {
        return String.format("MethodExecutable{name=%s, parameters=%s}", backingMethod.getName(), parameters);
    }
}
