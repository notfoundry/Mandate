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
 * An annotation used to define the global default syntax for all methods annotated with {@link Executes executes} in the target class,
 * or as a syntax tree override for one of the aforementioned methods.
 * This should be applied at the class level if all commands in the class should inherit from a common base syntax component, otherwise
 * individual commands should have their own {@link Syntax Syntax} annotations as needed
 *
 * @author Foundry
 */
@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Syntax {

    /**
     * The base syntax components that target command(s) should be considered to be children of
     * An element of this array should be a single word, representing an alias or definition for the name
     * of the target command(s). An example of this might be {"execute", "exec", "do"} for a command that intends to have a
     * fully-qualified executor of "execute [sub-syntax] [command arguments]"
     *
     * @return the base components of the syntax tree for the target command(s)
     */
    String[] syntax() default {};
}
