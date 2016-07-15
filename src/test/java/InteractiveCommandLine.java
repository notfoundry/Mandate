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

import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Foundry
 */
public class InteractiveCommandLine {
    public static void main(String[] args) {
        final CommandManager commandManager = Mandate.newManager();
        commandManager.register(new InteractiveCommandLine());
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            Execution execution = commandManager.execute(scanner.nextLine());
            System.out.println("Result code: " + execution.result());
        }
    }

    @Executes
    @Syntax(syntax = "accept")
    public ExitCode acceptUserInput(IODescriptor io) throws IOException {
        io.out().write("Please enter something:");
        String input = io.in().read();
        io.out().write(input);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(syntax = {"sum", "add"})
    public ExitCode sum(IODescriptor io, long augend, long addend) {
        io.out().write(augend + addend);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(syntax = {"take"})
    public ExitCode sum(IODescriptor io, Integer augend) {
        io.out().write(augend);
        return ExitCode.SUCCESS;
    }

    @Executes
    @Syntax(syntax = {"execute", "exec"})
    public ExitCode execute(IODescriptor io) {
        return ExitCode.SUCCESS;
    }
}
