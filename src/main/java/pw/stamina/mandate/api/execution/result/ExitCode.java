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

package pw.stamina.mandate.api.execution.result;

/**
 * An enumeration of the possible exit codes for a command
 * <p>
 * All commands annotated as {@link pw.stamina.mandate.api.annotations.Executes executes} must return one of these constant values.
 * <li>{@link #SUCCESS SUCCESS}</li>
 * <li>{@link #FAILURE FAILURE}</li>
 * <li>{@link #TERMINATED TERMINATED}</li>
 * <li>{@link #INVALID INVALID}</li>
 *
 * @author Foundry
 */
public enum ExitCode {

    /**
     * The exit code returned for a successful command execution
     */
    SUCCESS,

    /**
     * The exit code returned for a failed command execution
     */
    FAILURE,

    /**
     * The exit code returned for command execution that was terminated prior to completion
     */
    TERMINATED,

    /**
     * The exit code returned for an execution that was never started as a result of invalid user input
     */
    INVALID;
}
