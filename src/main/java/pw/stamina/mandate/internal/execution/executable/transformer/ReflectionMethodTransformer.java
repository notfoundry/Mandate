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

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * A utility class allowing for the programmatic conversion of class methods to SAM interface implementations
 * <p>
 * ReflectionMethodTransformer provides methods for converting previously obtained reflection {@link Method method} objects through
 * the {@link ReflectionMethodTransformer#transform(Class, Class, Object, Method, Object...) direct transformer}, as well as
 * method objects discovered via their String name through the {@link ReflectionMethodTransformer#transform(Class, Class, Object, String, Class[]) lookup transformer}.
 * <p>
 * Both the direct and lookup transformers support passing captured arguments as a part of the conversion process through either their default or overloaded method variants.
 * Captured arguments serve as constants references for invocation, meaning that the parameters present in the conversion target type SAM must be compatible
 * with all parameters in the converted method minus the parameters that are having captured arguments supplied to them.
 * <p>
 * The SAM of the conversion target type must have an identical signature to the method being converted discounting captured arguments, with the
 * exception of SAMs making use of parameterized types. The conversion system cannot reify these types prior to invoking the SAM of the transformed object,
 * seeing as the JVM erases these types. As such, care must be taken to not provide incompatable transformation target types when dealing with parameterized arguments,
 * as internally all references to that type will be of type {@link Object Object}, to which any argument can be passed.
 * <p>
 * When this class is initialized, it will attempt to acquire a privileged {@link java.lang.invoke.MethodHandles.Lookup MethodHandle Lookup} object with which
 * private, protected, and package-local methods can be converted. If this conversion fails, the transformer will issue a warning that lookups will be limited to
 * publicly accessible methods.
 *
 * @author Foundry
 */
public final class ReflectionMethodTransformer {
    private static final MethodHandles.Lookup LOOKUP;

    private static final boolean DO_PRIVILEGED_LOOKUPS;

    private ReflectionMethodTransformer() {}

    /**
     * Attempts to generated an instance of the conversion target type serving as an dynamic invoker for a specific reflection method
     * <p>
     * The conversion target must be an interface with one single non-default and non-static method to be considered valid. In other words,
     * the {@link FunctionalInterface FunctionalInterface} annotation should be able to be applied to that interface without error.
     *
     * @param conversionTarget the conversion target to which the backing method should be converted
     * @param parentClass the class type of the parent of the converted method
     * @param parentInstance the object serving as the parent of the converted method, null if the method is static
     * @param backingMethod the method to be converted to the conversion target type
     * @param capturedState the captured state, if any, to be supplied to the converted method upon invocation
     * @param <T> the type of the SAM object to be created
     * @param <K> the type of the parent object in which the converted method is contained
     * @return an object of type T with a single method accepting all non-captured arguments of the converted method
     * @throws IllegalHandleLookupException if the required method handle cannot be looked up due to an access violation
     * @throws MethodTransformationException if there are incompatibilities between the conversion target and the converted method
     */
    @SuppressWarnings("unchecked")
    public static <T, K> T transform(Class<T> conversionTarget, Class<? extends K> parentClass, K parentInstance, Method backingMethod, Object... capturedState) {
        if (parentClass == null) throw new NullPointerException("parent class provided cannot be null");
        validateConversion(conversionTarget, backingMethod, capturedState);    //before we go any further, check certain invariants
        try {
            final MethodHandles.Lookup caller = LOOKUP.in(parentClass);   //transform a new lookup in our parent class
            MethodHandle implMethod; Method lambdaMethod = null;

            for (Method method : conversionTarget.getMethods()) {    //guaranteed that only a single method exists
                if (!method.isDefault() && !Modifier.isStatic(method.getModifiers())) {
                    lambdaMethod = method;
                    break;
                }
            }

            final int methodModifiers = backingMethod.getModifiers();
            if (Modifier.isPrivate(methodModifiers)) {  //begin resolving the MethodHandle based on its protection level
                if (DO_PRIVILEGED_LOOKUPS) {    //if we have access to a privileged lookup object
                    //private static methods don't use invokespecial bytecode, so account for that possibility
                    implMethod = Modifier.isStatic(methodModifiers) ? caller.unreflect(backingMethod) : caller.unreflectSpecial(backingMethod, parentClass);
                } else {
                    throw new IllegalHandleLookupException("Tried to do lookup on private method " + backingMethod.getName() + ", but an access error occurred");
                }
            } else if (Modifier.isProtected(methodModifiers)) {
                if (DO_PRIVILEGED_LOOKUPS) {
                    implMethod = caller.unreflect(backingMethod);
                } else {
                    throw new IllegalHandleLookupException("Tried to do lookup on protected method " + backingMethod.getName() + ", but an access error occurred");
                }
            } else if (Modifier.isPublic(methodModifiers)) {
                implMethod = caller.unreflect(backingMethod);
            } else {    //no explicit access modifier; must be package-local
                if (DO_PRIVILEGED_LOOKUPS) {
                    implMethod = caller.unreflect(backingMethod);
                } else {
                    throw new IllegalHandleLookupException("Tried to do lookup on package-local method " + backingMethod.getName() + ", but an access error occurred");
                }
            }

            if (implMethod != null && lambdaMethod != null) {
                final Class<?>[] erasedBackingParameters = new Class[lambdaMethod.getParameterCount()], capturedStateClasses = flattenObjectsToClasses(capturedState);
                for (int i = 0; i < lambdaMethod.getParameterTypes().length; i++) { //ensure that if the interface method uses any generic types, the created method type is made compatible with them
                    erasedBackingParameters[i] = !backingMethod.getParameterTypes()[i+capturedState.length].isAssignableFrom(lambdaMethod.getParameterTypes()[i])
                            ? Object.class : backingMethod.getParameterTypes()[i+capturedState.length];
                }
                final Class<?> erasedReturnType = backingMethod.getReturnType().isAssignableFrom(lambdaMethod.getReturnType())
                        ? backingMethod.getReturnType() : Object.class; //if the method has a generic return type, erase it

                final MethodType instantiatedMethodType = MethodType.methodType(backingMethod.getReturnType(), stripCapturedTypes(backingMethod.getParameterTypes(), capturedStateClasses));  //generate a type to match the backing method
                final MethodType lambdaMethodType = MethodType.methodType(erasedReturnType, erasedBackingParameters);
                if (Modifier.isStatic(methodModifiers)) {
                    return (T) LambdaMetafactory.metafactory(caller, lambdaMethod.getName(), MethodType.methodType(conversionTarget, stripParameterTypes(backingMethod.getParameterTypes(), capturedStateClasses)),   //the type used for static invocations
                            lambdaMethodType, implMethod, instantiatedMethodType).dynamicInvoker().invokeWithArguments(Arrays.asList(capturedState));   //varargs seems to resolve a passed array as a single object otherwise
                } else {
                    if (parentInstance == null) {   //assert that the backing instance for a non-static method is not null
                        throw new MethodTransformationException("Instance of parent class for non-static method " + backingMethod.getName() + " cannot be null");
                    }
                    return (T) LambdaMetafactory.metafactory(caller, lambdaMethod.getName(), MethodType.methodType(conversionTarget, parentClass, (Class<?>[]) stripParameterTypes(backingMethod.getParameterTypes(), capturedStateClasses)),    //the type used for instance invocations
                            lambdaMethodType, implMethod, instantiatedMethodType).dynamicInvoker().invokeWithArguments(Arrays.asList(arrayConcat(new Object[] {parentInstance}, capturedState)));
                }
            } else {
                throw new IllegalHandleLookupException("Unable to do lookup on method \"" + backingMethod.getName() + "\", insufficient permissions from security manager?");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MethodTransformationException("Exception creating dynamic invoker for method \"" + backingMethod.getName() + "\":" + System.lineSeparator() + e);
        }
    }

    /**
     * Attempts to generated an instance of the conversion target type serving as an dynamic invoker for an unresolved method supplier with captured state
     * <p>
     * The conversion target must be an interface with one single non-default and non-static method to be considered valid. In other words,
     * the {@link FunctionalInterface FunctionalInterface} annotation should be able to be applied to that interface without error.
     * <p>
     * @see ReflectionMethodTransformer#transform(Class, Class, Object, String, Class[], Object...)
     *
     * @param conversionTarget the conversion target to which the backing method should be converted
     * @param parentClass the class type of the parent of the converted method
     * @param parentInstance the object serving as the parent of the converted method, null if the method is static
     * @param methodEquivalent the string name of the method to be converted to the conversion target type
     * @param  methodParameters the method parameter types of the method to be looked up through the methodEquivalent argument
     * @param capturedState the captured state, if any, to be supplied to the converted method upon invocation
     * @param <T> the type of the SAM object to be created
     * @param <K> the type of the parent object in which the converted method is contained
     * @return an object of type T with a single method accepting all non-captured arguments of the converted method
     * @throws IllegalHandleLookupException if the required method handle cannot be looked up due to an access violation
     * @throws MethodTransformationException if there are incompatibilities between the conversion target and the converted method
     */
    public static <T, K> T transform(Class<T> conversionTarget, Class<? extends K> parentClass, K parentInstance, String methodEquivalent, Class<?>[] methodParameters, Object... capturedState) {
        for (Class<?> clazz = parentClass; clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {  //loop through inheritance tree
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodEquivalent) && Arrays.equals(method.getParameterTypes(), methodParameters))   //validate that method signature matches intention
                    return transform(conversionTarget, parentClass, parentInstance, method, capturedState);
            }
        }
        throw new MethodTransformationException("Method " + methodEquivalent
                + " with parameters " + Arrays.toString(methodParameters)
                + " does not exist in class " + parentClass.getSimpleName());
    }

    /**
     * Attempts to generated an instance of the conversion target type serving as an dynamic invoker for an unresolved method with no supplied captured state
     * <p>
     * The conversion target must be an interface with one single non-default and non-static method to be considered valid. In other words,
     * the {@link FunctionalInterface FunctionalInterface} annotation should be able to be applied to that interface without error.
     * <p>
     * @see ReflectionMethodTransformer#transform(Class, Class, Object, String, Class[], Object...)
     *
     * @param conversionTarget the conversion target to which the backing method should be converted
     * @param parentClass the class type of the parent of the converted method
     * @param parentInstance the object serving as the parent of the converted method, null if the method is static
     * @param methodEquivalent the string name of the method to be converted to the conversion target type
     * @param  methodParameters the method parameter types of the method to be looked up through the methodEquivalent argument
     * @param <T> the type of the SAM object to be created
     * @param <K> the type of the parent object in which the converted method is contained
     * @return an object of type T with a single method accepting all non-captured arguments of the converted method
     * @throws IllegalHandleLookupException if the required method handle cannot be looked up due to an access violation
     * @throws MethodTransformationException if there are incompatibilities between the conversion target and the converted method
     */
    public static <T, K> T transform(Class<T> conversionTarget, Class<? extends K> parentClass, K parentInstance, String methodEquivalent, Class<?>... methodParameters) {
        return transform(conversionTarget, parentClass, parentInstance, methodEquivalent, methodParameters, new Object[0]);
    }

    private static void validateConversion(Class<?> conversionTarget, Method methodEquivalent, Object... capturedState) throws MethodTransformationException {
        if (conversionTarget == null) throw new MethodTransformationException("Lambda interface cannot be null");
        if (methodEquivalent == null) throw new MethodTransformationException("Method equivalent cannot be null");

        if (!conversionTarget.isInterface()) {   //basic check to ensure that you are dealing with an interface
            throw new MethodTransformationException("Class " + conversionTarget.getSimpleName()
                    + " is not an interface and cannot be used as a lambda target");
        }
        Method functionalMethod = null;
        for (Method method : conversionTarget.getMethods()) {
            if (!method.isDefault() && !Modifier.isStatic(method.getModifiers())) {  //check to make sure that we aren't dealing with a default method
                if (functionalMethod == null) { //first non-default method found
                    functionalMethod = method;
                } else {   //more than one non-default method exists in interface
                    throw new MethodTransformationException("Interface " + conversionTarget.getSimpleName()
                            + "has more than one non-default method and cannot be used as a lambda target");
                }
            }
        }

        if (functionalMethod == null) {   //interface has no non-default methods
            throw new MethodTransformationException("Interface " + conversionTarget.getSimpleName() + " does not have any non-default methods");
        } else if (!functionalMethod.getReturnType().isAssignableFrom(methodEquivalent.getReturnType())) { //sanity check for incompatible return types
            throw new MethodTransformationException("Incompatible methods: backing method " + methodEquivalent.getName()
                    + " returns " + methodEquivalent.getReturnType()
                    + " while interface method " + functionalMethod.getName()
                    + " returns " + functionalMethod.getReturnType());
        } else if (functionalMethod.getParameterCount() != methodEquivalent.getParameterCount() - capturedState.length) {  //sanity check for incompatible parameter counts
            throw new MethodTransformationException("Incompatible methods: backing method " + methodEquivalent.getName()
                    + " takes " + methodEquivalent.getParameterCount() + " arguments while interface method " + functionalMethod.getName()
                    + " takes " + functionalMethod.getParameterCount() + " arguments");
        }

        Class<?>[] lambdaParams = functionalMethod.getParameterTypes(), methodParams = methodEquivalent.getParameterTypes();
        for (int i = 0; i < capturedState.length; i++) {    //check for compatible method signatures inside capture
            if (!methodParams[i].isAssignableFrom(capturedState[i].getClass())) {   //ensure the two parameters are interoperable
                throw new MethodTransformationException("Incompatible capture: parameter " + i + " of backing method " + methodEquivalent.getName()
                        + " is of type " + methodParams[i].getSimpleName()
                        + " but is supplied captured argument of type " + capturedState[i].getClass().getSimpleName());
            }
        }
        for (int i = 0; i < lambdaParams.length; i++) {    //check for compatible method signatures past capture
            if (!lambdaParams[i].isAssignableFrom(methodParams[i+capturedState.length])) {   //ensure the two parameters are interoperable
                throw new MethodTransformationException("Incompatible methods: parameter " + i+capturedState.length + " of backing method " + methodEquivalent.getName()
                        + " is of type " + methodParams[i+capturedState.length].getSimpleName()
                        + " while parameter " + i + " of interface method " + functionalMethod.getName()
                        + " is of type " + lambdaParams[i].getSimpleName());
            }
        }
    }

    private static Class<?>[] flattenObjectsToClasses(Object[] objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    private static Class<?>[] stripCapturedTypes(Class<?>[] parameterTypes, Class<?>... captureTypes) {
        return Arrays.copyOfRange(parameterTypes, captureTypes.length, parameterTypes.length);
    }

    private static Class<?>[] stripParameterTypes(Class<?>[] parameterTypes, Class<?>... captureTypes) {
        return Arrays.copyOfRange(parameterTypes, 0, captureTypes.length);
    }

    private static Object[] arrayConcat(Object[] first, Object[] second) {
        Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    static {
        MethodHandles.Lookup lookupObject;
        boolean hasPrivilegedLookup;
        try {
            Field lookupImplField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"); //try to get the trusted Lookup object
            lookupImplField.setAccessible(true);    //attempt to crack the field
            lookupObject = (MethodHandles.Lookup) lookupImplField.get(null);    //success!
            hasPrivilegedLookup = true;
        } catch (ReflectiveOperationException e) {  //something prevented us from accessing the trusted Lookup object
            System.err.println("(LambdaFactory) (Warning) Could not acquire privileged lookup object; lambda factory usage will be limited to public methods");
            lookupObject = MethodHandles.lookup();  //failure, unprivileged lookups only
            hasPrivilegedLookup = false;
        }

        LOOKUP = lookupObject;
        DO_PRIVILEGED_LOOKUPS = hasPrivilegedLookup;
    }
}
