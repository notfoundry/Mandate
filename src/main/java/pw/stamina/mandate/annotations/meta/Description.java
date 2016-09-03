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

package pw.stamina.mandate.annotations.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotating giving description metadata to a declared command
 * <p>
 * This should be used primarily to supplement command manager implementations that might choose
 * to provide auto-generated manual pages for registered commands
 *
 * @author Mark Johnson
 */
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Description {

    /**
     * An series of strings, each representing one line in the description of the annotated command
     * <p>
     * Each element in the array should be considered to be delimited by the result of {@link System#lineSeparator lineSeparator}
     * as the values are declared
     *
     * @return the description of the annotated command
     */
    String[] value();
}
