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
import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
@Syntax(syntax = "foo")
public class ClassSyntaxOverrideTest {
    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = Mandate.newManager(StandardInputStream::get, () -> commandOutput::add, () -> commandErrors::add);

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
    public void testSyntaxOverride() {
        Execution result = commandManager.execute("sum 125 125");

        commandErrors.forEach(System.out::println);

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals(250, commandOutput.poll());
    }

    @Test
    public void testFailedClassSyntaxUse() {
        Execution result = commandManager.execute("foo 200 175");

        Assert.assertTrue(result.result() == ExitCode.INVALID);
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    public ExitCode sum(IODescriptor io, int augend, int addend) {
        io.out().write(augend + addend);
        return ExitCode.SUCCESS;
    }
}
