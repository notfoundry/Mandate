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
import pw.stamina.mandate.io.IOEnvironment;
import pw.stamina.mandate.internal.io.streams.StandardErrorStream;
import pw.stamina.mandate.internal.io.streams.StandardInputStream;
import pw.stamina.mandate.internal.io.streams.StandardOutputStream;

import java.util.function.Supplier;

/**
 * @author Foundry
 */
public enum DefaultIOEnvironment implements IOEnvironment {
    INSTANCE;

    @Override
    public Supplier<CommandInput> in() {
        return StandardInputStream::get;
    }

    @Override
    public Supplier<CommandOutput> out() {
        return StandardOutputStream::get;
    }

    @Override
    public Supplier<CommandOutput> err() {
        return StandardErrorStream::get;
    }

    @Override
    public Supplier<IODescriptor> descriptorFactory() {
        return () -> new SimpleIODescriptor(in().get(), out().get(), err().get());
    }

    public static DefaultIOEnvironment getInstance() {
        return INSTANCE;
    }
}
