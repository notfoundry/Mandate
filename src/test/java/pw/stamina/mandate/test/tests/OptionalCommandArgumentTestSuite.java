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

package pw.stamina.mandate.test.tests;

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

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * @author Mark Johnson
 */
public class OptionalCommandArgumentTestSuite {
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
    public void testUsingFullCommandSignature() {
        final Execution result = commandContext.execute("execute first \"second argument\" 100");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals("first second argument 100",
                commandOutput.poll());
    }

    @Test
    public void testUsingPartialCommandSignature() {
        final Execution result = commandContext.execute("execute first second");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals("first second 0",
                commandOutput.poll());
    }

    @Test
    public void testUsingOnlyMandatoryArguments() {
        final Execution result = commandContext.execute("execute first");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals("first DEFAULT 0",
                commandOutput.poll());
    }

    @Executes
    @Syntax(root = "execute")
    public ExitCode doThing(@Implicit final IODescriptor io, final String arg1, final Optional<String> arg2, final Optional<Integer> arg3) {
        io.out().write(String.format("%s %s %d", arg1, arg2.orElse("DEFAULT"), arg3.orElse(0)));
        return ExitCode.SUCCESS;
    }
}
