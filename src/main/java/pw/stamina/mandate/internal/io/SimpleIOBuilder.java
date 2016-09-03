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
import pw.stamina.mandate.io.IOBuilder;
import pw.stamina.mandate.io.IOEnvironment;
import pw.stamina.mandate.internal.io.streams.StandardErrorStream;
import pw.stamina.mandate.internal.io.streams.StandardInputStream;
import pw.stamina.mandate.internal.io.streams.StandardOutputStream;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Mark Johnson
 */
public class SimpleIOBuilder implements IOBuilder {

    private Supplier<CommandInput> inputStreamSupplier;

    private Supplier<CommandOutput> outputStreamSupplier;

    private Supplier<CommandOutput> errorStreamSupplier;

    @Override
    public IOBuilder usingInputStream(final Supplier<CommandInput> inputStreamSupplier) {
        checkPrecondition(this.inputStreamSupplier == null, "Standard input stream supplier already provided");
        this.inputStreamSupplier = inputStreamSupplier;
        return this;
    }

    @Override
    public IOBuilder usingOutputStream(final Supplier<CommandOutput> outputStreamSupplier) {
        checkPrecondition(this.outputStreamSupplier == null, "Standard output stream supplier already provided");
        this.outputStreamSupplier = outputStreamSupplier;
        return this;
    }

    @Override
    public IOBuilder usingErrorStream(final Supplier<CommandOutput> errorStreamSupplier) {
        checkPrecondition(this.errorStreamSupplier == null, "Standard error stream supplier already provided");
        this.errorStreamSupplier = errorStreamSupplier;
        return this;
    }

    @Override
    public IOEnvironment build() {
        return new SimpleIOEnvironment(
                Optional.ofNullable(inputStreamSupplier).orElseGet(() -> StandardInputStream::get),
                Optional.ofNullable(outputStreamSupplier).orElseGet(() -> StandardOutputStream::get),
                Optional.ofNullable(errorStreamSupplier).orElseGet(() -> StandardErrorStream::get)
        );
    }

    private static void checkPrecondition(final boolean assertion, final String failureMessage) {
        if (!assertion) throw new IllegalStateException(failureMessage);
    }
}
