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
import pw.stamina.mandate.internal.parsing.argument.ArgumentParsingException;
import pw.stamina.mandate.internal.utils.PrimitiveArrays;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mark Johnson
 */
public final class ArrayArgumentHandler implements ArgumentHandler<Object> {

    @Override
    public Object parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        final ArgumentHandler<?> handlerLookup = commandContext.getArgumentHandlers().findArgumentHandler((Class<?>) (Class) parameter.getType().getComponentType())
                .orElseThrow(() -> new ArgumentParsingException(String.format("%s is not a supported parameter type", parameter.getType().getComponentType())));

        final List<String> rawComponents = new ArrayList<>();
        final StringBuilder rawComponent = new StringBuilder(input.getRaw().length());

        char[] inputChars; boolean escaped = false, quoted = false; int depth = 0;
        for (int idx = 1; idx < (inputChars = input.getRaw().toCharArray()).length - 1; idx++) {
            if (escaped) {
                rawComponent.append(inputChars[idx]);
                escaped = false;
            } else {
                switch (inputChars[idx]) {
                    case '\\': {
                        escaped = true;
                        break;
                    }
                    case '"': {
                        quoted = !quoted;
                        break;
                    }
                    case '}':   //fall through
                    case ']':
                        if (!quoted) {
                            depth--;
                        }
                        rawComponent.append(inputChars[idx]);
                        break;
                    case '{':    //fall through
                    case '[':
                        if (!quoted) {
                            depth++;
                        }
                        rawComponent.append(inputChars[idx]);
                        break;
                    case ',': {
                        if (!quoted && depth == 0) {
                            while (inputChars[idx+1] == ' ') {
                                idx++;
                            }
                            if (rawComponent.length() > 0) {
                                rawComponents.add(rawComponent.toString());
                                rawComponent.setLength(0);
                            }
                            break;
                        }
                    }
                    case ' ': {
                        if (!quoted && depth == 0) {
                            if (inputChars[idx - 1] != ' ' && inputChars[idx - 1] != ',') {
                                throw new ArgumentParsingException("Array element at position " + idx + " is separated by space, but not comma delimited");
                            } else {
                                idx++;
                                break;
                            }
                        }
                    }
                    default: {
                        rawComponent.append(inputChars[idx]);
                        escaped = false;
                    }
                }
            }

        }
        if (rawComponent.length() > 0) {
            rawComponents.add(rawComponent.toString());
            rawComponent.setLength(0);
        }

        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            final int min = Math.min(length.min(), length.max());
            final int max = Math.max(length.min(), length.max());
            if (rawComponents.size() < min) {
                throw new ArgumentParsingException(String.format("'%s' is too short: length can be between %d-%d elements", input.getRaw(), min, max));
            } else if (rawComponents.size() > max) {
                throw new ArgumentParsingException(String.format("'%s' is too long: length can be between %d-%d elements", input.getRaw(), min, max));
            }
        }

        final Object resultArray = Array.newInstance(parameter.getType().getComponentType(), rawComponents.size());
        for (int i = 0; i < rawComponents.size(); i++) {
            Array.set(resultArray, i, handlerLookup.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(rawComponents.get(i)), new ArrayProxyCommandParameter(parameter), commandContext));
        }
        return resultArray;
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("Array(%s%s)[length=%d-%d]", parameter.getType().getComponentType().getSimpleName(), String.join("", Collections.nCopies(PrimitiveArrays.getDimensions(parameter.getType()).length, "[]")), Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + String.format("Array(%s%s)", parameter.getType().getComponentType().getSimpleName(), String.join("", Collections.nCopies(PrimitiveArrays.getDimensions(parameter.getType()).length, "[]")));
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Object[].class};
    }

    private static class ArrayProxyCommandParameter implements CommandParameter {
        private final CommandParameter backingParameter;

        ArrayProxyCommandParameter(final CommandParameter backingParameter) {
            this.backingParameter = backingParameter;
        }

        @Override
        public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
            return backingParameter.getAnnotation(annotationClass);
        }

        @Override
        public Annotation[] getAnnotations() {
            return backingParameter.getAnnotations();
        }

        @Override
        public Class<?> getType() {
            return backingParameter.getType().getComponentType();
        }

        @Override
        public Type[] getTypeParameters() {
            return new Type[0]; //arrays cannot have type parameters
        }

        @Override
        public boolean isOptional() {
            return backingParameter.isOptional();
        }

        @Override
        public boolean isImplicit() {
            return backingParameter.isImplicit();
        }

        @Override
        public String getDescription() {
            return "An element in an array described as \"" + backingParameter.getDescription() + "\"";
        }

        @Override
        public String getLabel() {
            return backingParameter.getLabel();
        }
    }

}

