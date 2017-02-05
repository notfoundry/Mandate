/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2017 Mark Johnson
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

package pw.stamina.mandate.internal.execution.executable.invoker;

import pw.stamina.mandate.execution.result.ExitCode;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Mark Johnson
 */
public class ReflectionMethodInvokerProxy implements CommandInvoker {

    private final Method backingMethod;

    private final Object methodParent;

    public ReflectionMethodInvokerProxy(final Method backingMethod, final Object methodParent) {
        this.backingMethod = backingMethod;
        this.methodParent = methodParent;
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            this.backingMethod.setAccessible(true);
            return null;
        });
    }

    @Override
    public ExitCode invoke(final Object... arguments) throws Exception {
        return (ExitCode) backingMethod.invoke(methodParent, arguments);
    }
}
