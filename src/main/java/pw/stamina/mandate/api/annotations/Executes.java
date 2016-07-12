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

package pw.stamina.mandate.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to declare a specific method as being an executable command
 * <p>
 * This should be used in conjunction with the {@link Syntax Syntax} annotation to define the
 * base command that this executable should be linked to.
 *
 * @author Foundry
 */
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Executes {

    /**
     * The syntax tree, if any, that should be used to supplement the syntax definition
     * attributed to this command by its associated {@link Syntax Syntax} annotation.
     * <p>
     * An element of this array should be formatted as a pipe-delimited sequence of aliases for
     * the next element in the syntax tree that this command should be considered a part of.
     * <p>
     * An example of this might be {@code {"return|ret", "string|str"}} for a command that intends to have a
     * fully-qualified executor of {@code [command name] return string [command arguments]}
     *
     * @return the supplementary syntax tree for this command
     */
    String[] tree() default {};
}
