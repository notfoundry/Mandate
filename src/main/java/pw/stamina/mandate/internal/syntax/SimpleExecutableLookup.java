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

package pw.stamina.mandate.internal.syntax;

import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.syntax.ExecutableLookup;

/**
 * @author Mark Johnson
 */
public class SimpleExecutableLookup implements ExecutableLookup {

    private final CommandExecutable executable;

    private final Exception exception;

    public SimpleExecutableLookup(final CommandExecutable executable, final Exception exception) {
        this.executable = executable;
        this.exception = exception;
    }

    public SimpleExecutableLookup(final CommandExecutable executable) {
        this(executable, null);
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public CommandExecutable getExecutable() {
        return executable;
    }

    @Override
    public boolean wasSuccessful() {
        return executable != null;
    }
}
