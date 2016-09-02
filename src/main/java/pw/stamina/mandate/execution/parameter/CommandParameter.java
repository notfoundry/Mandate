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

package pw.stamina.mandate.execution.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * A parameter for a command, generally used as a target for an {@link pw.stamina.mandate.execution.argument.ArgumentHandler ArgumentHandler}
 * <p>
 * CommandParameters should provide enough information for an argument parsing system to successful determine the reified object type that this parameter
 * is targeting, whether this parameter is a wrapped {@link java.util.Optional Optional} value, and if this parameter is a flag
 *
 * @author Foundry
 */
public interface CommandParameter extends AnnotatedElement {

    /**
     * Returns the specified annotation if it is present on the backing parameter represented by this instance, else null
     *
     * @param annotationClass the class of the annotation to be looked up
     * @param <A> the type of the annotation to be looked up
     * @return the specified annotation if it is present on the backing parameter represented by this instance, else null
     */
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    /**
     * Returns an array consisting of all the annotations present on the backing parameter
     * <p>
     * If there are no annotations present, this array will have a length of zero
     *
     * @return an array consisting of all the annotations present on the backing parameter
     */
    Annotation[] getAnnotations();

    default Annotation[] getDeclaredAnnotations() {
        return getAnnotations();
    }

    /**
     * @return the reified type of this parameter
     */
    Class<?> getType();

    Type[] getTypeParameters();

    /**
     * Returns whether or not this parameter should be considered to be optional
     * @return {@code true} if this parameter is optional, else {@code false}
     */
    boolean isOptional();

    boolean isImplicit();

    /**
     * Returns a friendly description of this parameter. See {@link pw.stamina.mandate.annotations.meta.Usage Usage}
     * @return a friendly description of this parameter
     */
    String getDescription();

    /**
     * Returns a friendly name for this parameter. See {@link pw.stamina.mandate.annotations.meta.Usage Usage}
     * @return a friendly name for this parameter
     */
    String getLabel();
}
