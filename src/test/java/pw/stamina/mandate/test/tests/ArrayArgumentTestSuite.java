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
import pw.stamina.mandate.annotations.Length;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * @author Mark Johnson
 */
@Syntax(root = "run")
public class ArrayArgumentTestSuite {

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
    public void testPassingToStringArray() {
        final Execution result = commandContext.execute("run strings [foo, \"Hello World!\", baz]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertArrayEquals(new String[] {"foo", "Hello World!", "baz"}, (String[]) commandOutput.poll());
    }

    @Test
    public void testPassingTo2DStringArray() {
        final Execution result = commandContext.execute("run 2dstrings [[foo, bar], [baz, quz, \"tricky, ]]]\"]]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertArrayEquals(new String[][] {{"foo", "bar"}, {"baz", "quz", "tricky", "]]]"}}, (String[][]) commandOutput.poll());
    }

    @Test
    public void testPassingZeroLengthArrayAsArgument() {
        final Execution result = commandContext.execute("run strings []");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertArrayEquals(new String[0], (String[]) commandOutput.poll());
    }

    @Test
    public void testPassingToPrimitiveIntArray() {
        final Execution result = commandContext.execute("run ints [100, 5000, 15000]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertArrayEquals(new int[] {100, 5000, 15000}, (int[]) commandOutput.poll());
    }

    @Test
    public void testPassingToClampedLengthIntArray() {
        final Execution result = commandContext.execute("run clampedints [1, 2, 3, 4, 5]");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertArrayEquals(new int[] {1, 2, 3, 4, 5}, (int[]) commandOutput.poll());
    }

    @Test
    public void testFailingClampedLengthArrayCheck() {
        final Execution result = commandContext.execute("run clampedints [1, 2, 3]");

        assertTrue(result.result() == ExitCode.INVALID);

        assertEquals(0, commandOutput.size());

        assertEquals(1, commandErrors.size());
    }

    @Executes(tree = "strings")
    public ExitCode runStrings(@Implicit final IODescriptor io, final String[] strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "ints")
    public ExitCode runInts(@Implicit final IODescriptor io, final int[] ints) {
        io.out().write(ints);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "clampedints")
    public ExitCode runClampedInts(@Implicit final IODescriptor io, @Length(min = 5, max = 5) final int[] ints) {
        io.out().write(ints);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "2dstrings")
    public ExitCode run2DStrings(@Implicit final IODescriptor io, final String[][] strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }
}
