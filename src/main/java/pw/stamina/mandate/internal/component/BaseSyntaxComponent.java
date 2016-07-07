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

package pw.stamina.mandate.internal.component;

import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.execution.CommandExecutable;

import java.util.*;

/**
 * @author Foundry
 */
public class BaseSyntaxComponent implements SyntaxComponent {
    private  Map<String, SyntaxComponent> childMap;
    private final String syntax;
    private Set<CommandExecutable> executables;

    public BaseSyntaxComponent(String syntax) {
        this(syntax, new CommandExecutable[0]);
    }

    public BaseSyntaxComponent(String syntax, CommandExecutable... executables) {
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
    public SyntaxComponent getChild(String syntax) {
        return childMap != null ? childMap.get(syntax) : null;
    }

    @Override
    public void addChild(SyntaxComponent component) {
        if (childMap == null) childMap = new LinkedHashMap<>();
        childMap.put(component.getSyntax(), component);
    }

    @Override
    public Optional<Collection<SyntaxComponent>> findChildren() {
        return Optional.ofNullable(childMap).map(Map::values).map(Collections::unmodifiableCollection);
    }

    @Override
    public void addExecutable(CommandExecutable executable) {
        if (executables == null) executables = new HashSet<>();
        executables.add(executable);
    }

    @Override
    public Optional<Set<CommandExecutable>> findExecutables() {
        return Optional.ofNullable(executables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSyntaxComponent that = (BaseSyntaxComponent) o;
        return Objects.equals(syntax, that.syntax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(syntax);
    }

    @Override
    public String toString() {
        return String.format("BaseSyntaxComponent{syntax=%s, children=%s, executables=%s}", syntax, childMap.values(), executables);
    }
}
