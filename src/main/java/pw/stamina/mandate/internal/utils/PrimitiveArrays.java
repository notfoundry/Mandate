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

package pw.stamina.mandate.internal.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mark Johnson
 */
public final class PrimitiveArrays {
    private static final Map<Class, Class> UNWRAPPED_TO_WRAPPED_CACHE = new HashMap<>();
    private static final Map<Class, Class> WRAPPED_TO_UNWRAPPED_CACHE = new HashMap<>();

    private PrimitiveArrays() {}

    public static Class wrap(final Class unwrapped) {
        return UNWRAPPED_TO_WRAPPED_CACHE.computeIfAbsent(Primitives.wrap(getBaseComponentType(validateIsArray(unwrapped))), componentType -> Array.newInstance(componentType, getDimensions(unwrapped)).getClass());
    }

    public static Class unwrap(final Class wrapped) {
        return WRAPPED_TO_UNWRAPPED_CACHE.computeIfAbsent(Primitives.unwrap(getBaseComponentType(validateIsArray(wrapped))), componentType -> Array.newInstance(componentType, getDimensions(wrapped)).getClass());
    }

    private static <T> Class<T> validateIsArray(final Class<T> arrayClass) {
        if (!arrayClass.isArray()) {
            throw new IllegalArgumentException("Class \"" + arrayClass.getSimpleName() + "\" is not an array type");
        }
        return arrayClass;
    }

    public static Class getBaseComponentType(Class arrayClass) {
        while (arrayClass.isArray()) {
            arrayClass = arrayClass.getComponentType();
        }
        return arrayClass;
    }

    public static int[] getDimensions(Class arrayClass) {
        int dim = 0;
        while (arrayClass.isArray()) {
            arrayClass = arrayClass.getComponentType();
            dim++;
        }
        return new int[dim];
    }
}
