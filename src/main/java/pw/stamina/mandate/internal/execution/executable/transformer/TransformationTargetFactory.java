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
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Foundry
 */
public final class TransformationTargetFactory {

    private static final String GENERATED_CLASS_NAME = "TransformationTarget";

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private TransformationTargetFactory() {}

    public static Class<?> makeMatching(Method method) {
        ClassWriter cw = new ClassWriter(0);
        String className = makeUniqueName(TransformationTargetFactory.class.getPackage(), method);

        cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, className.replace(".", "/"), null, Type.getInternalName(Object.class), null);

        cw.visitSource("<dynamic>", null);

        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "execute", Type.getMethodDescriptor(method), null, null).visitEnd();

        cw.visitEnd();

        return SystemClassLoader.defineClass(className, cw.toByteArray());
    }

    private static String makeUniqueName(Package parentPackage, Method method) {
        return String.format("%s.%s_%d", parentPackage.getName(), GENERATED_CLASS_NAME, NEXT_ID.getAndIncrement());
    }
}
