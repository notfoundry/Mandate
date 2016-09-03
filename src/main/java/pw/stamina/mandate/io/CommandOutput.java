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

package pw.stamina.mandate.io;

/**
 * A stream of output to which a running command can write
 * This is generally used to allow commands to print messages as they run, giving them an incredibly
 * uncomplicated interface for doing so through the {@link CommandOutput#write(Object) write} method
 *
 * @author Mark Johnson
 */
@FunctionalInterface
public interface CommandOutput {

    /**
     * Submits a string to the output stream represented by this instance
     * <p>
     * The usage of this object is up to the implementation to decide, depending on its intended use
     *
     * @param o the object to be written to the stream
     */
    void write(Object o);
}
