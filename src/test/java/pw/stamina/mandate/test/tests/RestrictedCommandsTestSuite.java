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
import pw.stamina.mandate.annotations.Restricted;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.security.SimpleCommandSender;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.security.CommandSender;
import pw.stamina.mandate.security.Permission;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mark Johnson
 */
public class RestrictedCommandsTestSuite {

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
    public void testPassingToStringSet() {
        final Execution result = commandContext.execute("run restricted foo",
                new SimpleCommandSender(Collections.singleton(Permission.of("mandate.admin"))));

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(1, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals("RESTRICTED: foo", commandOutput.poll());
    }

    @Test
    public void testCheckingPermissionsForCommandSender() {
        final CommandSender commandSender = new SimpleCommandSender(new HashSet<>(Arrays.asList(
                Permission.of("a.b.c.*"),
                Permission.of("e.f.g"),
                Permission.of("h.i.j")
        )));

        assertTrue(commandSender.hasPermission(Permission.of("a.b.c.d")));
        assertFalse(commandSender.hasPermission(Permission.of("e.f.h")));
        assertTrue(commandSender.hasPermission(Permission.of("h.i.j")));
    }

    @Executes(tree = "restricted")
    @Syntax(root = "run")
    @Restricted(permission = "mandate.admin")
    public ExitCode runRestricted(@Implicit final IODescriptor io, final String inputString) {
        io.out().write(String.format("RESTRICTED: %s", inputString));
        return ExitCode.SUCCESS;
    }
}
