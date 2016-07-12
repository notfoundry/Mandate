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

package pw.stamina.mandate.api.execution.argument;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.parsor.exceptions.ParseException;

/**
 * An argument handler designed to parse a CommandArgument to a reified Object type
 * A {@link CommandManager CommandManager} implementation may choose to have any given number of argument handlers, queryable through {@link CommandManager#findArgumentHandler(Class) findArgumentHandler}.
 *
 * @param <T> the type of object to which this argument handler will formally parse input
 * @author Foundry
 */
public interface ArgumentHandler<T> {

    /**
     * A method that takes a CommandInput and, using the provided CommandParameter and CommandManager to assist if necessary,
     * parses it as a value of the type T
     *
     * @param input the token of user input to be parsed
     * @param parameter the parameter for which the input should be parsed to an argument
     * @param commandManager the command manager that this handler is registered to
     * @return a parsed argument of type T, guaranteed to be compatible with the provided CommandParameter
     * @throws ParseException
     */
    T parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException;

    /**
     * A method designed to provide a friendly representation of the provided parameter, as interpreted by this argument handler
     *
     * @param parameter the parameter for which a string representation should be generated
     * @return a string representation of the provided parameter
     */
    String getSyntax(CommandParameter parameter);

    /**
     * @return an array of the classes corresponding to the reified object types that this argument handler supports
     */
    Class[] getHandledTypes();
}
