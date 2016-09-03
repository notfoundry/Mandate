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
import pw.stamina.mandate.annotations.flag.AutoFlag;
import pw.stamina.mandate.annotations.flag.UserFlag;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.io.IODescriptor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Mark Johnson
 */
public class CommandFlagTestSuite {
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
    public void testFlagSet() {
        final Execution result = commandContext.execute("greet --caps --recipient you hello");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to you: HELLO", commandOutput.poll());
    }

    @Test
    public void testFlagUnset() {
        final Execution result = commandContext.execute("greet foo");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to someone: foo", commandOutput.poll());
    }

    @Test
    public void testNonDeclarationOrderFlagSet() {
        final Execution result = commandContext.execute("greet hello --recipient me --caps");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to me: HELLO", commandOutput.poll());
    }

    @Executes
    @Syntax(tree = "greet")
    public ExitCode doGreeting(@Implicit final IODescriptor io,
                               @AutoFlag(flag = {"-caps"}) final boolean useCaps,
                               @UserFlag(flag = {"-recipient"}, elsedef = "someone") final String recipient,
                               final String greeting) {

        io.out().write("to " + recipient + ": " + (useCaps ? greeting.toUpperCase() : greeting));
        return ExitCode.SUCCESS;
    }
}
