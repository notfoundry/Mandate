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

package pw.stamina.mandate.api.component;

import pw.stamina.mandate.api.execution.CommandExecutable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A syntax component from the syntax hierarchy for a command
 * This serves as a general contract for what a syntax component should know about itself
 * and it's environment
 *
 * @author Foundry
 */
public interface SyntaxComponent {

    /**
     * @return the specific piece of syntax represented by this component
     */
    String getSyntax();

    /**
     * @param syntax the string name of the piece of syntax that should be looked up as a child of this component
     * @return the syntax component corresponding to the syntax string provided if it is a valid child, else null
     */
    SyntaxComponent getChild(String syntax);

    /**
     * Adds a syntax component as a child of this one. Generally, a SyntaxComponent implementation
     * should prevent duplicate pieces of syntax from existing as children
     * @param component the syntax component to be added as a child of this one
     */
    void addChild(SyntaxComponent component);

    /**
     * @return a collection of all children of this syntax component, if any are present
     */
    Optional<Collection<SyntaxComponent>> findChildren();

    /**
     * Adds an executable to the executables coupled with this piece of syntax. Generally, a SyntaxComponent
     * implementation should prevent duplicate executables from being coupled to itself
     * @param executable the executable to couple to this piece of syntax
     */
    void addExecutable(CommandExecutable executable);

    /**
     * @return a set, if any, of all executables coupled to this piece of syntax
     */
    Optional<Set<CommandExecutable>> findExecutables();
}
