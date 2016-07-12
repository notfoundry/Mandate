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

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import tests.*;

/**
 * @author Foundry
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(ClassSyntaxInheritanceTest.class, ClassSyntaxOverrideTest.class, OptionalCommandArgumentTest.class, CommandFlagTest.class, OverlappingCommandFlagTest.class, ExclusiveCommandFlagTest.class, ArrayArgumentTest.class);

        System.out.println();
        result.getFailures().forEach(System.out::println);

        System.err.println(String.format("%d tests completed in %dms: %d passed, %d failed", result.getRunCount(), result.getRunTime(), result.getRunCount() - result.getFailureCount(), result.getFailureCount()));
    }
}
