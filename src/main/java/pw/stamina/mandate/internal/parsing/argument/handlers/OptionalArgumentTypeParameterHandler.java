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
import pw.stamina.mandate.internal.parsing.argument.ArgumentParsingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class OptionalArgumentTypeParameterHandler implements ArgumentHandler<Optional<?>> {
    @Override
    public Optional<?> parse(final CommandArgument input, final CommandParameter parameter, final CommandContext commandContext) throws InputParsingException {
        final Type[] typeParameters = parameter.getTypeParameters();

        final ArgumentHandler<?> handlerLookup = commandContext.getArgumentHandlers().findArgumentHandler(reifyType(typeParameters[0]))
                .orElseThrow(() -> new ArgumentParsingException(String.format("%s is not a supported parameter type", typeParameters[0])));

        final Type[] resolutionTypes = getTypeParameters(typeParameters[0]);

        return Optional.ofNullable(handlerLookup.parse(
                commandContext.getCommandConfiguration().getArgumentCreationStrategy().newArgument(input.getRaw()),
                new OptionalTypeParameter(parameter, resolutionTypes),
                commandContext));
    }

    @Override
    public String getSyntax(final CommandParameter parameter) {
        throw new UnsupportedOperationException("Type parameters cannot be used as method parameters");
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Optional.class};
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

    private static class OptionalTypeParameter implements CommandParameter {

        private final CommandParameter backingParameter;

        private final Type[] typeParameters;

        OptionalTypeParameter(final CommandParameter backingParameter, final Type... typeParameters) {
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
            return "An Optional type parameter described as \"" + backingParameter.getDescription() + "\"";
        }

        @Override
        public String getLabel() {
            return backingParameter.getLabel();
        }
    }
}
