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
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.api.annotations.flag.UserFlag;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
public class CommandFlagTest {
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
    public void testFlagSet() {
        Execution result = commandManager.execute("greet --caps --recipient you hello");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to you: HELLO", commandOutput.poll());
    }

    @Test
    public void testFlagUnset() {
        Execution result = commandManager.execute("greet foo");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to someone: foo", commandOutput.poll());
    }

    @Test
    public void testNonDeclarationOrderFlagSet() {
        Execution result = commandManager.execute("greet hello --recipient me --caps");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertEquals("to me: HELLO", commandOutput.poll());
    }

    @Executes
    @Syntax(syntax = "greet")
    public ExitCode doGreeting(IODescriptor io,
                               @AutoFlag(flag = {"-caps"}) boolean useCaps,
                               @UserFlag(flag = {"-recipient"}, elsedef = "someone") String recipient,
                               String greeting) {

        io.out().write("to " + recipient + ": " + (useCaps ? greeting.toUpperCase() : greeting));
        return ExitCode.SUCCESS;
    }
}
