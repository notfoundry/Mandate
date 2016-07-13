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

package pw.stamina.mandate.internal.execution.executable;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.execution.executable.transformer.InvokerProxy;
import pw.stamina.mandate.internal.execution.executable.transformer.InvokerProxyFactory;
import pw.stamina.mandate.internal.execution.result.ExecutionFactory;
import pw.stamina.mandate.internal.parsing.ArgumentToObjectParser;
import pw.stamina.parsor.exceptions.ParseException;

import java.lang.reflect.Method;
import java.util.Deque;

/**
 * @author Foundry
 */
class TransformerExecutable extends MethodExecutable {

    final InvokerProxy invoker;

    TransformerExecutable(Method backingMethod, Object methodParent, CommandManager commandManager) throws MalformedCommandException {
        super(backingMethod, methodParent, commandManager);
        this.invoker = InvokerProxyFactory.makeProxy(backingMethod, methodParent);
    }

    @Override
    public Execution execute(Deque<CommandArgument> arguments, IODescriptor io) throws ParseException {
        final Object[] parsedArgs = (argumentParser == null ? (argumentParser = new ArgumentToObjectParser(this, commandManager)) : argumentParser).parse(arguments);
        return ExecutionFactory.makeExecution(invoker, methodParent, io, parsedArgs);
    }
}
