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
import pw.stamina.mandate.execution.argument.ArgumentHandler;
import pw.stamina.mandate.execution.argument.CommandArgument;
import pw.stamina.mandate.execution.parameter.CommandParameter;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.utils.reflect.TypeBuilder;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.parsing.InputParsingException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Foundry
 */
public class ConstructedCommandContextTestSuite {

    private final Queue<Object> commandErrors = new ArrayDeque<>();

    private final Queue<Object> commandOutput = new ArrayDeque<>();

    private final CommandContext ctx = Mandate.newContextBuilder()
            .usingIOEnvironment(Mandate.newIOBuilder()
                    .usingOutputStream(() -> commandOutput::add)
                    .usingErrorStream(() -> commandErrors::add)
                    .build())
            .withArgumentProvider(Mandate.newArgumentProviderBuilder()
                    .addProvider(TypeBuilder.from(List.class, String.class), () -> Collections.singletonList("foo"))
                    .build())
            .withHandlerRegistry(Mandate.newHandlerRegistryBuilder()
                    .addHandler(new ArgumentHandler<Object>() {
                        @Override
                        public Object parse(CommandArgument input, CommandParameter parameter, CommandContext commandContext) throws InputParsingException {
                            return input.getRaw();
                        }

                        @Override
                        public String getSyntax(CommandParameter parameter) {
                            return "Object";
                        }

                        @Override
                        public Class[] getHandledTypes() {
                            return new Class[] {Object.class};
                        }
                    })
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
        ctx.register(this);
    }

    @Test
    public void testUsingCustomArgumentHandler() {
        final Execution result = ctx.execute("getobj bar");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertTrue(commandOutput.peek() instanceof List);

        assertEquals(commandOutput.poll(), Collections.singletonList("foo"));

        assertTrue(commandOutput.peek() instanceof String);

        assertEquals(commandOutput.poll(), "bar");
    }

    @Executes
    @Syntax(root = "getobj")
    public ExitCode getObjectCommand(@Implicit IODescriptor io,
                                     @Implicit List<String> stringList,
                                     Object object) {
        io.out().write(stringList);
        io.out().write(object);
        return ExitCode.SUCCESS;
    }
}
