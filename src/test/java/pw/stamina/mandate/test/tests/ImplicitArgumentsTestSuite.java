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

package pw.stamina.mandate.test.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.io.IODescriptor;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author Foundry
 */
@Syntax(tree = "implicit")
public class ImplicitArgumentsTestSuite {

    private final Queue<Object> commandErrors = new ArrayDeque<>();

    private final Queue<Object> commandOutput = new ArrayDeque<>();

    private final CommandContext commandContext = Mandate.newContextBuilder()
            .usingIOEnvironment(Mandate.newIOBuilder()
                    .usingOutputStream(() -> commandOutput::add)
                    .usingErrorStream(() -> commandErrors::add)
                    .build())
            .build();

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(final Throwable e, final Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setup() {
        commandContext.register(this);
    }

    @Test
    public void testRegisteringNewImplicitArgument() {
        final ZonedDateTime currentTime = ZonedDateTime.now();

        commandContext.getValueProviders().registerProvider(ZonedDateTime.class, () -> currentTime);

        final Execution result = commandContext.execute("implicit time");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof ZonedDateTime);

        Assert.assertEquals(commandOutput.poll(), currentTime);
    }

    @Test
    public void testExecutingCommandWithNoIODescriptor() {
        final Execution result = commandContext.execute("implicit none");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);
    }

    @Executes(tree = "time")
    public ExitCode runImplicitTime(@Implicit final IODescriptor io, @Implicit final ZonedDateTime time) {
        io.out().write(time);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "none")
    public ExitCode runNoImplicits() {
        return ExitCode.SUCCESS;
    }

}
