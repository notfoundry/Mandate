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

import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.meta.Usage;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.internal.utils.GenericResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Mark Johnson
 */
class DeclaredCommandParameter implements CommandParameter {

    private final Parameter parameter;

    private final Class type;

    DeclaredCommandParameter(final Parameter parameter, final Class type) {
        this.parameter = parameter;
        this.type = type;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return this.parameter.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.parameter.getAnnotations();
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public boolean isOptional() {
        return this.parameter.getType() == Optional.class;
    }

    @Override
    public boolean isImplicit() {
        return getAnnotation(Implicit.class) != null;
    }

    @Override
    public String getDescription() {
        final Usage usage = getAnnotation(Usage.class);
        return usage != null ? usage.usage() : "";
    }

    @Override
    public String getLabel() {
        final Usage usage = getAnnotation(Usage.class);
        return usage != null ? usage.name() : parameter.getName();
    }

    @Override
    public Type[] getTypeParameters() {
        return GenericResolver.typeParametersOf(parameter.getParameterizedType());
    }

    @Override
    public String toString() {
        return String.format("DeclaredCommandParameter{name=%s, type=%s, optional=%s, implicit=%s, annotations=%s}", getLabel(), getType().getCanonicalName(), isOptional(), isImplicit(), Arrays.toString(getAnnotations()));
    }
}
