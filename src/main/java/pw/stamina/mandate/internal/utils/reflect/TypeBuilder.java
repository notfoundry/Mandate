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

package pw.stamina.mandate.internal.utils.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Foundry
 */
public final class TypeBuilder {

    private TypeBuilder() {}

    public static Type from(final Class<?> rawType, final Type... typeParameters) {
        Objects.requireNonNull(rawType, "Raw type cannot be null");
        Objects.requireNonNull(typeParameters, "Type parameters cannot be null");

        final TypeVariable<?>[] typeVariablesForType = rawType.getTypeParameters();
        if (typeVariablesForType.length != typeParameters.length) {
            throw new IllegalArgumentException(String.format("Tried to pass %d type parameters to type %s (%d type parameters)",
                    typeParameters.length, rawType.getCanonicalName(), rawType.getTypeParameters().length));
        }

        if (typeParameters.length > 0) {
            for (int i = 0; i < typeParameters.length; i++) {
                Objects.requireNonNull(typeParameters[i], "Type parameters cannot be null");
                final Type[] typeBounds = typeVariablesForType[i].getBounds();
                for (final Type bound : typeBounds) {
                    checkTypeBounds(bound, typeParameters[i]);
                }
            }
            return new ConstructedParameterizedType(rawType, typeParameters);
        } else {
            return rawType;
        }
    }

    private static void checkTypeBounds(Type boundary, Type attempt) {
        if (boundary instanceof Class) {
            if (attempt instanceof Class) {
                if (!((Class<?>) boundary).isAssignableFrom((Class<?>) attempt)) {
                    throw new IllegalArgumentException("");
                }
            } else if (attempt instanceof ParameterizedType) {
                if (!((Class<?>) boundary).isAssignableFrom((Class<?>) ((ParameterizedType) attempt).getRawType())) {
                    throw new IllegalArgumentException("");
                }
            } else if (attempt instanceof WildcardType) {
                for (final Type type : ((WildcardType) attempt).getUpperBounds()) {
                    if (type instanceof Class) {
                        if (!((Class<?>) boundary).isAssignableFrom((Class<?>) type)) {
                            throw new IllegalArgumentException("");
                        }
                    } else if (type instanceof ParameterizedType) {
                        if (!((Class<?>) boundary).isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType())) {
                            throw new IllegalArgumentException("");
                        }
                    }
                }
                for (final Type type : ((WildcardType) attempt).getLowerBounds()) {
                    if (type instanceof Class) {
                        if (!((Class<?>) type).isAssignableFrom((Class<?>) boundary)) {
                            throw new IllegalArgumentException("");
                        }
                    } else if (type instanceof ParameterizedType) {
                        if (!((Class<?>) ((ParameterizedType) type).getRawType()).isAssignableFrom((Class<?>) boundary)) {
                            throw new IllegalArgumentException("");
                        }
                    }
                }
            }
        } else if (boundary instanceof ParameterizedType) {
            if (attempt instanceof Class) {
                if (!((Class<?>) ((ParameterizedType) boundary).getRawType()).isAssignableFrom((Class<?>) attempt)) {
                    throw new IllegalArgumentException("");
                }
            }
        }
    }

    private static class ConstructedParameterizedType implements ParameterizedType {
        private final Type[] actualTypeArguments;

        private final Class<?> rawType;

        private final Type ownerType;

        ConstructedParameterizedType(final Class<?> rawType, final Type[] actualTypeArguments) {
            this.actualTypeArguments = actualTypeArguments;
            this.rawType = rawType;
            this.ownerType = rawType.getDeclaringClass();
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        public boolean equals(final Object o) {
            if (o instanceof ParameterizedType) {
                final ParameterizedType type = (ParameterizedType)o;
                if (this == type) {
                    return true;
                } else {
                    final Type ownerType = type.getOwnerType();
                    final Type rawType = type.getRawType();
                    return Objects.equals(this.ownerType, ownerType) && Objects.equals(this.rawType, rawType) && Arrays.equals(this.actualTypeArguments, type.getActualTypeArguments());
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
        }

        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if( this.ownerType != null) {
                if (this.ownerType instanceof Class) {
                    sb.append(((Class) this.ownerType).getName());
                } else {
                    sb.append(this.ownerType.toString());
                }

                sb.append(".");
                sb.append(this.rawType.getName());
            } else {
                sb.append(this.rawType.getName());
            }

            if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
                sb.append("<");
                boolean var2 = true;
                for (final Type typeArgument : this.actualTypeArguments) {
                    if (!var2) {
                        sb.append(", ");
                    }

                    sb.append(typeArgument.getTypeName());
                    var2 = false;
                }

                sb.append(">");
            }

            return sb.toString();
        }
    }
}
