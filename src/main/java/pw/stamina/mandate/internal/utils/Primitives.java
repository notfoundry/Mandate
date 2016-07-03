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
    BYTE(Byte.TYPE, Byte.class),
    SHORT(Short.TYPE, Short.class),
    INTEGER(Integer.TYPE, Integer.class),
    LONG(Long.TYPE, Long.class),
    FLOAT(Float.TYPE, Float.class),
    DOUBLE(Double.TYPE, Double.class),
    BOOLEAN(Boolean.TYPE, Boolean.class),
    CHAR(Character.TYPE, Character.class),
    VOID(Void.TYPE, void.class);

    private static final Map<Class, Class> UNWRAPPED_TO_WRAPPED = new HashMap<>();
    private static final Map<Class, Class> WRAPPED_TO_UNWRAPPED = new HashMap<>();

    private final Class unwrapped;
    private final Class wrapped;

    Primitives(Class unwrapped, Class wrapped) {
        this.unwrapped = unwrapped;
        this.wrapped = wrapped;
    }

    public static Class wrap(Class unwrapped) {
        final Class wrapped = Primitives.UNWRAPPED_TO_WRAPPED.get(unwrapped);
        return (wrapped != null) ? wrapped : unwrapped;
    }

    public static Class unwrap(Class wrapped) {
        final Class unwrapped = Primitives.WRAPPED_TO_UNWRAPPED.get(wrapped);
        return (unwrapped != null) ? unwrapped : wrapped;
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

