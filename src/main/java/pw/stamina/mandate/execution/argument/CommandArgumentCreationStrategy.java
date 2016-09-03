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

package pw.stamina.mandate.execution.argument;

/**
 * The strategy by which plain String objects are converted to {@link CommandArgument CommandArgument} instances for use
 * in future processing.
 *
 * @author Mark Johnson
 */
public interface CommandArgumentCreationStrategy {

    /**
     * Attempts to return a CommandArgument instance that is syntactically equivalent to the String passed as an argument
     * to this method.
     *
     * @param argument the String argument from which a CommandArgument should be derived
     * @return a new CommandArgument representing the String argument passed to this method
     */
    CommandArgument newArgument(String argument);
}
