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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.execution.executable.transformer.dynamic.ReflectionMethodTransformer;
import pw.stamina.mandate.internal.utils.Primitives;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Foundry
 */
public final class InvokerProxyFactory {

    private static final String GENERATED_CLASS_NAME = "InvokerProxy";

    private static final Map<Class<?>, Consumer<MethodVisitor>> UNBOXING_ACTIONS;

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private InvokerProxyFactory() {}

    public static InvokerProxy makeProxy(Method backingMethod, Object methodParent) {
        Class<?> executorInterface = TransformationTargetFactory.makeMatching(backingMethod);
        String executorJvmName = Type.getInternalName(executorInterface);
        String executorJvmDescriptor = Type.getDescriptor(executorInterface);

        String proxyCanonicalName = makeUniqueName(InvokerProxyFactory.class.getPackage(), backingMethod);
        String proxyJvmName = proxyCanonicalName.replace(".", "/");

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, proxyJvmName, null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(InvokerProxy.class)});

        cw.visitSource("<dynamic>", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "target", executorJvmDescriptor, null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(executorInterface)), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, proxyJvmName, "target", executorJvmDescriptor);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();

        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "execute", Type.getMethodDescriptor(Type.getType(ExitCode.class), Type.getType(IODescriptor.class), Type.getType(Object[].class)), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, proxyJvmName, "target", executorJvmDescriptor);
            mv.visitVarInsn(ALOAD, 1);

            Class<?>[] paramTypes = backingMethod.getParameterTypes();
            for (int i = 1; i < paramTypes.length; i++) {
                mv.visitVarInsn(ALOAD, 2);
                mv.visitLdcInsn(i-1);
                mv.visitInsn(AALOAD);
                if (!paramTypes[i].isPrimitive()){
                    mv.visitTypeInsn(CHECKCAST, Type.getInternalName(paramTypes[i]));
                } else {
                    mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Primitives.wrap(paramTypes[i])));
                    UNBOXING_ACTIONS.get(paramTypes[i]).accept(mv);
                }
            }

            mv.visitMethodInsn(INVOKEINTERFACE, executorJvmName, "execute", Type.getMethodDescriptor(backingMethod), true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(backingMethod.getParameterTypes().length + 2, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        try {
            return (InvokerProxy) ClassLoaderSupport.defineClass(InvokerProxyFactory.class.getClassLoader(), proxyCanonicalName, cw.toByteArray()).getDeclaredConstructor(executorInterface).newInstance(ReflectionMethodTransformer.transform(executorInterface, methodParent.getClass(), methodParent, backingMethod));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new InvokerProxyGenerationException("Exception creating invoker proxy for method '" + backingMethod + "'", e);
        }
    }

    private static String makeUniqueName(Package parentPackage, Method method) {
        return String.format("%s.%s_%d", parentPackage.getName(), GENERATED_CLASS_NAME, NEXT_ID.getAndIncrement());
    }

    static {
        Map<Class<?>, Consumer<MethodVisitor>> actions = new HashMap<>();
        actions.put(Byte.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Byte.class), "byteValue", "()B", false));
        actions.put(Short.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Short.class), "shortValue", "()S", false));
        actions.put(Integer.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Integer.class), "intValue", "()I", false));
        actions.put(Long.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Long.class), "longValue", "()J", false));
        actions.put(Float.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Float.class), "floatValue", "()F", false));
        actions.put(Double.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Double.class), "doubleValue", "()D", false));
        actions.put(Boolean.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false));
        actions.put(Character.TYPE, mv -> mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Character.class), "charValue", "()C", false));
        UNBOXING_ACTIONS = actions;
    }
}