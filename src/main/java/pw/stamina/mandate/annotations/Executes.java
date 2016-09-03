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

package pw.stamina.mandate.annotations;

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
 * @author Mark Johnson
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

    /**
     * Whether or not the annotated command should be executed asynchronously
     * <p>
     * If this is marked as {@code true}, the command will proceed in a non-blocking fashion, allowing
     * the calling thread to continue without having to wait for the result code of the command. If this is
     * {@code false}, the calling thread will block until the invoked command is finished executing.
     * <p>
     * By default, this is {@code false}
     * @return whether or not the annotated command should be executed asynchronously
     */
    boolean async() default false;
}
