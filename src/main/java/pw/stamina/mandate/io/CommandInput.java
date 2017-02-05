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

package pw.stamina.mandate.io;

import java.io.IOException;

/**
 * A stream of input from which a running command can read if supplementary input is necessary
 *
 * @author Mark Johnson
 */
@FunctionalInterface
public interface CommandInput {

    /**
     * Finds and returns the next complete token from this input stream
     * <p>
     * This method may block while waiting for input
     *
     * @return the next complete token from this input stream
     * @throws IOException if there was an exception reading the input
     */
    String read() throws IOException;
}
