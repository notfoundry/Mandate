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

import org.junit.Test;
import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.annotations.flag.AutoFlag;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.execution.parameter.UnsupportedParameterException;

/**
 * @author Mark Johnson
 */
public class OverlappingCommandFlagTestSuite {

    private final CommandContext commandContext = Mandate.newContext();

    @Test(expected = UnsupportedParameterException.class)
    public void testFailedFlagUse() {
        commandContext.register(this);
    }

    @Executes
    @Syntax(tree = "run")
    public ExitCode run(
            @AutoFlag(flag = {"f", "flag1"}) final boolean flag1,
            @AutoFlag(flag = {"f", "flag2"}) final boolean flag2) {
        return ExitCode.SUCCESS;
    }
}
