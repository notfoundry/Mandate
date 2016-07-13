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

package pw.stamina.mandate.internal.execution.result;

import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.execution.executable.transformer.InvokerProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Foundry
 */
public final class ExecutionFactory {

    private static final Map<Class<?>, Function<Object, Function<Object, Function<IODescriptor, Function<Object[], Execution>>>>> EXECUTION_SUPPLIERS;

    private ExecutionFactory() {}

    public static <T> Execution makeExecution(T executable, Object parent, IODescriptor io, Object[] args) {
        Execution execution; Class<?> executableClass;
        Function<Object, Function<Object, Function<IODescriptor, Function<Object[], Execution>>>> lookup;
        if ((lookup = EXECUTION_SUPPLIERS.get((executableClass = executable.getClass()))) == null) {
            for (Map.Entry<Class<?>, Function<Object, Function<Object, Function<IODescriptor, Function<Object[], Execution>>>>> e : EXECUTION_SUPPLIERS.entrySet()) {
                if (e.getKey().isAssignableFrom(executableClass)) {
                    lookup = e.getValue();
                    break;
                }
            }
            if (lookup == null) {
                throw new IllegalArgumentException("Executables of type '" + executable.getClass().getCanonicalName() + "' are not supported at this time");
            }
        }
        return lookup.apply(executable).apply(parent).apply(io).apply(args);
    }

    static {
        Map<Class<?>, Function<Object, Function<Object, Function<IODescriptor, Function<Object[], Execution>>>>> suppliers = new HashMap<>();
        suppliers.put(Method.class, executable -> parent -> io -> args -> new AsynchronousMethodExecution((Method) executable, parent, io, args));
        suppliers.put(InvokerProxy.class, executable -> parent -> io -> args -> new AsynchronousTransformerExecution((InvokerProxy) executable, io, args));
        EXECUTION_SUPPLIERS = suppliers;
    }
}
