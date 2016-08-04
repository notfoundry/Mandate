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

package pw.stamina.mandate.internal.execution.executable.transformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Foundry
 */
final class ClassLoaderSupport {

    private static final Method CLASS_LOADER_DEFINER;

    private ClassLoaderSupport() {}

    static Class<?> defineClass(ClassLoader classLoader, String className, byte[] bytes) {
        Object[] args = new Object[] {className, bytes, 0, bytes.length};
        try {
            return (Class) CLASS_LOADER_DEFINER.invoke(classLoader, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TransformationTargetGenerationException("Exception defining class '" + className + "'", e);
        }
    }

    static {
        Method m = null;
        try {
            final Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            m = method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        CLASS_LOADER_DEFINER = m;
    }
}
