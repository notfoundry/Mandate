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

package pw.stamina.mandate.internal.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Foundry
 */
public enum Primitives {
    BYTE(Byte.TYPE, Byte.class, 'B'),
    SHORT(Short.TYPE, Short.class, 'S'),
    INTEGER(Integer.TYPE, Integer.class, 'I'),
    LONG(Long.TYPE, Long.class, 'J'),
    FLOAT(Float.TYPE, Float.class, 'F'),
    DOUBLE(Double.TYPE, Double.class, 'D'),
    BOOLEAN(Boolean.TYPE, Boolean.class, 'Z'),
    CHAR(Character.TYPE, Character.class, 'C'),
    VOID(Void.TYPE, void.class, 'V');

    private static final Map<Class, Class> UNWRAPPED_TO_WRAPPED = new HashMap<>();
    private static final Map<Class, Class> WRAPPED_TO_UNWRAPPED = new HashMap<>();

    private final Class unwrapped;
    private final Class wrapped;
    private final char jvmName;

    Primitives(Class unwrapped, Class wrapped, char jvmName) {
        this.unwrapped = unwrapped;
        this.wrapped = wrapped;
        this.jvmName = jvmName;
    }

    public Class unwrapped() {
        return unwrapped;
    }

    public Class wrapped() {
        return wrapped;
    }

    public char jvmName() {
        return jvmName;
    }

    public static Class wrap(Class unwrapped) {
        final Class wrapped = Primitives.UNWRAPPED_TO_WRAPPED.get(unwrapped);
        return (wrapped != null) ? wrapped : unwrapped;
    }

    public static Class unwrap(Class wrapped) {
        final Class unwrapped = Primitives.WRAPPED_TO_UNWRAPPED.get(wrapped);
        return (unwrapped != null) ? unwrapped : wrapped;
    }

    public static char jvmName(Class unwrapped) {
        if (unwrapped.isPrimitive()) {
            for (Primitives p : values()) {
                if (p.unwrapped == unwrapped) return p.jvmName;
            }
        }
        throw new IllegalArgumentException("Class " + unwrapped.getCanonicalName() + " is not a primitive");
    }

    static {
        Primitives[] values;
        for (int i = 0; i < (values = values()).length; i++) {
            final Primitives primitive = values[i];
            Primitives.UNWRAPPED_TO_WRAPPED.put(primitive.unwrapped, primitive.wrapped);
            Primitives.WRAPPED_TO_UNWRAPPED.put(primitive.wrapped, primitive.unwrapped);
        }
    }
}

