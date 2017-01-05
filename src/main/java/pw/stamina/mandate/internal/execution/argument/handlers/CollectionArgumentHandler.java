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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.annotations.Length;
import pw.stamina.mandate.internal.execution.argument.ArgumentParsingException;
import pw.stamina.mandate.parsing.InputParsingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public class CollectionArgumentHandler implements ArgumentHandler<Collection<?>> {
    @Override
    public Collection<?> parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        final Type[] typeParameters = parameter.getTypeParameters();

//        Early resolution to make sure we aren't doing unnecessary parsing for an unsupported result
        final ArgumentHandler<?> handlerLookup = commandContext.getArgumentHandlers().findArgumentHandler(reifyType(typeParameters[0]))
                .orElseThrow(() -> new ArgumentParsingException(String.format("%s is not a supported parameter type", typeParameters[0])));;

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
                                throw new ArgumentParsingException("Collection element at position " + idx + " is separated by space, but not comma delimited");
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

        final Type[] resolutionTypes = getTypeParameters(typeParameters[0]);
        return rawComponents.stream()
                .map(component -> (Object) handlerLookup.parse(
                        commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(component),
                        new CollectionProxyCommandParameter(parameter, resolutionTypes),
                        commandContext)
                ).collect(Collectors.toList());
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        final Length length = parameter.getAnnotation(Length.class);
        if (length != null) {
            return parameter.getLabel() + " - " + String.format("Collection<%s>[length=%d-%d]", parameter.getTypeParameters()[0].getTypeName(), Math.min(length.min(), length.max()), Math.max(length.min(), length.max()));
        }
        return parameter.getLabel() + " - " + String.format("Collection<%s>", parameter.getTypeParameters()[0].getTypeName());
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Collection.class};
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

    private static class CollectionProxyCommandParameter implements CommandParameter {

        private final CommandParameter backingParameter;

        private final Type[] typeParameters;

        CollectionProxyCommandParameter(final CommandParameter backingParameter, final Type... typeParameters) {
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
            return "An element in a Collection described as \"" + backingParameter.getDescription() + "\"";
        }

        @Override
        public String getLabel() {
            return backingParameter.getLabel();
        }
    }
}
