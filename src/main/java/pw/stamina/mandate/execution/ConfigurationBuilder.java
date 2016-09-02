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

package pw.stamina.mandate.execution;

import pw.stamina.mandate.execution.argument.CommandArgumentCreationStrategy;
import pw.stamina.mandate.execution.executable.CommandExecutableCreationStrategy;
import pw.stamina.mandate.execution.parameter.CommandParameterCreationStrategy;
import pw.stamina.mandate.parsing.ArgumentReificationStrategy;
import pw.stamina.mandate.parsing.InputTokenizationStrategy;
import pw.stamina.mandate.syntax.SyntaxComponentCreationStrategy;

/**
 * @author Foundry
 */
public interface ConfigurationBuilder {
    ConfigurationBuilder usingSyntaxCreationStrategy(SyntaxComponentCreationStrategy syntaxComponentCreationStrategy);

    ConfigurationBuilder usingArgumentCreationStrategy(CommandArgumentCreationStrategy commandArgumentCreationStrategy);

    ConfigurationBuilder usingInputTokenizationStrategy(InputTokenizationStrategy inputTokenizationStrategy);

    ConfigurationBuilder usingExecutableCreationStrategy(CommandExecutableCreationStrategy executableCreationStrategy);

    ConfigurationBuilder usingParameterCreationStrategy(CommandParameterCreationStrategy parameterCreationStrategy);

    ConfigurationBuilder usingArgumentReificationStrategy(ArgumentReificationStrategy argumentReificationStrategy);

    CommandConfiguration build();
}
