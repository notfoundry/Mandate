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

package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.execution.CommandConfiguration;
import pw.stamina.mandate.execution.argument.CommandArgumentCreationStrategy;
import pw.stamina.mandate.execution.executable.CommandExecutableCreationStrategy;
import pw.stamina.mandate.execution.parameter.CommandParameterCreationStrategy;
import pw.stamina.mandate.parsing.ArgumentReificationStrategy;
import pw.stamina.mandate.parsing.InputTokenizationStrategy;
import pw.stamina.mandate.syntax.SyntaxComponentCreationStrategy;

/**
 * @author Mark Johnson
 */
public class SimpleCommandConfiguration implements CommandConfiguration {

    private final SyntaxComponentCreationStrategy syntaxComponentCreationStrategy;

    private final CommandArgumentCreationStrategy commandArgumentCreationStrategy;

    private final InputTokenizationStrategy inputTokenizationStrategy;

    private final CommandExecutableCreationStrategy executableCreationStrategy;

    private final CommandParameterCreationStrategy parameterCreationStrategy;

    private final ArgumentReificationStrategy argumentReificationStrategy;

    public SimpleCommandConfiguration(final SyntaxComponentCreationStrategy syntaxComponentCreationStrategy,
                                      final CommandArgumentCreationStrategy commandArgumentCreationStrategy,
                                      final InputTokenizationStrategy inputTokenizationStrategy,
                                      final CommandExecutableCreationStrategy executableCreationStrategy,
                                      final CommandParameterCreationStrategy parameterCreationStrategy,
                                      final ArgumentReificationStrategy argumentReificationStrategy) {
        this.syntaxComponentCreationStrategy = syntaxComponentCreationStrategy;
        this.commandArgumentCreationStrategy = commandArgumentCreationStrategy;
        this.inputTokenizationStrategy = inputTokenizationStrategy;
        this.executableCreationStrategy = executableCreationStrategy;
        this.parameterCreationStrategy = parameterCreationStrategy;
        this.argumentReificationStrategy = argumentReificationStrategy;
    }

    @Override
    public SyntaxComponentCreationStrategy getSyntaxCreationStrategy() {
        return syntaxComponentCreationStrategy;
    }

    @Override
    public CommandArgumentCreationStrategy getArgumentCreationStrategy() {
        return commandArgumentCreationStrategy;
    }

    @Override
    public InputTokenizationStrategy getInputTokenizationStrategy() {
        return inputTokenizationStrategy;
    }

    @Override
    public CommandExecutableCreationStrategy getExecutableCreationStrategy() {
        return executableCreationStrategy;
    }

    @Override
    public CommandParameterCreationStrategy getParameterCreationStrategy() {
        return parameterCreationStrategy;
    }

    @Override
    public ArgumentReificationStrategy getArgumentReificationStrategy() {
        return argumentReificationStrategy;
    }
}
