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
import pw.stamina.mandate.internal.annotations.Length;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Mark Johnson
 */
@Syntax(root = "run")
public class ListArgumentTestSuite {

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
    public void testPassingToStringList() {
        final Execution result = commandContext.execute("run strings [foo, bar]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(Arrays.asList("foo", "bar"), commandOutput.poll());
    }

    @Test
    public void testPassingZeroLengthListAsArgument() {
        final Execution result = commandContext.execute("run strings []");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(Collections.emptyList(), commandOutput.poll());
    }

    @Test
    public void testPassingToClampedLengthIntList() {
        final Execution result = commandContext.execute("run clampedints [1, 2, 3, 4, 5]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), commandOutput.poll());
    }

    @Test
    public void testFailingClampedLengthListCheck() {
        final Execution result = commandContext.execute("run clampedints [1, 2, 3]");

        assertTrue(result.result() == ExitCode.INVALID);

        assertEquals(0, commandOutput.size());

        assertEquals(1, commandErrors.size());
    }

    @Test
    public void testPassingTo2DStringList() {
        final Execution result = commandContext.execute("run 2dstrings [[foo, bar], [baz, quz]]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(Arrays.asList(Arrays.asList("foo", "bar"), Arrays.asList("baz", "quz")), commandOutput.poll());
    }

    @Executes(tree = "strings")
    public ExitCode runStrings(@Implicit final IODescriptor io, final List<String> strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "clampedints")
    public ExitCode runClampedInts(@Implicit final IODescriptor io, @Length(min = 5, max = 5) final List<Integer> ints) {
        io.out().write(ints);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "2dstrings")
    public ExitCode run2DStrings(@Implicit final IODescriptor io, final List<List<String>> strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }
}
