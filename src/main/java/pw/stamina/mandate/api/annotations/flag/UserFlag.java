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

package pw.stamina.mandate.api.annotations.flag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation denoting a parameter that should be considered to be a command flag requiring a single operand
 * <p>
 * This should generally be used in cases where additional information is required to discern the meaning of this flag,
 * such as providing a file path to a logging flag.
 *
 * @author Foundry
 */
@Target(value=ElementType.PARAMETER)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface UserFlag {

    /**
     * An array of the strings that should be matched against when checking for the presence of this flag
     * <p>
     * A CommandExecutable implementation should generally prevent duplicate flag definitions from existing
     * in a command, as the behavior of the flag is in that case undefined should it still be permissible
     *
     * @return the flag definitions that should be considered to represent this flag.
     */
    String[] flag();

    /**
     * The input that should be parsed to a value of the parameter type annotated by this if the described flag
     * is marked as missing.
     * <p>
     * It is the responsibility of the developer defining this to ensure that
     * the value described here is valid input for the argument parser corresponding to referenced type.
     *
     * @return the input that should be parsed to an argument for the annotated parameter
     */
    String elsedef() default "";

    /**
     * A set of flags, if any, that should be incompatible with this flag.
     * <p>
     * This should be used when only one flag out of some number of other flags should be present at any given time.
     *
     * @return a set of flags that should be incompatible with this one
     */
    String[] xor() default {};
}

