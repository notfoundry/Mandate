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

package pw.stamina.mandate.api.annotations.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation providing a name, along with usage information for, the annotated command parameter
 * This should be used primarily to supplement command manager implementations that might choose
 * to provide auto-generated manual pages for registered commands
 *
 * @author Foundry
 */
@Target(value=ElementType.PARAMETER)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Usage {

    /**
     * @return The preferred name of this parameter
     */
    String name();

    /**
     * A usage string supplementing the defintion of the annotated parameter.
     *
     * @return The defined usage help text for this particular parameter
     */
    String usage() default "";
}

