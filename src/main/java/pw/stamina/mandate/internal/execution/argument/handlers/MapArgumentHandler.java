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

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.parsing.InputParsingException;
import pw.stamina.mandate.internal.annotations.Length;
import pw.stamina.mandate.internal.execution.argument.ArgumentParsingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Foundry
 */
public class MapArgumentHandler implements ArgumentHandler<Map<?, ?>> {
    @Override
    public Map<?, ?> parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {

        final Type[] typeParameters = parameter.getTypeParameters();

//        Early resolution to make sure we aren't doing unnecessary parsing for an unsupported result
        final ArgumentHandler<?> keyHandlerLookup = commandContext.getArgumentHandlers().findArgumentHandler(reifyType(typeParameters[0]))
                .orElseThrow(() -> new ArgumentParsingException(String.format("%s is not a supported parameter type", typeParameters[0])));;
        final ArgumentHandler<?> valueHandlerLookup = commandContext.getArgumentHandlers().findArgumentHandler(reifyType(typeParameters[1]))
                .orElseThrow(() -> new ArgumentParsingException(String.format("%s is not a supported parameter type", typeParameters[1])));

        final Map<String, String> rawComponents = new LinkedHashMap<>();
        final StringBuilder rawKey = new StringBuilder(input.getRaw().length());
        final StringBuilder rawValue = new StringBuilder(input.getRaw().length());

        ParsingStage currentStage = ParsingStage.KEY;

        char[] inputChars; boolean escaped = false, quoted = false; int depth = 0;
        for (int idx = 1; idx < (inputChars = input.getRaw().toCharArray()).length - 1; idx++) {
            if (escaped) {
                switch (currentStage) {
                    case KEY: {
                        rawKey.append(inputChars[idx]);
                        break;
                    }
                    case VALUE: {
                        rawValue.append(inputChars[idx]);
                        break;
                    }
                }
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
                        switch (currentStage) {
                            case KEY: {
                                rawKey.append(inputChars[idx]);
                                break;
                            }
                            case VALUE: {
                                rawValue.append(inputChars[idx]);
                                break;
                            }
                        }
                        break;
                    case '{':    //fall through
                    case '[':
                        if (!quoted) {
                            depth++;
                        }
                        switch (currentStage) {
                            case KEY: {
                                rawKey.append(inputChars[idx]);
                                break;
                            }
                            case VALUE: {
                                rawValue.append(inputChars[idx]);
                                break;
                            }
                        }
                        break;
                    case '-': {
                        if (!quoted && depth == 0) {
                            if (inputChars[idx + 1] == '>') {
                                if (currentStage == ParsingStage.KEY) {
                                    currentStage = ParsingStage.VALUE;
                                    idx++;
                                }
                            }
                        } else {
                            switch (currentStage) {
                                case KEY: {
                                    rawKey.append(inputChars[idx]);
                                    break;
                                }
                                case VALUE: {
                                    rawValue.append(inputChars[idx]);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case ',': {
                        if (!quoted && depth == 0) {
                            while (inputChars[idx+1] == ' ') {
                                idx++;
                            }
                            if (rawKey.length() > 0 && rawValue.length() > 0) {
                                rawComponents.put(rawKey.toString(), rawValue.toString());
                                rawKey.setLength(0);
                                rawValue.setLength(0);
                                currentStage = ParsingStage.KEY;
                            }
                            break;
                        }
                    }
                    case ' ': {
                        if (!quoted && depth == 0) {
                            if (inputChars[idx + 1] != '-' && inputChars[idx - 1] != '>') {
                                idx++;
                            }
                            break;
                        }
                    }
                    default: {
                        switch (currentStage) {
                            case KEY: {
                                rawKey.append(inputChars[idx]);
                                break;
                            }
                            case VALUE: {
                                rawValue.append(inputChars[idx]);
                                break;
                            }
                        }
                        escaped = false;
                    }
                }
            }
        }

        if (rawKey.length() > 0 && rawValue.length() > 0) {
            rawComponents.put(rawKey.toString(), rawValue.toString());
            rawKey.setLength(0);
            rawValue.setLength(0);
        }

        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            final int min = Math.min(length.min(), length.max());
            final int max = Math.max(length.min(), length.max());
            if (rawComponents.size() < min) {
                throw new ArgumentParsingException(String.format("'%s' is too short: length can be between %d-%d mappings", input.getRaw(), min, max));
            } else if (rawComponents.size() > max) {
                throw new ArgumentParsingException(String.format("'%s' is too long: length can be between %d-%d mappings", input.getRaw(), min, max));
            }
        }

        final Type[] keyResolutionTypes = getTypeParameters(typeParameters[0]);
        final Type[] valueResolutionTypes = getTypeParameters(typeParameters[1]);
        return rawComponents.entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(
                keyHandlerLookup.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(entry.getKey()), new MapProxyCommandParameter(parameter, keyResolutionTypes), commandContext),
                valueHandlerLookup.parse(commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(entry.getValue()), new MapProxyCommandParameter(parameter, valueResolutionTypes), commandContext)
        )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("Map<%s -> %s>[length=%d-%d]", parameter.getTypeParameters()[0].getTypeName(), parameter.getTypeParameters()[1].getTypeName(), Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + String.format("Map<%s -> %s>", parameter.getTypeParameters()[0].getTypeName(), parameter.getTypeParameters()[1].getTypeName());
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Map.class};
    }

    private static Type[] getTypeParameters(final Type initialType) {
        return (initialType instanceof ParameterizedType) ? ((ParameterizedType) initialType).getActualTypeArguments() : new Type[] {initialType};
    }

    private static Class<?> reifyType(final Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return (Class<?>) type;
        }
    }

    private static class MapProxyCommandParameter implements CommandParameter {

        private final CommandParameter backingParameter;

        private final Type[] typeParameters;

        MapProxyCommandParameter(final CommandParameter backingParameter, final Type... typeParameters) {
            this.backingParameter = backingParameter;
            this.typeParameters = typeParameters;
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
            return (Class<?>) typeParameters[0];
        }

        @Override
        public Type[] getTypeParameters() {
            return typeParameters;
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
            return "An entry in a Map described as \"" + backingParameter.getDescription() + "\"";
        }

        @Override
        public String getLabel() {
            return backingParameter.getLabel();
        }
    }

    private enum ParsingStage {
        KEY, VALUE
    }
}
