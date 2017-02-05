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

package pw.stamina.mandate.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mark Johnson
 */
public final class Permission {

    public static final Permission WILDCARD = new Permission("*");

    private static final Map<String, Permission> PERMISSION_CACHE = new HashMap<>();

    private final String permission;

    private Permission(final String permission) {
        this.permission = permission;
    }

    public String getRawName() {
        return permission;
    }

    public static Permission of(final String permission) {
        return PERMISSION_CACHE.computeIfAbsent(permission, Permission::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Permission that = (Permission) o;
        return Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permission='" + permission + '\'' +
                '}';
    }
}
