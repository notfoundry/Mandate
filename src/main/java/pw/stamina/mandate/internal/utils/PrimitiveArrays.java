package pw.stamina.mandate.internal.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Foundry
 */
public final class PrimitiveArrays {
    private static final Map<Class, Class> UNWRAPPED_TO_WRAPPED_CACHE = new HashMap<>();
    private static final Map<Class, Class> WRAPPED_TO_UNWRAPPED_CACHE = new HashMap<>();

    private PrimitiveArrays() {}

    public static Class wrap(Class unwrapped) {
        return UNWRAPPED_TO_WRAPPED_CACHE.computeIfAbsent(Primitives.wrap(getBaseComponentType(validateIsArray(unwrapped))), componentType -> Array.newInstance(componentType, getDimensions(unwrapped)).getClass());
    }

    public static Class unwrap(Class wrapped) {
        return WRAPPED_TO_UNWRAPPED_CACHE.computeIfAbsent(Primitives.unwrap(getBaseComponentType(validateIsArray(wrapped))), componentType -> Array.newInstance(componentType, getDimensions(wrapped)).getClass());
    }

    private static Class validateIsArray(Class arrayClass) {
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
