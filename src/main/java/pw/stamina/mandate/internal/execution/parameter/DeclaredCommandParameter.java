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

package pw.stamina.mandate.internal.execution.parameter;

import pw.stamina.mandate.api.annotations.meta.Usage;
import pw.stamina.mandate.api.execution.CommandParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Foundry
 */
public class DeclaredCommandParameter implements CommandParameter {
    private final Parameter parameter;
    private final Class type;

    public DeclaredCommandParameter(Parameter parameter, Class type) {
        this.parameter = parameter;
        this.type = type;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
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
    public String getDescription() {
        Usage usage = getAnnotation(Usage.class);
        return usage != null ? usage.usage() : "";
    }

    @Override
    public String getLabel() {
        Usage usage = getAnnotation(Usage.class);
        return usage != null ? usage.name() : parameter.getName();
    }

    @Override
    public String toString() {
        return String.format("DeclaredCommandParameter{name=%s, type=%s, optional=%s, annotations=%s}", getLabel(), getType().getCanonicalName(), isOptional(), Arrays.toString(getAnnotations()));
    }
}
