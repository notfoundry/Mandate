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

package pw.stamina.mandate.internal.execution.argument.handlers;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.CommandParameter;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.parsor.exceptions.ParseException;
import pw.stamina.parsor.impl.parsers.singletons.BooleanParser;

/**
 * @author Foundry
 */
public final class BooleanArgumentHandler implements ArgumentHandler<Boolean> {

    @Override
    public Boolean parse(CommandArgument input, CommandParameter parameter, CommandManager commandManager) throws ParseException {
        return BooleanParser.get().parse(input.getRaw());
    }

    @Override
    public String getSyntax(CommandParameter parameter) {
        return parameter.getLabel() + " - " + "Boolean";
    }

    @Override
    public Class[] getHandledTypes() {
        return new Class[] {Boolean.class};
    }

}

