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

package pw.stamina.mandate.internal.execution;

import pw.stamina.mandate.execution.CommandConfiguration;
import pw.stamina.mandate.parsing.argument.CommandArgumentCreationStrategy;
import pw.stamina.mandate.internal.parsing.argument.DefaultCommandArgumentFactory;
import pw.stamina.mandate.execution.executable.CommandExecutableCreationStrategy;
import pw.stamina.mandate.internal.execution.executable.DefaultCommandExecutableFactory;
import pw.stamina.mandate.execution.parameter.CommandParameterCreationStrategy;
import pw.stamina.mandate.internal.execution.parameter.DefaultCommandParameterFactory;
import pw.stamina.mandate.parsing.ArgumentReificationStrategy;
import pw.stamina.mandate.internal.parsing.DefaultArgumentReifier;
import pw.stamina.mandate.internal.parsing.DefaultInputTokenizer;
import pw.stamina.mandate.parsing.InputTokenizationStrategy;
import pw.stamina.mandate.internal.syntax.component.DefaultSyntaxTreeFactory;
import pw.stamina.mandate.syntax.SyntaxTreeCreationStrategy;

/**
 * @author Mark Johnson
 */
public enum DefaultCommandConfiguration implements CommandConfiguration {
    INSTANCE;

    @Override
    public SyntaxTreeCreationStrategy getSyntaxCreationStrategy() {
        return DefaultSyntaxTreeFactory.getInstance();
    }

    @Override
    public CommandArgumentCreationStrategy getArgumentCreationStrategy() {
        return DefaultCommandArgumentFactory.getInstance();
    }

    @Override
    public InputTokenizationStrategy getInputTokenizationStrategy() {
        return DefaultInputTokenizer.getInstance();
    }

    @Override
    public CommandExecutableCreationStrategy getExecutableCreationStrategy() {
        return DefaultCommandExecutableFactory.getInstance();
    }

    @Override
    public CommandParameterCreationStrategy getParameterCreationStrategy() {
        return DefaultCommandParameterFactory.getInstance();
    }

    public ArgumentReificationStrategy getArgumentReificationStrategy() {
        return DefaultArgumentReifier.getInstance();
    }

    public static DefaultCommandConfiguration getInstance() {
        return INSTANCE;
    }
}
