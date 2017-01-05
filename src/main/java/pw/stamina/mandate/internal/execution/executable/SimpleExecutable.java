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

package pw.stamina.mandate.internal.execution.executable;

import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.flag.AutoFlag;
import pw.stamina.mandate.annotations.flag.UserFlag;
import pw.stamina.mandate.annotations.meta.Description;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.execution.executable.invoker.CommandInvoker;
import pw.stamina.mandate.internal.execution.executable.invoker.InvokerFactory;
import pw.stamina.mandate.internal.execution.result.ExecutionFactory;
import pw.stamina.mandate.parsing.ArgumentReificationException;
import pw.stamina.mandate.parsing.InputParsingException;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;

/**
 * @author Mark Johnson
 */
class SimpleExecutable implements CommandExecutable {

    private final String executableName;

    private final Description executableDescription;

    private final CommandInvoker commandInvoker;

    private final CommandContext commandContext;

    private final List<CommandParameter> parameters;

    private final boolean parallel;

    SimpleExecutable(final Method backingMethod, final Object methodParent, final CommandContext commandContext) throws MalformedCommandException {
        if (backingMethod.getReturnType() != ExitCode.class) {
            throw new MalformedCommandException("Annotated method '" + backingMethod.getName() + "' does have a return type of " + ExitCode.class.getCanonicalName());
        }

        this.parameters = (this.commandContext = commandContext).getCommandConfiguration().getParameterCreationStrategy().generateCommandParameters(backingMethod, commandContext);
        this.executableName = backingMethod.getName();
        this.executableDescription = backingMethod.getDeclaredAnnotation(Description.class);
        this.commandInvoker = InvokerFactory.makeInvoker(backingMethod, methodParent);
        this.parallel = backingMethod.getDeclaredAnnotation(Executes.class).async();
    }

    @Override
    public Execution execute(final Deque<CommandArgument> arguments, final ExecutionContext executionContext) throws ArgumentReificationException {
        try {
            final Object[] parsedArgs = commandContext.getCommandConfiguration().getArgumentReificationStrategy().parse(arguments, parameters, executionContext, commandContext);
            return ExecutionFactory.makeExecution(commandInvoker, executionContext, parsedArgs, parallel);
        } catch (InputParsingException e) {
            executionContext.getIODescriptor().err().write(e.getMessage());
            return Execution.complete(ExitCode.INVALID);
        }
    }

    @Override
    public List<CommandParameter> getParameters() {
        return parameters;
    }

    @Override
    public String getDescription() {
        return (executableDescription != null) ? String.join(System.lineSeparator(), (CharSequence[]) executableDescription.value()) : "";
    }

    @Override
    public int minimumArguments() {
        return (int) parameters.stream()
                .filter(param -> param.getAnnotation(AutoFlag.class) == null && param.getAnnotation(UserFlag.class) == null)
                .filter(param -> !param.isOptional() && !param.isImplicit())
                .count();
    }

    @Override
    public int maximumArguments() {
        final int baseParameterCount = (int) parameters.stream()
                .filter(param -> !param.isImplicit())
                .count();
        return baseParameterCount + (int) parameters.stream()
                .filter(param -> param.getAnnotation(UserFlag.class) != null)
                .count();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleExecutable that = (SimpleExecutable) o;
        return this.minimumArguments() == that.minimumArguments() &&
                this.maximumArguments() == that.maximumArguments();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + minimumArguments();
        result = 31 * result + maximumArguments();
        return result;
    }

    @Override
    public String toString() {
        return String.format("SimpleExecutable{name=%s, parameters=%s}", executableName, parameters);
    }
}

