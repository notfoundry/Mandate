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

package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;
import pw.stamina.mandate.internal.annotations.Length;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * @author Foundry
 */
@Syntax(syntax = "run")
public class ArrayArgumentTest {

    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = new UnixCommandManager(StandardInputStream::get, () -> commandOutput::add, () -> commandErrors::add);

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setup() {
        commandManager.register(this);
    }

    @Test
    public void testPassingToStringArray() {
        Execution result = commandManager.execute("run strings [foo, \"Hello World!\", baz]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[foo, Hello World!, baz]", commandOutput.poll());
    }

    @Test
    public void testPassingTo2DStringArray() {
        Execution result = commandManager.execute("run 2dstrings [[foo, bar], [baz, quz, \"tricky, ]]]\"]]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[[foo, bar], [baz, quz, tricky, ]]]]]", commandOutput.poll());
    }

    @Test
    public void testPassingZeroLengthArrayAsArgument() {
        Execution result = commandManager.execute("run strings []");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[]", commandOutput.poll());
    }

    @Test
    public void testPassingToPrimitiveIntArray() {
        Execution result = commandManager.execute("run ints [100, 5000, 15000]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[100, 5000, 15000]", commandOutput.poll());
    }

    @Test
    public void testPassingToClampedLengthIntArray() {
        Execution result = commandManager.execute("run clampedints [1, 2, 3, 4, 5]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("[1, 2, 3, 4, 5]", commandOutput.poll());
    }

    @Test
    public void testFailingClampedLengthArrayCheck() {
        Execution result = commandManager.execute("run clampedints [1, 2, 3]");

        Assert.assertTrue(result.result() == ExitCode.INVALID);
    }

    @Executes(tree = "strings")
    public ExitCode runStrings(IODescriptor io, String[] strings) {
        io.out().write(Arrays.toString(strings));
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "ints")
    public ExitCode runInts(IODescriptor io, int[] ints) {
        io.out().write(Arrays.toString(ints));
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "clampedints")
    public ExitCode runClampedInts(IODescriptor io, @Length(min = 5, max = 5) int[] ints) {
        io.out().write(Arrays.toString(ints));
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "2dstrings")
    public ExitCode run2DStrings(IODescriptor io, String[][] strings) {
        io.out().write(Arrays.deepToString(strings));
        return ExitCode.SUCCESS;
    }
}
