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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.internal.annotations.Length;
import pw.stamina.mandate.internal.execution.argument.ArgumentParseException;
import pw.stamina.mandate.internal.execution.argument.CommandArgumentFactory;
import pw.stamina.mandate.internal.utils.PrimitiveArrays;
import pw.stamina.parsor.api.parsing.ParseException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Foundry
 */
public final class ArrayArgumentHandler implements ArgumentHandler<Object> {

    @Override
    public Object parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        @SuppressWarnings("unchecked")
        Optional<ArgumentHandler> handlerLookup = commandManager.findArgumentHandler((Class) parameter.getType().getComponentType());
        if (!handlerLookup.isPresent()) throw new ArgumentParseException("", parameter.getType().getComponentType(), "");

        List<String> rawComponents = new ArrayList<>();
        StringBuilder rawComponent = new StringBuilder();

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
                    case ']':
                        if (!quoted) {
                            depth--;
                        }
                        rawComponent.append(inputChars[idx]);
                        break;
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
                                throw new ArgumentParseException("", parameter.getType().getComponentType(), "Array element at position " + idx + " is separated by space, but not comma delimited");
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

        Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            int min = Math.min(length.min(), length.max());
            int max = Math.max(length.min(), length.max());
            if (rawComponents.size() < min) {
                throw new ArgumentParseException(input.getRaw(), parameter.getType(), String.format("'%s' is too short: length can be between %d-%d elements", input.getRaw(), min, max));
            } else if (rawComponents.size() > max) {
                throw new ArgumentParseException(input.getRaw(), parameter.getType(), String.format("'%s' is too long: length can be between %d-%d elements", input.getRaw(), min, max));
            }
        }

        Object resultArray = Array.newInstance(parameter.getType().getComponentType(), rawComponents.size());
        for (int i = 0; i < rawComponents.size(); i++) {
            Array.set(resultArray, i, handlerLookup.get().parse(CommandArgumentFactory.newArgument(rawComponents.get(i)), new ArrayProxyCommandParameter(parameter), commandManager));
        }
        return resultArray;
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("Array(%s%s)[length=%d-%d]", parameter.getType().getSimpleName(), String.join("", Collections.nCopies(PrimitiveArrays.getDimensions(parameter.getType()).length, "[]")), Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + String.format("Array(%s%s)", parameter.getType().getSimpleName(), String.join("", Collections.nCopies(PrimitiveArrays.getDimensions(parameter.getType()).length, "[]")));
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Object[].class};
    }

    private static class ArrayProxyCommandParameter implements CommandParameter {
        private final CommandParameter backingParameter;

        ArrayProxyCommandParameter(CommandParameter backingParameter) {
            this.backingParameter = backingParameter;
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
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
        public boolean isOptional() {
            return backingParameter.isOptional();
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

