/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Mark Johnson
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

package pw.stamina.mandate.internal.execution.parameter;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.flag.AutoFlag;
import pw.stamina.mandate.annotations.flag.UserFlag;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.execution.parameter.CommandParameterCreationStrategy;
import pw.stamina.mandate.internal.utils.GenericResolver;
import pw.stamina.mandate.internal.utils.PrimitiveArrays;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public enum DefaultCommandParameterFactory implements CommandParameterCreationStrategy {
    INSTANCE;

    public List<CommandParameter> generateCommandParameters(final Method backingMethod, final CommandContext commandContext) throws UnsupportedParameterException {
        final Set<String> usedFlags = new HashSet<>();
        final boolean[] reachedOptionals = {false};
        final boolean[] reachedRequired = {false};
        return Arrays.stream(backingMethod.getParameters()).map(parameter -> {
            if (!parameter.isAnnotationPresent(Implicit.class)) {
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
                        type = (Class<?>) GenericResolver.typeParametersOf(parameter.getParameterizedType())[0];

                } else if (type == Optional.class) {
                    reachedOptionals[0] = true;
                    type = (Class<?>) GenericResolver.typeParametersOf(parameter.getParameterizedType())[0];

                } else {
                    if (reachedOptionals[0]) {
                        throw new UnsupportedParameterException("Parameter " + parameter.getName()
                                + " in method " + backingMethod.getName()
                                + " is mandatory, but exists after optional parameters");
                    }
                    reachedRequired[0] = true;
                }

                if (!commandContext.getArgumentHandlers().findArgumentHandler(type).isPresent()) {
                    throw new UnsupportedParameterException(String.format("%s is not a supported parameter type", type.getCanonicalName()));
                } else if (type.isArray() && !commandContext.getArgumentHandlers().findArgumentHandler((PrimitiveArrays.getBaseComponentType(type.getComponentType()))).isPresent()) {
                    throw new UnsupportedParameterException(String.format("Array element %s is not a supported parameter type", type.getCanonicalName()));
                }

                return new DeclaredCommandParameter(parameter, type);
            } else {
                return new DeclaredCommandParameter(parameter, parameter.getType());
            }
        }).collect(Collectors.toList());
    }

    public static DefaultCommandParameterFactory getInstance() {
        return INSTANCE;
    }
}
