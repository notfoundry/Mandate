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

package pw.stamina.mandate.internal.syntax.component;

import pw.stamina.mandate.syntax.SyntaxTree;
import pw.stamina.mandate.execution.executable.CommandExecutable;

import java.util.*;

/**
 * @author Mark Johnson
 */
class BaseSyntaxTree implements SyntaxTree {
    private Map<String, SyntaxTree> childMap;
    private final String syntax;
    private Set<CommandExecutable> executables;

    BaseSyntaxTree(final String syntax) {
        this(syntax, new CommandExecutable[0]);
    }

    BaseSyntaxTree(final String syntax, final CommandExecutable... executables) {
        this.syntax = syntax;
        if (executables.length > 0) {
            this.executables = new HashSet<>(Arrays.asList(executables));
        }
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

    @Override
    public Optional<SyntaxTree> findChild(final String syntax) {
        return childMap != null ? Optional.ofNullable(childMap.get(syntax)) : Optional.empty();
    }

    @Override
    public void addChild(final SyntaxTree component) {
        if (childMap == null) childMap = new LinkedHashMap<>();
        childMap.put(component.getSyntax(), component);
    }

    @Override
    public Optional<Set<SyntaxTree>> findChildren() {
        return Optional.ofNullable(childMap).map(children -> new HashSet<>(children.values())).map(Collections::unmodifiableSet);
    }

    @Override
    public void addExecutable(final CommandExecutable executable) {
        if (executables == null) executables = new HashSet<>();
        executables.add(executable);
    }

    @Override
    public Optional<Set<CommandExecutable>> findExecutables() {
        return Optional.ofNullable(executables);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BaseSyntaxTree that = (BaseSyntaxTree) o;
        return Objects.equals(syntax, that.syntax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(syntax);
    }

    @Override
    public String toString() {
        return String.format("BaseSyntaxTree{syntax=%s, children=%s, executables=%s}", syntax, childMap.values(), executables);
    }
}
