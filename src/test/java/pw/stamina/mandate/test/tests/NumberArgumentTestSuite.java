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

package pw.stamina.mandate.test.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.internal.annotations.numeric.IntClamp;
import pw.stamina.mandate.internal.annotations.numeric.PreciseClamp;
import pw.stamina.mandate.internal.annotations.numeric.RealClamp;
import pw.stamina.mandate.io.IODescriptor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mark Johnson
 */
@Syntax(root = {"acceptnumbers"})
public class NumberArgumentTestSuite {
    private final Queue<Object> commandErrors = new ArrayDeque<>();

    private final Queue<Object> commandOutput = new ArrayDeque<>();

    private final CommandContext commandContext = Mandate.newContextBuilder()
            .usingIOEnvironment(Mandate.newIOBuilder()
                    .usingOutputStream(() -> commandOutput::add)
                    .usingErrorStream(() -> commandErrors::add)
                    .build())
            .build();

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(final Throwable e, final Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setup() {
        commandContext.register(this);
    }

    @Test
    public void testPassingWrappedNumbersToCommand() {
        final Execution result = commandContext.execute("acceptnumbers wrapped 32 64 128 256 512.1024 1024.2048");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(6, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals((byte) 32, commandOutput.poll());
        assertEquals((short) 64, commandOutput.poll());
        assertEquals(128, commandOutput.poll());
        assertEquals(256L, commandOutput.poll());
        assertEquals(512.1024F, commandOutput.poll());
        assertEquals(1024.2048D, commandOutput.poll());
    }

    @Test
    public void testPassingUnwrappedNumbersToCommand() {
        final Execution result = commandContext.execute("acceptnumbers unwrapped 32 64 128 256 512.1024 1024.2048");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(6, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals((byte) 32, commandOutput.poll());
        assertEquals((short) 64, commandOutput.poll());
        assertEquals(128, commandOutput.poll());
        assertEquals(256L, commandOutput.poll());
        assertEquals(512.1024F, commandOutput.poll());
        assertEquals(1024.2048D, commandOutput.poll());
    }

    @Test
    public void testPassingPreciseNumbersToCommand() {
        final Execution result = commandContext.execute("acceptnumbers precise 324161900713241619007132416190071324161900713241619007132416190071 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(2, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals(new BigInteger("324161900713241619007132416190071324161900713241619007132416190071"), commandOutput.poll());

        assertEquals(new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823"), commandOutput.poll());
    }

    @Test
    public void testPasssingMixedClampedNumbersToCommand() {
        final Execution result = commandContext.execute("acceptnumbers mixedclamped 32 256 512.1024 4096.8192 324161900713241619007132416190071324161900713241619007132416190071");

        assertTrue(result.result() == ExitCode.SUCCESS);

        assertEquals(5, commandOutput.size());

        assertEquals(0, commandErrors.size());

        assertEquals(24, commandOutput.poll());

        assertEquals(512L, commandOutput.poll());

        assertEquals(384F, commandOutput.poll());

        assertEquals(4096.8192D, commandOutput.poll());

        assertEquals(new BigInteger("324161900713241619007132416190071324161900713241619007132416190071"), commandOutput.poll());
    }

    @Executes(tree = {"wrapped"})
    public ExitCode acceptNumbersWrapped(@Implicit final IODescriptor io, Byte b, Short s, Integer i, Long j, Float f, Double d) {
        io.out().write(b);
        io.out().write(s);
        io.out().write(i);
        io.out().write(j);
        io.out().write(f);
        io.out().write(d);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = {"unwrapped"})
    public ExitCode acceptNumbersUnwrapped(@Implicit final IODescriptor io, byte b, short s, int i, long j, float f, double d) {
        io.out().write(b);
        io.out().write(s);
        io.out().write(i);
        io.out().write(j);
        io.out().write(f);
        io.out().write(d);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = {"precise"})
    public ExitCode acceptNumbersUnwrapped(@Implicit final IODescriptor io, BigInteger bi, BigDecimal bd) {
        io.out().write(bi);
        io.out().write(bd);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = {"mixedclamped"})
    public ExitCode acceptNumbersMixedAndClamped(@Implicit final IODescriptor io,
                                                 @IntClamp(min = 0, max = 24) int i,
                                                 @RealClamp(min = 512, max = 1024) Long l,
                                                 @IntClamp(min = 256, max = 384) float f,
                                                 @RealClamp(min = 4096, max = 8192) Double d,
                                                 @PreciseClamp(min = "8213748312784723184783217423894172384123423142134234") BigInteger bi) {
        io.out().write(i);
        io.out().write(l);
        io.out().write(f);
        io.out().write(d);
        io.out().write(bi);
        return ExitCode.SUCCESS;
    }
}
