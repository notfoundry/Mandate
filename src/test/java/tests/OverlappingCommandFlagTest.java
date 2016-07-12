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

import org.junit.Test;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.annotations.flag.AutoFlag;
import pw.stamina.mandate.internal.execution.executable.UnsupportedParameterException;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;

/**
 * @author Foundry
 */
public class OverlappingCommandFlagTest {

    private CommandManager commandManager = new UnixCommandManager();

    @Test(expected = UnsupportedParameterException.class)
    public void testFailedFlagUse() {
        commandManager.register(this);
    }

    @Executes
    @Syntax(syntax = "run")
    public ExitCode run(IODescriptor io,
                               @AutoFlag(flag = {"f", "flag1"}) boolean flag1,
                               @AutoFlag(flag = {"f", "flag2"}) boolean flag2) {
        return ExitCode.SUCCESS;
    }
}
