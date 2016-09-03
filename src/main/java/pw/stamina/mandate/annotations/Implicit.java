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
 * An annotation used to mark a command parameter as having arguments provided to it by the executing {@link pw.stamina.mandate.execution.CommandContext CommandContext}
 * instead of relying on user input to determine the reified Object passed to it.
 * <p>
 * This can be thought of as a form of dependency injection for commands, preventing state from having to be stored and
 * updated in the container objects in which command-executable methods are stored.
 *
 * @author Mark Johnson
 */
@Target(value=ElementType.PARAMETER)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Implicit {}
