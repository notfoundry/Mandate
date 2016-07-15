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
import pw.stamina.mandate.internal.UnixCommandManager;

import java.util.function.Supplier;

/**
 * @author Foundry
 */
public final class Mandate {
    private Mandate() {
        throw new IllegalStateException(this.getClass().getCanonicalName() + " cannot be instantiated.");
    }

    public static CommandManager newManager() {
        return new UnixCommandManager();
    }

    public static CommandManager newManager(Supplier<CommandInput> stdin, Supplier<CommandOutput> stdout, Supplier<CommandOutput> stderr) {
        return new UnixCommandManager(stdin, stdout, stderr);
    }

    public static String getVersion() {
        return "1.6.0";
    }

    public static String getFormattedVersion() {
        return "Mandate v" + getVersion();
    }
}
