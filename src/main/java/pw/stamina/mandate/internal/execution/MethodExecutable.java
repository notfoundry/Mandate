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
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.api.annotations.flag.UserFlag;
import pw.stamina.mandate.api.annotations.meta.Description;
import pw.stamina.mandate.internal.exceptions.MalformedCommandException;
import pw.stamina.mandate.internal.exceptions.UnsupportedParameterException;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.internal.execution.parameter.DeclaredCommandParameter;
import pw.stamina.mandate.internal.parsing.ArgumentToObjectParser;
import pw.stamina.parsor.exceptions.ParseException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Foundry
 */
public class MethodExecutable implements CommandExecutable {
    private final Method backingMethod;
    private final Object methodParent;
    private final CommandManager commandManager;
    private final List<CommandParameter> parameters;
    private ArgumentToObjectParser argumentParser;

    public MethodExecutable(Method backingMethod, Object methodParent, CommandManager commandManager) throws MalformedCommandException {
        if (backingMethod.getReturnType() != ExitCode.class) {
            throw new MalformedCommandException("Annotated method '" + backingMethod.getName() + "' does have a return type of " + ExitCode.class.getCanonicalName());
        } else if (backingMethod.getParameterCount() == 0 || backingMethod.getParameterTypes()[0] != IODescriptor.class) {
            throw new MalformedCommandException("Annotated method '" + backingMethod.getName() + "' does not have a first parameter of type " + IODescriptor.class.getCanonicalName());
        }

        this.parameters = generateCommandParameters((this.backingMethod = backingMethod), (this.commandManager = commandManager));
        this.methodParent = methodParent;
        this.backingMethod.setAccessible(true);
    }

    @Override
    public Execution execute(Deque<CommandArgument> arguments, IODescriptor io) throws ParseException {
        final Object[] parsedArgs = (argumentParser == null ? (argumentParser = new ArgumentToObjectParser(this, commandManager)) : argumentParser).parse(arguments);
        return ExecutionFactory.makeExecution(backingMethod, methodParent, arrayConcat(new Object[] {io}, parsedArgs));
    }

    @Override
    public List<CommandParameter> getParameters() {
        return parameters;
    }

    @Override
    public String getDescription() {
        Description description = backingMethod.getDeclaredAnnotation(Description.class);
        return (description != null) ? String.join(System.lineSeparator(), description.value()) : "";
    }

    @Override
    public int minimumArguments() {
        return (int) parameters.stream()
                .filter(param -> param.getAnnotation(AutoFlag.class) == null && param.getAnnotation(UserFlag.class) == null)
                .filter(param -> !param.isOptional())
                .count();
    }

    @Override
    public int maximumArguments() {
        return parameters.size() + (int) parameters.stream()
                .filter(param -> param.getAnnotation(UserFlag.class) != null)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodExecutable that = (MethodExecutable) o;
        return this.minimumArguments() == that.minimumArguments() && this.maximumArguments() == that.maximumArguments();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + minimumArguments();
        result = 31 * result + maximumArguments();
        return result;
    }

    @Override
    public String toString() {
        return String.format("MethodExecutable{name=%s, parameters=%s}", backingMethod.getName(), parameters);
    }

    private static List<CommandParameter> generateCommandParameters(Method backingMethod, CommandManager commandManager) throws UnsupportedParameterException {
        if (backingMethod.getParameterCount() > 1) {
            Set<String> usedFlags = new HashSet<>();
            boolean[] reachedOptionals = {false}, reachedRequired = {false};
            return Arrays.stream(backingMethod.getParameters(), 1, backingMethod.getParameterCount()).map(parameter -> {
                Class<?> type = parameter.getType();
                AutoFlag autoFlag = parameter.getDeclaredAnnotation(AutoFlag.class);
                UserFlag userFlag = parameter.getDeclaredAnnotation(UserFlag.class);

                if (autoFlag != null || userFlag != null) {
                    if (autoFlag != null && userFlag != null) {
                        throw new UnsupportedParameterException("Parameter " + parameter.getName()
                                + " in method " + backingMethod.getName()
                                + " is annotated as both an automatic and operand-based flag");
                    } else if ((reachedRequired[0] || reachedOptionals[0])) {
                        throw new UnsupportedParameterException("Parameter " + parameter.getName()
                                + " in method " + backingMethod.getName()
                                + " is annotated as flag, but exists after non-flag parameters");
                    }
                    for (String flag : (autoFlag != null ? autoFlag.flag() : userFlag.flag())) {
                        if (!usedFlags.add(flag)) {
                            throw new UnsupportedParameterException("Parameter " + parameter.getName()
                                    + " in method " + backingMethod.getName()
                                    + " uses previously declared flag name '" + flag + "'");
                        }
                    }
                    if (type == Optional.class)
                        type = resolveGenericType(parameter);

                } else if (type == Optional.class) {
                    reachedOptionals[0] = true;
                    type = resolveGenericType(parameter);

                } else {
                    if (reachedOptionals[0]) {
                        throw new UnsupportedParameterException("Parameter " + parameter.getName()
                                + " in method " + backingMethod.getName()
                                + " is mandatory, but exists after optional parameters");
                    }
                    reachedRequired[0] = true;
                }

                if (!commandManager.findArgumentHandler(type).isPresent()) {
                    throw new UnsupportedParameterException(String.format("%s is not a supported parameter type", type.getCanonicalName()));
                }

                return new DeclaredCommandParameter(parameter, type);
            }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private static Class<?> resolveGenericType(Parameter parameter) throws UnsupportedParameterException {
        final Type generic = parameter.getParameterizedType();
        if (generic instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[0];
        } else {
            throw new UnsupportedParameterException("failed to resolve argument type for optional parameter " + parameter.getName());
        }
    }

    private static Object[] arrayConcat(Object[] first, Object[] second) {
        Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
