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

package pw.stamina.mandate.internal.security;

import pw.stamina.mandate.security.CommandSender;
import pw.stamina.mandate.security.Permission;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Mark Johnson
 */
public class SimpleCommandSender implements CommandSender {

    private final Set<Permission> permissions;

    public SimpleCommandSender(final Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (permissions.contains(permission)) {
            return true;
        }
        final List<String> lookupPermissionBlocks = Arrays.asList(permission.getRawName().split(Pattern.quote(".")));
        for (final Permission presentPermission : permissions) {
            final List<String> presentPermissionBlocks = Arrays.asList(presentPermission.getRawName().split(Pattern.quote(".")));
            if (arePermissionBlocksCompatible(lookupPermissionBlocks.iterator(), presentPermissionBlocks.iterator())) {
               return true;
            }
        }
        return false;
    }

    private static boolean arePermissionBlocksCompatible(final Iterator<String> lookupIterator, final Iterator<String> presentIterator) {
        while (lookupIterator.hasNext() && presentIterator.hasNext()) {
            final String lookupBlock = lookupIterator.next();
            final String presentBlock = presentIterator.next();

            if (!lookupBlock.equals(presentBlock) && !presentBlock.equals(Permission.WILDCARD.getRawName())) {
                return false;
            }
        }
        return true;
    }

}
