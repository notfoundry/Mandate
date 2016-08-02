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

package pw.stamina.mandate;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.io.CommandInput;
import pw.stamina.mandate.api.io.CommandOutput;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.UnixCommandManager;

import java.util.function.Supplier;

/**
 * This class serves as a manifest of information pertaining to this version of Mandate, along
 * with acting as a central factory for CommandManager instances
 * <p>
 * CommandManager instances can be obtained either through the {@link Mandate#newManager() nullary} or {@link Mandate#newManager(Supplier, Supplier, Supplier) ternary}
 * factory methods in this class, which serve to either give default CommandManager implementations or implementations with the specified suppliers
 * of CommandInput and CommandOutput objects for the input, output, and error streams of running commands.
 *
 * @author Foundry
 */
public final class Mandate {

    private Mandate() {
        throw new IllegalStateException(this.getClass().getCanonicalName() + " cannot be instantiated.");
    }

    /**
     * This constructs a new CommandManager instance, conforming with the specification as described by the public API interfaces
     * <p>
     * These instances are not cached, so it is essential that a references to the created instance be maintained for it to be useful. Else, you
     * risk losing the registered commands in the manager.
     * <p>
     * The instance will be constructed with reasonable defaults for its standard input, output, and error streams,
     * reading from {@link System#in System.in} for input and printing to {@link System#out System.out} for output.
     *
     * @return a new CommandManager instance
     */
    public static CommandManager newManager() {
        return new UnixCommandManager();
    }

    /**
     * This constructs a new CommandManager instance, conforming with the specification as described by the public API interfaces
     * <p>
     * These instances are not cached, so it is essential that a references to the created instance be maintained for it to be useful. Else, you
     * risk losing the registered commands in the manager.
     * <p>
     * The instance will be constructed with the guarantee that the provided input, output, and error streams will be used in the command execution
     * process unless they are overridden by piped command input/output or by a call to {@link CommandManager#execute(String, IODescriptor) execute with a specific IODescriptor}
     *
     * @param stdin the supplier of CommandInput objects to be provided to running commands as input streams
     * @param stdout the supplier of CommandOutput objects to be provided to running commands as output streams
     * @param stderr the supplier of CommandOutput objects to be provided to running commands as error streams
     * @return a new CommandManager instance with the specified IO stream suppliers
     */
    public static CommandManager newManager(Supplier<CommandInput> stdin, Supplier<CommandOutput> stdout, Supplier<CommandOutput> stderr) {
        return new UnixCommandManager(stdin, stdout, stderr);
    }

    /**
     * Returns the semantic version number for this copy of Mandate
     * <p>
     * This will be a String with the form of {majorVersion}.{minorVersion}.{patchVersion}, where
     * majorVersion is associated with large or potentially breaking changes to the Mandate public API, minorVersion with
     * feature inclusions or other noticeable changes, and patchVersion with smaller changes, enhancements, and bugfixes
     *
     * @return the semantic version number of this copy of Mandate
     */
    public static String getVersion() {
        return "1.6.3";
    }

    /**
     * Returns a friendly identifier String for this copy of Mandate
     * <p>
     * This will return the same value as {@link Mandate#getVersion() the version accessor}, but prepended
     * with "Mandate" to form a friendlier, more user-readable String
     *
     * @return a friendly identifier String for this copy of Mandate
     */
    public static String getFormattedVersion() {
        return "Mandate v" + getVersion();
    }
}
