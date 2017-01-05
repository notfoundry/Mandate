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

package pw.stamina.mandate;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.ConfigurationBuilder;
import pw.stamina.mandate.execution.ContextBuilder;
import pw.stamina.mandate.execution.argument.ArgumentHandlerRegistryBuilder;
import pw.stamina.mandate.execution.argument.ArgumentProviderBuilder;
import pw.stamina.mandate.internal.execution.SimpleConfigurationBuilder;
import pw.stamina.mandate.internal.execution.argument.SimpleArgumentHandlerRegistryBuilder;
import pw.stamina.mandate.internal.execution.argument.implicit.SimpleArgumentProviderBuilder;
import pw.stamina.mandate.io.IOBuilder;
import pw.stamina.mandate.internal.execution.SimpleContextBuilder;
import pw.stamina.mandate.internal.io.SimpleIOBuilder;

/**
 * This class serves as a manifest of information pertaining to this version of Mandate, along
 * with acting as a central factory for CommandContext instances
 * <p>
 * CommandContext instances can be obtained either through the {@link Mandate#newContext() nullary} or {@link Mandate#newContext() ternary}
 * factory methods in this class, which serve to either give default CommandContext implementations or implementations with the specified suppliers
 * of CommandInput and CommandOutput objects for the input, output, and error streams of running commands.
 *
 * @author Mark Johnson
 */
public final class Mandate {

    private Mandate() {
        throw new IllegalStateException(this.getClass().getCanonicalName() + " cannot be instantiated.");
    }

    /**
     * This constructs a new CommandContext instance, conforming with the specification as described by the public API interfaces
     * <p>
     * These instances are not cached, so it is essential that a references to the created instance be maintained for it to be useful. Else, you
     * risk losing the registered commands in the manager.
     * <p>
     * The instance will be constructed with reasonable defaults for its standard input, output, and error streams,
     * reading from {@link System#in System.in} for input and printing to {@link System#out System.out} for output.
     *
     * @return a new CommandContext instance
     */
    public static CommandContext newContext() {
        return newContextBuilder()
                .usingIOEnvironment(newIOBuilder()
                        .build())
                .usingConfiguration(newConfigurationBuilder()
                        .build())
                .withHandlerRegistry(newHandlerRegistryBuilder()
                        .build())
                .withArgumentProvider(newArgumentProviderBuilder()
                        .build())
                .build();
    }

    public static ContextBuilder newContextBuilder() {
        return new SimpleContextBuilder();
    }

    public static IOBuilder newIOBuilder() {
        return new SimpleIOBuilder();
    }

    public static ConfigurationBuilder newConfigurationBuilder() {
        return new SimpleConfigurationBuilder();
    }

    public static ArgumentHandlerRegistryBuilder newHandlerRegistryBuilder() {
        return new SimpleArgumentHandlerRegistryBuilder();
    }

    public static ArgumentProviderBuilder newArgumentProviderBuilder() {
        return new SimpleArgumentProviderBuilder();
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
        return "2.3.0";
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
