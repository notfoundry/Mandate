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

package pw.stamina.mandate.internal.io;

import pw.stamina.mandate.io.CommandInput;
import pw.stamina.mandate.io.CommandOutput;
import pw.stamina.mandate.io.IODescriptor;
import pw.stamina.mandate.io.IOEnvironment;

import java.util.function.Supplier;

/**
 * @author Mark Johnson
 */
public class SimpleIOEnvironment implements IOEnvironment {

    private final Supplier<CommandInput> stdin;

    private final Supplier<CommandOutput> stdout;

    private final Supplier<CommandOutput> stderr;

    public SimpleIOEnvironment(final Supplier<CommandInput> stdin, final Supplier<CommandOutput> stdout, final Supplier<CommandOutput> stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    @Override
    public Supplier<CommandInput> in() {
        return stdin;
    }

    @Override
    public Supplier<CommandOutput> out() {
        return stdout;
    }

    @Override
    public Supplier<CommandOutput> err() {
        return stderr;
    }

    @Override
    public Supplier<IODescriptor> descriptorFactory() {
        return () -> new SimpleIODescriptor(in().get(), out().get(), err().get());
    }
}
