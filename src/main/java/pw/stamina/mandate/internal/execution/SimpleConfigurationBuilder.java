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
import pw.stamina.mandate.execution.ConfigurationBuilder;
import pw.stamina.mandate.execution.argument.CommandArgumentCreationStrategy;
import pw.stamina.mandate.execution.executable.CommandExecutableCreationStrategy;
import pw.stamina.mandate.execution.parameter.CommandParameterCreationStrategy;
import pw.stamina.mandate.internal.execution.argument.DefaultCommandArgumentFactory;
import pw.stamina.mandate.internal.execution.executable.DefaultCommandExecutableFactory;
import pw.stamina.mandate.internal.execution.parameter.DefaultCommandParameterFactory;
import pw.stamina.mandate.internal.parsing.DefaultArgumentReifier;
import pw.stamina.mandate.internal.parsing.DefaultInputTokenizer;
import pw.stamina.mandate.internal.syntax.component.DefaultSyntaxComponentFactory;
import pw.stamina.mandate.parsing.ArgumentReificationStrategy;
import pw.stamina.mandate.parsing.InputTokenizationStrategy;
import pw.stamina.mandate.syntax.SyntaxComponentCreationStrategy;

import java.util.Optional;

/**
 * @author Mark Johnson
 */
public class SimpleConfigurationBuilder implements ConfigurationBuilder {

    private SyntaxComponentCreationStrategy syntaxComponentCreationStrategy;

    private CommandArgumentCreationStrategy commandArgumentCreationStrategy;

    private InputTokenizationStrategy inputTokenizationStrategy;

    private CommandExecutableCreationStrategy executableCreationStrategy;

    private CommandParameterCreationStrategy parameterCreationStrategy;

    private ArgumentReificationStrategy argumentReificationStrategy;

    @Override
    public ConfigurationBuilder usingSyntaxCreationStrategy(final SyntaxComponentCreationStrategy syntaxComponentCreationStrategy) {
        checkPrecondition(this.syntaxComponentCreationStrategy == null, "SyntaxComponent creation strategy has already been provided");
        this.syntaxComponentCreationStrategy = syntaxComponentCreationStrategy;
        return this;
    }

    @Override
    public ConfigurationBuilder usingArgumentCreationStrategy(final CommandArgumentCreationStrategy commandArgumentCreationStrategy) {
        checkPrecondition(this.commandArgumentCreationStrategy == null, "CommandArgument creation strategy has already been provided");
        this.commandArgumentCreationStrategy = commandArgumentCreationStrategy;
        return this;
    }

    @Override
    public ConfigurationBuilder usingInputTokenizationStrategy(final InputTokenizationStrategy inputTokenizationStrategy) {
        checkPrecondition(this.inputTokenizationStrategy == null, "Input tokenization strategy has already been provided");
        this.inputTokenizationStrategy = inputTokenizationStrategy;
        return this;
    }

    @Override
    public ConfigurationBuilder usingExecutableCreationStrategy(final CommandExecutableCreationStrategy executableCreationStrategy) {
        checkPrecondition(this.executableCreationStrategy == null, "CommandExecutable creation strategy has already been provided");
        this.executableCreationStrategy = executableCreationStrategy;
        return this;
    }

    @Override
    public ConfigurationBuilder usingParameterCreationStrategy(final CommandParameterCreationStrategy parameterCreationStrategy) {
        checkPrecondition(this.parameterCreationStrategy == null, "CommandParameter creation strategy has already been provided");
        this.parameterCreationStrategy = parameterCreationStrategy;
        return this;
    }

    @Override
    public ConfigurationBuilder usingArgumentReificationStrategy(final ArgumentReificationStrategy argumentReificationStrategy) {
        checkPrecondition(this.argumentReificationStrategy == null, "Argument reification strategy has already been provided");
        this.argumentReificationStrategy = argumentReificationStrategy;
        return this;
    }

    @Override
    public CommandConfiguration build() {
        return new SimpleCommandConfiguration(
                Optional.ofNullable(syntaxComponentCreationStrategy).orElseGet(DefaultSyntaxComponentFactory::getInstance),
                Optional.ofNullable(commandArgumentCreationStrategy).orElseGet(DefaultCommandArgumentFactory::getInstance),
                Optional.ofNullable(inputTokenizationStrategy).orElseGet(DefaultInputTokenizer::getInstance),
                Optional.ofNullable(executableCreationStrategy).orElseGet(DefaultCommandExecutableFactory::getInstance),
                Optional.ofNullable(parameterCreationStrategy).orElseGet(DefaultCommandParameterFactory::getInstance),
                Optional.ofNullable(argumentReificationStrategy).orElseGet(DefaultArgumentReifier::getInstance)
        );
    }

    private static void checkPrecondition(final boolean assertion, final String failureMessage) {
        if (!assertion) throw new IllegalStateException(failureMessage);
    }
}
