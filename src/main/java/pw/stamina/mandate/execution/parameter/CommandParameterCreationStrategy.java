/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2017 Mark Johnson
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

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.internal.execution.parameter.UnsupportedParameterException;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The strategy by which the {@link CommandParameter CommandParameters} associated with a {@link pw.stamina.mandate.execution.executable.CommandExecutable CommandExecutable}
 * will be generated from the method backing them.
 *
 * @author Mark Johnson
 */
public interface CommandParameterCreationStrategy {

    /**
     * Attempts to return a list of {@link CommandParameter CommandParameters} corresponding as best possible to the declared parameters
     * of the backing method provided as an argument to this method.
     *
     * @param backingMethod the backing method that the produced list of CommandParameters will be based on
     * @param commandContext the command context associated with this creation attempt
     * @return a list of CommandParameters best matching the parameters of the backing method
     * @throws UnsupportedParameterException if one of the parameters of the provided method does not have a suitable {@link pw.stamina.mandate.parsing.argument.ArgumentHandler ArgumentHandler}
     */
    List<CommandParameter> generateCommandParameters(Method backingMethod, CommandContext commandContext) throws UnsupportedParameterException;
}
