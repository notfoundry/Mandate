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
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.io.IODescriptor;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;

/**
 * @author Mark Johnson
 */
public class MapArgumentTestSuite {
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
    public void testPassingStringIntegerMap() {
        final Execution result = commandContext.execute("stringintmap [foo->256, bar -> 512]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof Map);

        Assert.assertEquals(256, ((Map) commandOutput.peek()).get("foo"));

        Assert.assertEquals(512, ((Map) commandOutput.peek()).get("bar"));
    }

    @Test
    public void testPassing2DStringIntegerMap() {
        final Execution result = commandContext.execute("2dstringintmap [foo ->[bar-> 512]]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof Map);

        Assert.assertEquals(Collections.singletonMap("bar", 512), ((Map) commandOutput.poll()).get("foo"));
    }

    @Executes
    @Syntax(tree = "stringintmap")
    public ExitCode stringIntMapCommand(@Implicit final IODescriptor io, final Map<String, Integer> map) {
        io.out().write(map);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(tree = "2dstringintmap")
    public ExitCode stringIntMapCommand2D(@Implicit final IODescriptor io, final Map<String, Map<String, Integer>> map) {
        io.out().write(map);
        return ExitCode.SUCCESS;
    }
}
