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

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Mark Johnson
 */
public class OptionalTypeParameterTestSuite {
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
    public void testPassingOptionalIntegerList() {
        final Execution result = commandContext.execute("optionalparameters [2, 4, 6, 8, 10]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(Arrays.asList(Optional.of(2), Optional.of(4), Optional.of(6), Optional.of(8), Optional.of(10)), commandOutput.poll());
    }

    @Executes
    @Syntax(root = "optionalparameters")
    public ExitCode optionalIntegerList(@Implicit final IODescriptor io, final List<Optional<Integer>> list) {
        io.out().write(list);
        return ExitCode.SUCCESS;
    }
}
