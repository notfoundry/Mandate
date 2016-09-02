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

package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.io.CommandInput;
import pw.stamina.mandate.io.CommandOutput;
import pw.stamina.mandate.io.IODescriptor;

/**
 * @author Foundry
 */
public final class SimpleIODescriptor implements IODescriptor {
    private final CommandInput in;
    private final CommandOutput out;
    private final CommandOutput err;

    public SimpleIODescriptor(final CommandInput in, final CommandOutput out, final CommandOutput err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    @Override
    public CommandInput in() {
        return in;
    }

    @Override
    public CommandOutput out() {
        return out;
    }

    @Override
    public CommandOutput err() {
        return err;
    }
}
