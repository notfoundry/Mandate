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
import pw.stamina.mandate.internal.utils.reflect.TypeBuilder;
import pw.stamina.mandate.io.IODescriptor;

import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mark Johnson
 */
@Syntax(root = "implicit")
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

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof ZonedDateTime);

        assertEquals(commandOutput.poll(), currentTime);
    }

    @Test
    public void testPassingParameterizedImplicitArgument() {
        commandContext.getValueProviders().registerProvider(
                TypeBuilder.from(Map.class, String.class, String.class),
                () -> Collections.singletonMap("foo", "bar"));

        final Execution result = commandContext.execute("implicit stringstringmap");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertTrue(commandOutput.peek() instanceof Map);

        assertEquals(((Map<String, String>) commandOutput.poll()).get("foo"), "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentiatingBetweenGenericTypeParameters() {
        commandContext.getValueProviders().registerProvider(
                TypeBuilder.from(Map.class, String.class, String.class),
                () -> Collections.singletonMap("foo", "bar"));

        final Execution result = commandContext.execute("implicit intintmap");

        assertTrue(result.result() == ExitCode.FAILURE);

        assertEquals(0, commandOutput.size());

        assertEquals(1, commandErrors.size());
    }

    @Test
    public void testExecutingCommandWithNoIODescriptor() {
        final Execution result = commandContext.execute("implicit none");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(0, commandOutput.size());

        assertEquals(0, commandErrors.size());
    }

    @Executes(tree = "time")
    public ExitCode runImplicitTime(@Implicit final IODescriptor io, @Implicit final ZonedDateTime time) {
        io.out().write(time);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "stringstringmap")
    public ExitCode runImplicitStringToStringMap(@Implicit IODescriptor io, @Implicit Map<String, String> stringStringMap) {
        io.out().write(stringStringMap);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "intintmap")
    public ExitCode runImplicitIntegerToIntegerMap(@Implicit Map<Integer, Integer> integerIntegerMap) {
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "none")
    public ExitCode runNoImplicits() {
        return ExitCode.SUCCESS;
    }

}
